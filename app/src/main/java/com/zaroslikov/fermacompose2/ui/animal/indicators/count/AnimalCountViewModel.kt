package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.add.TitleAndSuffixDomain
import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

@HiltViewModel
class AnimalCountViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalRepository: AnimalRepository,
    private val resourceProvider: ResourceProvider,
    private val warehouseRepository: WarehouseRepository
) : EntryNewViewModel<AnimalCountState, AnimalCountIntent>(AnimalCountState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemId])
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemIdPT])
    private var confirmDelete = CompletableDeferred<Boolean?>()
    override fun insert() {
        TODO("Not yet implemented")
    }

    override fun update() {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        TODO("Not yet implemented")
    }
    override fun validation() {
        TODO("Not yet implemented")
    }

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    override fun onIntent(intent: AnimalCountIntent) {
        return when (intent) {
            // Animal Card
            is AnimalCountIntent.DialogSoloClicked -> updateSoloDialog(intent.value)
            is AnimalCountIntent.SexClicked -> updateSex(intent.value)
            AnimalCountIntent.SaveGroupPressed -> saveSoloAnimal()

            // Open Bottom Sheet Animal
            is AnimalCountIntent.DialogClicked -> updateOpenDialog(
                intent.isEntry,
                intent.version,
                intent.item
            )

            AnimalCountIntent.EndDialogClicked -> updateEndDialog()

            // Warning Alert Dialog
            AnimalCountIntent.WarningConrPressed -> updateWarning()
            is AnimalCountIntent.WarningEndDialogClicked -> updateOpenWarningDialog(
                false,
                false
            )

            AnimalCountIntent.WarningDeleteConrPressed -> updateWarningDelete()
            AnimalCountIntent.WarningDeleteEndDialogClicked -> updateOpenWarningDeleteDialog(
                false,
                false
            )

            // Update Count Animal
            is AnimalCountIntent.CountChanged -> updateCountActionAnimal(intent.value)
            is AnimalCountIntent.PriceChanged -> updatePriceActionAnimal(intent.value)
            is AnimalCountIntent.AutoPriceClicked -> updateAutoPriceActionAnimal(intent.value)
            is AnimalCountIntent.DateClicked -> updateDateActionAnimal(intent.value)
            is AnimalCountIntent.NoteChanged -> updateNoteActionAnimal(intent.value)
            is AnimalCountIntent.DeleteCountPressed -> deleteAnimal(intent.value, intent.delete)

            // Add Count Animal
            is AnimalCountIntent.CountAddChanged -> updateCountAdd(intent.value)
            AnimalCountIntent.InsertAddPressed -> insertAddAnimal()
            AnimalCountIntent.UpdateAddPressed -> updateAddAnimal()

            // Sale Count Animal
            is AnimalCountIntent.BuyerSaleChanged -> updateBuyerSale(intent.value)
            AnimalCountIntent.InsertSalePressed -> insertSaleAnimal()
            AnimalCountIntent.UpdateSalePressed -> editSaleAnimal()

            // WriteOff Count Animal
            AnimalCountIntent.InsertWriteOffPressed -> insertWriteOffAnimal()
            AnimalCountIntent.UpdateWriteOffPressed -> updateWriteOffAnimal()

            // ExpensesCount Animal
            AnimalCountIntent.InsertExpensesPressed -> insertExpensesAnimal()
            AnimalCountIntent.UpdateExpensesPressed -> editExpensesAnimal()

            // Kill Count Animal
            is AnimalCountIntent.TitleProductKillChanged -> updateTitleProductKill(intent.value)

            is AnimalCountIntent.TitleAndSuffixKillClicked -> updateTitleAndSuffixProductKill(intent.pair)

            is AnimalCountIntent.CountProductKillChanged -> updateCountProductKill(intent.value)

            is AnimalCountIntent.SuffixProductKillChanged -> updateSuffixProductKill(intent.value)

            AnimalCountIntent.AddProductKillChanged -> addProductKill()
            is AnimalCountIntent.ChoiceProductKillChanged -> choiceProductKill(intent.index)
            is AnimalCountIntent.CancelProductKillChanged -> cancelProductKill()
            is AnimalCountIntent.EditProductKillChanged -> editProductKill()
            is AnimalCountIntent.RemoveProductKillChanged -> removeProductKill(intent.index)

            AnimalCountIntent.InsertKillChanged -> insertKillAnimal()
            AnimalCountIntent.EditKillChanged -> editKillAnimal()
        }
    }

    suspend fun loadData() {
        updateState { it.copy(isLoading = true) }
        val weight = animalWeightRepository.getWeightAnimalLimit(itemId).first()
        combine(
            animalRepository.getAnimal(itemId),
            animalCountRepository.getCountAnimalLimit(itemId),
            animalCountRepository.getCountAnimal(itemId)
        ) { animal, count, countList ->

            val uiCountList = countList.map { item ->
                val kills = updateOpenKillDialog(item.id)
                item.toUi(productKill = kills)
            }

            Triple(animal, count, uiCountList)
        }.collectLatest { data ->
            Log.i("count23", "loadData: ${data.second}")
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    animal = data.first,
                    currentAnimal = data.second,
                    countList = data.third,
                    weight = weight,
                    isLoading = false
                )
            }
        }
    }

    // Open Dialog
    private fun updateOpenDialog(
        isEntry: Boolean,
        version: AnimalCountVersion,
        domainAnimalCountPrice: DomainAnimalCountPriceUi?
    ) {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    isOpenDialog = true,
                    currentProduct = state.currentProduct.copy(
                        version = version,
                        isEntry = isEntry
                    )
                )
            }
            domainAnimalCountPrice?.let { domain ->
                val oldCount = domain.count

                val pair: Pair<List<String>, List<TitleAndSuffixDomain>> =
                    when (domain.version) {
                        AnimalCountVersion.KILL -> {
                            val titles = addRepository.getItemsTitleAddList(itemIdPT).first()
                            Pair(emptyList(), titles)
                        }

                        AnimalCountVersion.SALE -> {
                            val buyers = saleRepository.getItemsBuyerSaleList(itemIdPT).first()
                            Pair(buyers, emptyList())
                        }

                        else -> Pair(emptyList(), emptyList())
                    }

                updateState { state ->
                    state.copy(
                        currentProduct = state.currentProduct.toUiMap22(
                            domain,
                            productKill = domain.productKill,
                            pair.first, pair.second
                        ),
                        oldCount = oldCount,
                    )
                }
            }
        }
    }


    private suspend fun updateOpenKillDialog(idCount: Long): List<ProductKill> {
        val productList = addRepository.getProductKillList(idCount).first()
        val mappedProducts = productList.map { add ->
            ProductKill(
                idProduct = add.id,
                title = add.title,
                isEntry = false,
                countProduct = add.count.toString(), // Double -> String
                suffixProduct = add.countSuffix
            )
        }
        return if (mappedProducts.isEmpty()) emptyList() else mappedProducts
    }

    private fun updateEndDialog() {
        updateState {
            it.copy(
                isOpenDialog = false,
                currentProduct = CountItem(),
                openWarningDeleteAllDialog = WarningAnimalCount.MINUS
            )
        }
    }

    private fun updateWeightDialog(value: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isOpenWeightDialog = value
                )
            )
        }
    }

    // Action Animal
    private fun updateCountActionAnimal(countAnimal: String) {
        /*val isErrorCountMore = if (getState().isEntry) isAnimalCountIncrease(
            countAnimal,
            getState().currentAnimal.count
        ) else false*/
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    count = countAnimal,
                    error = state.currentProduct.error.copy(
                        isErrorCount = countAnimal.isBlank(),
                        isErrorCountZero = isAnimalCountZero(countAnimal)
                    )
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updatePriceActionAnimal(price: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    price = price,
                    error = state.currentProduct.error.copy(
                        isErrorPrice = price.isBlank()
                    )
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateAutoPriceActionAnimal(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isAutoCalculate = isAutoPrice
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateDateActionAnimal(date: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    date = date
                )
            )
        }
    }

    private fun updateNoteActionAnimal(note: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    note = note
                )
            )
        }
    }

    private fun updatePriceAllActionAnimal() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    priceAll = if (it.currentProduct.isAutoCalculate) (it.currentProduct.price.toConvertZeroDouble() * it.currentProduct.count.toConvertZeroDouble()).formatNumber()
                    else "0"
                )
            )
        }
    }

    private fun deleteAnimal(id: Long, delete: Boolean? = null) {
        viewModelScope.launch {
            val isError = validation2(Transaction.DELETE, delete)
            val confirmed = awaitConfirmationIfNeeded(isError)
            if (confirmed) {
                animalCountRepository.deleteAnimalCountTable(id)
                updateEndDialog()
                transferToIndividual()
            }
        }
    }

    // Add Count Animal
    private fun updateCountAdd(countAnimal: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    count = countAnimal,
                    error = state.currentProduct.error.copy(
                        isErrorCount = countAnimal.isBlank(),
                        isErrorCountZero = isAnimalCountZero(countAnimal)
                    )
                )
            )
        }
    }

    fun insertAddAnimal() {
        viewModelScope.launch {
            validationAdd()
            if (getState().currentProduct.hasAnyError) {
                animalCountRepository.insertAnimalCountTable(domainAnimalCount())
                animalRepository.updateAnimalTable(getState().animal.copy(group = true))
                updateEndDialog()
                transferToIndividual()
            }
        }
    }

    fun updateAddAnimal() {
        validation(
            validation = { validationAdd() },
            transaction = Transaction.UPDATE,
            mainAction = {
                animalCountRepository.updateAnimalCountTable(domainAnimalCount())
            }
        )
    }

    // Sale Count Animal
    private fun updateBuyerSale(buyer: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    buyer = buyer
                )
            )
        }
    }

    private fun insertSaleAnimal() {
        validation(
            validation = { validationSaleExpenses() },
            transaction = Transaction.INSERT,
            mainAction = {
                val countId =
                    animalCountRepository.insertAnimalCountTable(domainAnimalCount())
                saleRepository.insertSale(domainSale(countId))
            }
        )
    }

    private fun editSaleAnimal() {
        validation(
            validation = { validationSaleExpenses() },
            transaction = Transaction.UPDATE,
            mainAction = {
                animalCountRepository.updateAnimalCountTable(domainAnimalCount())
                saleRepository.updateSale(domainSale())
                Log.i(
                    "count23",
                    "editSaleAnimal: DomainAnimalCount: ${domainAnimalCount()}"
                )
                Log.i("count23", "editSaleAnimal: DomainSale: ${domainSale()}")
            }
        )
    }

    // Write Off
    private fun insertWriteOffAnimal() {
        validation(
            validation = { validationWriteOff() },
            transaction = Transaction.INSERT
        ) {
            val countId = animalCountRepository.insertAnimalCountTable(domainAnimalCount())
            writeOffRepository.insertWriteOff(domainWriteOff(countId))
        }
    }

    private fun updateWriteOffAnimal() {
        validation(
            validation = { validationWriteOff() },
            transaction = Transaction.UPDATE
        ) {
            animalCountRepository.updateAnimalCountTable(domainAnimalCount())
            writeOffRepository.updateWriteOff(domainWriteOff())
        }
    }

    //Expenses Animal
    private fun insertExpensesAnimal() {
        validation(
            validation = { validationSaleExpenses() },
            transaction = Transaction.INSERT
        ) {
            val countId = animalCountRepository.insertAnimalCountTable(domainAnimalCount())
            expensesRepository.insertExpenses(domainExpenses(countId))
            animalRepository.updateAnimalTable(getState().animal.copy(group = true))
        }
    }

    private fun editExpensesAnimal() {
        validation(
            validation = { validationSaleExpenses() },
            transaction = Transaction.UPDATE
        ) {
            animalCountRepository.updateAnimalCountTable(domainAnimalCount())
            expensesRepository.updateExpenses(domainExpenses())
        }
    }

    // Kill Animal
    private fun insertKillAnimal() {
        validation(
            validation = { validationKill() },
            transaction = Transaction.INSERT,
            isWarningMinus = null,
        ) {
            val productList = getState().currentProduct.productKillList
            val countId =
                animalCountRepository.insertAnimalCountTable(domainAnimalCount())
            Log.i("count23", "insertKillAnimal: insertAnimalCountTable Yes")
            writeOffRepository.insertWriteOff(
                domainWriteOff(
                    countId,
                    reasonNote(productList)
                )
            )
            Log.i("count23", "insertKillAnimal: insertWriteOffAnimal Yes")
            productList.forEach { product ->
                addRepository.insertAdd(domainAdd(product, countId))
                Log.i("count23", "insertKillAnimal: insertAdd Yes")
            }
        }
    }

    private fun editKillAnimal() {
        validation(
            validation = { validationKill() },
            transaction = Transaction.UPDATE,
            isWarningMinus = false
        ) {
            val productList = getState().currentProduct.productKillList
            animalCountRepository.updateAnimalCountTable(domainAnimalCount())
            writeOffRepository.updateWriteOff(domainWriteOff(note = reasonNote(productList)))
            productList.forEach { product ->
                when {
                    product.idProduct == 0L && product.isVisibility -> addRepository.insertAdd(
                        domainAdd(product)
                    )

                    product.idProduct != 0L && !product.isVisibility -> addRepository.deleteAddById(
                        product.idProduct
                    )

                    product.idProduct != 0L && product.isVisibility -> addRepository.updateAdd(
                        domainAdd(product)
                    )

                    else -> {}
                }
            }
        }
    }

    private fun reasonNote(productList: List<ProductKill>): String {
        return resourceProvider.getString(R.string.animal_card_screen_kill_add_product) + productList.mapIndexed { index, it ->
            "${index + 1}. ${it.title} - ${it.countProduct} ${it.suffixProduct}"
        }.joinToString("\n")
    }

    // Kill Animal Count
    private fun updateTitleProductKill(newTitle: String) {
        viewModelScope.launch {
            val warehouseList = updateWarehouseUiStateSync(newTitle)
            updateState { state ->
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        currentProduct = state.currentProduct.currentProduct.copy(
                            title = newTitle,
                            error = state.currentProduct.currentProduct.error.copy(
                                isError = newTitle.isBlank(),
                                isErrorSlash = newTitle.isSlash()
                            ),
                            warehouseList = warehouseList
                        )
                    )
                )
            }
        }
    }

    private fun updateTitleAndSuffixProductKill(
        newTitleAndSuffix: Pair<String, Suffix>
    ) {
        viewModelScope.launch {
            val warehouseList = updateWarehouseUiStateSync(newTitleAndSuffix.first)
            updateState { state ->
                state.copy(
                    currentProduct = state.currentProduct.copy(
                        currentProduct = state.currentProduct.currentProduct.copy(
                            title = newTitleAndSuffix.first,
                            suffixProduct = newTitleAndSuffix.second,
                            warehouseList = warehouseList
                        )
                    )
                )
            }
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String): List<DomainCountSuffix> {
        return warehouseRepository
            .getCurrentBalanceProductList(name, itemIdPT)
            .firstOrNull() ?: emptyList()
    }

    private fun updateCountProductKill(newCount: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    currentProduct = state.currentProduct.currentProduct.copy(
                        countProduct = newCount,
                        error = state.currentProduct.currentProduct.error.copy(
                            isErrorCount = newCount.isBlank(),
                        )
                    )
                )
            )
        }
    }

    private fun updateSuffixProductKill(newSuffix: Suffix) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    currentProduct = state.currentProduct.currentProduct.copy(
                        suffixProduct = newSuffix
                    )
                )
            )
        }
    }

    private fun addProductKill() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    productKillList = state.currentProduct.productKillList + state.currentProduct.currentProduct.copy(
                        isEntry = false
                    ),
                    currentProduct = ProductKill(
                        suffixProduct = getState().weight?.suffix ?: Suffix.KILOGRAM
                    )
                )
            )
        }
    }

    private fun choiceProductKill(index: Int) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    productKillList = state.currentProduct.productKillList.mapIndexed { i, item ->
                        if (i == index) item.copy(isEntry = true)
                        else item.copy(isEntry = false)
                    },
                    currentProduct = state.currentProduct.productKillList[index],
                    indexProductKill = index
                )
            )
        }
    }

    private fun cancelProductKill() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    productKillList = state.currentProduct.productKillList.mapIndexed { i, item ->
                        if (i == state.currentProduct.indexProductKill) item.copy(isEntry = false)
                        else item
                    },
                    currentProduct = ProductKill(),
                )
            )
        }
    }

    private fun editProductKill() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    productKillList = state.currentProduct.productKillList.mapIndexed { i, item ->
                        if (i == state.currentProduct.indexProductKill) state.currentProduct.currentProduct
                        else item
                    },
                    currentProduct = ProductKill()
                )
            )
        }
    }

    private fun removeProductKill(index: Int) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    productKillList = state.currentProduct.productKillList.mapIndexed { i, item ->
                        if (i == index) item.copy(isVisibility = false)
                        else item.copy(isEntry = false)
                    },
                    currentProduct = ProductKill()
                )
            )
        }
    }

    // Animal Card
    private fun updateSoloDialog(openDialog: Boolean) {
        updateState {
            it.copy(
                openSoloDialog = openDialog
            )
        }
    }

    private fun updateSex(sex: Boolean) {
        updateState { state ->
            state.copy(
                animal = state.animal.copy(
                    sex = sex
                )
            )
        }
    }

    private fun saveSoloAnimal() {
        viewModelScope.launch {
            animalRepository.updateAnimalTable(getState().animal.copy(group = false))
        }
        showMessage("Пол изменен")
    }

    // Validation
    private fun validation(
        validation: suspend () -> Unit,
        transaction: Transaction,
        isWarningMinus: Boolean? = null,
        mainAction: suspend () -> Unit
    ) {
        viewModelScope.launch {
            validation()
            val hasAnyError = getState().currentProduct.hasAnyError
            if (hasAnyError) {
                val isError = validation2(transaction, isWarningMinus)
                val confirmed = awaitConfirmationIfNeeded(isError)
                if (confirmed) {
                    mainAction()
                    updateEndDialog()
                    transferToIndividual()
                }
            }
        }
    }

    private fun validationAdd() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorCount = state.currentProduct.count.isBlank(),
                    )
                )
            )
        }
    }

    private fun validationWriteOff() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorCount = state.currentProduct.count.isBlank(),
                        isErrorCountZero = isAnimalCountZero(state.currentProduct.count)
                    )
                )
            )
        }
    }

    private fun validationSaleExpenses() {
        /* Log.i(
             "count23",
             "validationSaleExpenses: isErrorPrice: ${getState().domainAnimalCountPrice.price}"
         )*/
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorPrice = state.currentProduct.price.isBlank(),
                        isErrorCount = state.currentProduct.count.isBlank(),
                        isErrorCountZero = isAnimalCountZero(state.currentProduct.count)
                    )
                )
            )
        }
    }

    private fun validationKill() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorCount = state.currentProduct.count.isBlank(),
                        isErrorCountZero = isAnimalCountZero(state.currentProduct.count)
                    )
                )
            )
        }
    }

    private fun CountItem.toUiMap22(
        domain: DomainAnimalCountPriceUi,
        productKill: List<ProductKill>,
        buyerList: List<String>,
        titleList: List<TitleAndSuffixDomain>
    ): CountItem {
        return copy(
            id = domain.id,
            count = domain.count,
            suffix = domain.suffix,
            date = domain.date,
            animalId = domain.animalId,
            note = domain.note,
            version = domain.version ?: AnimalCountVersion.ADD,
            price = domain.price?.formatNumber(false) ?: "",
            priceAll = domain.priceAll?.formatNumber() ?: "",
            isAutoCalculate = domain.priceAll != null,
            buyer = domain.buyer ?: "",
            tableId = domain.tableId ?: 0,
            idPT = domain.idPT ?: 0,
            productKillList = productKill,
            buyerList = buyerList,
            titleList = titleList
        )
    }

    private fun domainAnimalCount(): DomainAnimalCount {
        val state = getState().currentProduct
        return DomainAnimalCount(
            id = state.id,
            count = state.count,
            suffix = state.suffix,
            date = state.date,
            idAnimal = itemId,
            note = state.note,
            version = state.version
        )
    }

    private fun domainAdd(productKill: ProductKill, countId: Long? = null): DomainAddTable {
        val product = productKill
        val dateList = getState().currentProduct.date.split(".")

        val domain = DomainAddTable(
            id = product.idProduct,
            title = product.title,
            count = product.countProduct.toConvertZeroDouble(),
            countSuffix = product.suffixProduct,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            category = resourceProvider.getString(R.string.animal_card_screen_category_kill),
            note = resourceProvider.getString(R.string.animal_card_screen_note_kill),
            price = 0.0,
            idPT = itemIdPT,
            animalId = itemId,
            animalCountId = countId ?: getState().currentProduct.id
        )
        Log.i("count23", "domainAdd: $domain")
        return domain
    }

    private fun domainWriteOff(
        countId: Long? = null,
        note: String? = null
    ): DomainWriteOffTable {
        val state = getState().currentProduct
        val dateList = state.date.split(".")
        return DomainWriteOffTable(
            id = state.tableId ?: 0,
            title = getState().animal.name,
            count = state.count.toConvertDbDouble(),
            countSuffix = state.suffix,
            price = if (state.price.isBlank()) null else state.price.toConvertDbDouble(),
            priceAll = if (state.isAutoCalculate) state.priceAll.toConvertDbDouble() else null,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            status = true,
            note = note
                ?: resourceProvider.getString(R.string.animal_card_screen_note_sale),
            idPT = itemIdPT,
            animalCountId = countId ?: state.id
        )
    }

    private fun domainExpenses(countId: Long? = null): DomainExpensesTable {
        val state = getState().currentProduct
        val dateList = state.date.split(".")
        return DomainExpensesTable(
            id = state.tableId ?: 0,
            title = getState().animal.name,
            count = state.count.toConvertDbDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            price = state.price.toConvertZeroDouble(),
            priceAll = if (state.isAutoCalculate) state.priceAll.toConvertDbDouble() else null,
            countSuffix = state.suffix,
            category = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses),
            note = resourceProvider.getString(R.string.animal_card_screen_note_expenses),
            isShowFood = false,
            isShowWarehouse = false,
            isShowAnimals = false,
            isShowFoodHand = false,
            idPT = itemIdPT,
            animalId = itemId,
            animalCountId = countId ?: state.id
        )
    }

    private fun domainSale(countId: Long? = null): DomainSaleTable {
        val state = getState().currentProduct
        val dateList = state.date.split(".")
        return DomainSaleTable(
            id = state.tableId ?: 0,
            title = getState().animal.name,
            count = state.count.toConvertDbDouble(),
            countSuffix = state.suffix,
            price = state.price.toConvertZeroDouble(),
            priceAll = if (state.isAutoCalculate) state.priceAll.toConvertDbDouble() else null,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            category = resourceProvider.getString(R.string.animal_card_screen_category_sale),
            buyer = state.buyer,
            note = resourceProvider.getString(R.string.animal_card_screen_note_sale),
            idPT = itemIdPT,
            animalId = itemId,
            animalCountId = countId ?: state.id
        )
    }

    private fun DomainAnimalCountPrice.toUi(productKill: List<ProductKill>): DomainAnimalCountPriceUi {
        return DomainAnimalCountPriceUi(
            id = id,
            count = count,
            suffix = suffix,
            date = date,
            animalId = animalId,
            note = note,
            version = version,
            price = price,
            priceAll = priceAll,
            buyer = buyer,
            tableId = tableId,
            idPT = idPT,
            productKill = productKill
        )
    }

    private suspend fun awaitConfirmationIfNeeded(isError: Boolean): Boolean {
        return if (isError)
            suspendCancellableCoroutine { continuation ->
                confirmDelete = CompletableDeferred()
                viewModelScope.launch {
                    val confirmed = confirmDelete.await()
                    continuation.resume(confirmed == true) { cause, _, _ -> }
                }
            } else true
    }

    private fun updateOpenWarningDialog(
        isOpenWarningsDialog: Boolean,
        wait: Boolean,
        warningAnimalCount: WarningAnimalCount = WarningAnimalCount.MINUS
    ) {
        updateState {
            it.copy(
                openWarningDialog = isOpenWarningsDialog,
                openWarningDeleteAllDialog = warningAnimalCount
            )
        }
        Log.i("count23", "updateOpenWarningDialog: $warningAnimalCount ")
        confirmDelete.complete(wait)
    }

    private fun updateWarning() {
        updateState {
            it.copy(openWarningDialog = false)
        }
        updateOpenWarningDialog(false, true)
    }

    private fun updateOpenWarningDeleteDialog(
        isOpenWarningDeleteDialog: Boolean,
        wait: Boolean
    ) {
        updateState { it.copy(openWarningDeleteDialog = isOpenWarningDeleteDialog) }
        confirmDelete.complete(wait)
    }

    private fun updateWarningDelete() {
        updateState { it.copy(openWarningDeleteDialog = false) }
        updateOpenWarningDeleteDialog(false, true)
    }

    private fun validation2(transaction: Transaction, delete: Boolean? = null): Boolean {
        val countAll = getState().currentAnimal.count.toInt()
        val count = getState().currentProduct.count.toConvertZero()
        val countOld = getState().oldCount.toConvertZero()
        val isError = if (countAll >= 0) when (transaction) {
            Transaction.INSERT -> insert2(countAll, count)
            Transaction.UPDATE -> edit2(countAll, countOld, count)
            Transaction.DELETE -> delete2(countAll, countOld)
        } else false
        Log.i("count23", "isError: $isError ")
        Log.i(
            "count23",
            "countAll Boolean: ${countAll >= 0} " +
                    "countAll: $countAll, transaction: $transaction version: ${getState().currentProduct.version}" +
                    " isError: ${isError}"
        )
        val delete2 = when {
            isError && delete == true -> WarningAnimalCount.DELETE_MINUS
            !isError && delete == true -> WarningAnimalCount.DELETE
            isError && delete == false -> WarningAnimalCount.UPDATE_MINUS
            !isError && delete == false -> WarningAnimalCount.UPDATE
            else -> WarningAnimalCount.MINUS
        }
        if (isError || delete != null)
            updateOpenWarningDialog(true, true, delete2)

        return isError || delete != null
    }

    private fun delete2(countAll: Int, countOld: Int): Boolean {
        Log.i("count23", "delete2: countALL: $countAll countOld: $countOld ")
        return when (getState().currentProduct.version) {
            AnimalCountVersion.ADD, AnimalCountVersion.EXPENSES, AnimalCountVersion.INCUBATOR, null -> {
                Log.i("count23", "delete2:  ${0 > countAll - countOld} ")
                0 > countAll - countOld
            }

            AnimalCountVersion.SALE, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.KILL -> {
                Log.i("count23", "delete2:  ${0 > countAll + countOld} ")
                0 > countAll + countOld
            }
        }
    }

    private fun insert2(countAll: Int, count: Int): Boolean {
        Log.i(
            "count23",
            "edit2: countAll: $countAll, count: $count total Add: ${false} total Sale: ${0 > countAll - count}"
        )
        return when (getState().currentProduct.version) {
            AnimalCountVersion.ADD, AnimalCountVersion.EXPENSES, AnimalCountVersion.INCUBATOR, null -> {
                false
            }

            AnimalCountVersion.SALE, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.KILL -> {
                0 > countAll - count
            }
        }
    }

    private fun edit2(countAll: Int, countOld: Int, count: Int): Boolean {
        Log.i(
            "count23",
            "edit2: countAll: $countAll, countOld: $countOld, count: $count total Add: ${countAll - (countOld - count)} total Sale: ${countAll + (countOld - count)}"
        )
        return when (getState().currentProduct.version) {
            AnimalCountVersion.ADD, AnimalCountVersion.EXPENSES, AnimalCountVersion.INCUBATOR, null -> {
                Log.i("count23", "editAdd:  ${0 >= countAll - (countOld - count)} ")
                0 > (countAll - (countOld - count))
            }

            AnimalCountVersion.SALE, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.KILL -> {
                Log.i("count23", "editSale:  ${0 >= countAll + (countOld - count)} ")
                0 > (countAll + (countOld - count))
            }
        }
    }

    private suspend fun transferToIndividual() {
        delay(100)
        val countAnimalAll = getState().currentAnimal.count.toInt()
        val isAnimalGroup = getState().animal.group
        Log.i(
            "count23",
            "transferToIndividual: countAnimalAll: $countAnimalAll, isAnimalGroup: $isAnimalGroup"
        )
        when {
            countAnimalAll == 1 && isAnimalGroup -> updateSoloDialog(true)
            countAnimalAll != 1 && !isAnimalGroup -> animalRepository.updateAnimalTable(
                getState().animal.copy(
                    group = true
                )
            )

            else -> {}
        }
    }
}


sealed class AnimalCountIntent : BaseIntent {
    // Animal Card
    data class DialogSoloClicked(val value: Boolean) : AnimalCountIntent()
    data class SexClicked(val value: Boolean) : AnimalCountIntent()
    data object SaveGroupPressed : AnimalCountIntent()

    // Open Bottom Sheet Animal
    data class DialogClicked(
        val isEntry: Boolean,
        val version: AnimalCountVersion = AnimalCountVersion.ADD,
        val item: DomainAnimalCountPriceUi? = null
    ) :
        AnimalCountIntent()

    data object EndDialogClicked : AnimalCountIntent()

    // Warning Alert Dialog
    data object WarningEndDialogClicked : AnimalCountIntent()
    data object WarningConrPressed : AnimalCountIntent()

    data object WarningDeleteEndDialogClicked : AnimalCountIntent()
    data object WarningDeleteConrPressed : AnimalCountIntent()

    // Update Action Animal
    data class CountChanged(val value: String) : AnimalCountIntent()
    data class DateClicked(val value: String) : AnimalCountIntent()
    data class NoteChanged(val value: String) : AnimalCountIntent()
    data class PriceChanged(val value: String) : AnimalCountIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalCountIntent()
    data class DeleteCountPressed(val value: Long, val delete: Boolean? = null) :
        AnimalCountIntent()

    // Add Count Animal
    data class CountAddChanged(val value: String) : AnimalCountIntent()
    data object InsertAddPressed : AnimalCountIntent()
    data object UpdateAddPressed : AnimalCountIntent()

    // Sale Count Animal
    data class BuyerSaleChanged(val value: String) : AnimalCountIntent()
    data object InsertSalePressed : AnimalCountIntent()
    data object UpdateSalePressed : AnimalCountIntent()

    // WriteOff Count Animal
    data object InsertWriteOffPressed : AnimalCountIntent()
    data object UpdateWriteOffPressed : AnimalCountIntent()

    // Expenses Count Animal
    data object InsertExpensesPressed : AnimalCountIntent()
    data object UpdateExpensesPressed : AnimalCountIntent()

    // Kill Count Animal
    data class TitleProductKillChanged(val value: String) : AnimalCountIntent()
    data class TitleAndSuffixKillClicked(val pair: Pair<String, Suffix>) :
        AnimalCountIntent()

    data class CountProductKillChanged(val value: String) : AnimalCountIntent()
    data class SuffixProductKillChanged(val value: Suffix) : AnimalCountIntent()
    data object AddProductKillChanged : AnimalCountIntent()
    data class ChoiceProductKillChanged(val index: Int) : AnimalCountIntent()
    data object CancelProductKillChanged : AnimalCountIntent()
    data object EditProductKillChanged : AnimalCountIntent()
    data class RemoveProductKillChanged(val index: Int) : AnimalCountIntent()
    data object InsertKillChanged : AnimalCountIntent()
    data object EditKillChanged : AnimalCountIntent()
}

enum class Transaction {
    INSERT, UPDATE, DELETE
}

enum class WarningAnimalCount {
    MINUS, DELETE, DELETE_MINUS, UPDATE, UPDATE_MINUS
}