package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainSaleTable
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
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.animal.indicators.count.AnimalCountState.ProductKill
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
) : ListViewModel<AnimalCountState, AnimalCountIntent>(AnimalCountState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemId])
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemIdPT])
    private var confirmDelete = CompletableDeferred<Boolean?>()

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    fun onIntent(intent: AnimalCountIntent) {
        return when (intent) {
            // Animal Card
            is AnimalCountIntent.DialogSoloClicked -> updateSoloDialog(intent.value)
            is AnimalCountIntent.SexClicked -> updateSex(intent.value)
            AnimalCountIntent.SaveGroupPressed -> saveSoloAnimal()

            // Open Bottom Sheet Animal
            is AnimalCountIntent.DialogClicked -> updateOpenDialog(
                intent.isEntry,
                intent.isKillAnimalSheet,
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
            is AnimalCountIntent.TitleProductKillChanged -> updateTitleProductKill(
                intent.index, intent.value
            )

            is AnimalCountIntent.TitleAndSuffixKillClicked -> updateTitleAndSuffixProductKill(
                intent.index, intent.pair
            )

            is AnimalCountIntent.CountProductKillChanged -> updateCountProductKill(
                intent.index, intent.value
            )

            is AnimalCountIntent.SuffixProductKillChanged -> updateSuffixProductKill(
                intent.index, intent.value
            )

            AnimalCountIntent.AddProductKillChanged -> addProductKill()
            is AnimalCountIntent.RemoveProductKillChanged -> removeProductKill(intent.index)

            AnimalCountIntent.InsertKillChanged -> insertKillAnimal()
            AnimalCountIntent.EditKillChanged -> editKillAnimal()
        }
    }


    suspend fun loadData() {
        updateState { it.copy(isLoading = true) }
        val weight = animalWeightRepository.getWeightAnimalLimit(itemId).first()
        val titleList = addRepository.getItemsTitleAddList(itemIdPT).firstOrNull() ?: emptyList()
        combine(
            animalRepository.getAnimal(itemId),
            animalCountRepository.getCountAnimalLimit(itemId),
            animalCountRepository.getCountAnimal(itemId)
        ) { animal, count, countList ->
            Triple(animal, count, countList)
        }.collectLatest { data ->
            Log.i("count23", "loadData: ${data.second}")
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    animal = data.first,
                    currentAnimal = data.second,
                    countList = data.third,
                    weight = weight,
                    titleList = titleList,
                    isLoading = false
                )
            }
        }
    }

    // Open Dialog
    private fun updateOpenDialog(
        isEntry: Boolean,
        isKillAnimalSheet: Boolean = false,
        domainAnimalCountPrice: DomainAnimalCountPrice
    ) {
        viewModelScope.launch {
            val oldCount = if (isEntry) "" else domainAnimalCountPrice.count
            val price = domainAnimalCountPrice.price?.formatNumber(false) ?: ""
            val priceAll = domainAnimalCountPrice.priceAll?.formatNumber(false) ?: ""
            val isAutoPrice = domainAnimalCountPrice.priceAll != null
            val finalProductKill =
                if (isKillAnimalSheet) updateOpenKillDialog(domainAnimalCountPrice.id) else emptyList()
            Log.i(
                "count23",
                "updateOpenDialog: isAutoPrice: $isAutoPrice, priceAll: ${domainAnimalCountPrice.priceAll}," +
                        " finalProductKill: $finalProductKill, isKillAnimalSheet: $isKillAnimalSheet"
            )
            updateState {
                it.copy(
                    isOpenDialog = true,
                    isEntry = isEntry,
                    price = price,
                    priceAll = priceAll,
                    isAutoPrice = isAutoPrice,
                    domainAnimalCountPrice = domainAnimalCountPrice,
                    oldCount = oldCount,
                    productKill = finalProductKill
                )
            }
        }
    }

    private suspend fun updateOpenKillDialog(idCount: Long): List<ProductKill> {
        val productList = addRepository.getProductKillList(idCount).first()
        val mappedProducts = productList.map { add ->
            ProductKill(
                idProduct = add.id,
                title = add.title,
                countProduct = add.count.toString(), // Double -> String
                suffixProduct = add.countSuffix
            )
        }
        return if (mappedProducts.isEmpty()) listOf(ProductKill()) else mappedProducts
    }

    private fun updateEndDialog() {
        updateState {
            it.copy(
                isOpenDialog = false,
                domainAnimalCountPrice = DomainAnimalCountPrice(date = dateToday()),
                error = AnimalCountState.Error(),
                price = "",
                priceAll = "",
                isAutoPrice = false,
                openWarningDeleteAllDialog = WarningAnimalCount.MINUS
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
                domainAnimalCountPrice = state.domainAnimalCountPrice.copy(
                    count = countAnimal
                ),
                error = state.error.copy(
                    isErrorCount = countAnimal.isBlank(),
                    isErrorCountZero = isAnimalCountZero(countAnimal)
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updatePriceActionAnimal(price: String) {
        updateState { state ->
            state.copy(
                price = price,
                error = state.error.copy(
                    isErrorPrice = price.isBlank()
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateAutoPriceActionAnimal(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                isAutoPrice = isAutoPrice
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateDateActionAnimal(date: String) {
        updateState { state ->
            state.copy(
                domainAnimalCountPrice = state.domainAnimalCountPrice.copy(
                    date = date
                )
            )
        }
    }

    private fun updateNoteActionAnimal(note: String) {
        updateState { state ->
            state.copy(
                domainAnimalCountPrice = state.domainAnimalCountPrice.copy(
                    note = note
                )
            )
        }
    }

    private fun updatePriceAllActionAnimal() {
        updateState {
            it.copy(
                priceAll = if (it.isAutoPrice) (it.price.toConvertZeroDouble() * it.domainAnimalCountPrice.count.toConvertZeroDouble()).formatNumber()
                else "0"
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
                domainAnimalCountPrice = state.domainAnimalCountPrice.copy(
                    count = countAnimal
                ),
                error = state.error.copy(
                    isErrorCount = countAnimal.isBlank(),
                    isErrorCountZero = isAnimalCountZero(countAnimal)
                )
            )
        }
    }

    fun insertAddAnimal() {
        viewModelScope.launch {
            validationAdd()
            if (getState().hasAnyError) {
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
                domainAnimalCountPrice = state.domainAnimalCountPrice.copy(
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
                val countId = animalCountRepository.insertAnimalCountTable(domainAnimalCount())
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
                Log.i("count23", "editSaleAnimal: DomainAnimalCount: ${domainAnimalCount()}")
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
            isKillValidation = true
        ) {
            val productList = getState().productKill
            val countId =
                animalCountRepository.insertAnimalCountTable(domainAnimalCount())
            Log.i("count23", "insertKillAnimal: insertAnimalCountTable Yes")
            writeOffRepository.insertWriteOff(domainWriteOff(countId, reasonNote(productList)))
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
            isWarningMinus = false,
            isKillValidation = true
        ) {
            val productList = getState().productKill
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
    private fun updateTitleProductKill(index: Int, newTitle: String) {
        viewModelScope.launch {
            val warehouseList = updateWarehouseUiStateSync(newTitle)
            updateState { state ->
                state.copy(
                    productKill = state.productKill.mapIndexed { i, item ->
                        if (i == index)
                            item.copy(
                                title = newTitle,
                                error = item.error.copy(
                                    isError = newTitle.isBlank(),
                                    isErrorSlash = newTitle.isSlash()
                                ),
                                warehouseList = warehouseList
                            )
                        else item
                    }
                )
            }
        }
    }

    private fun updateTitleAndSuffixProductKill(
        index: Int,
        newTitleAndSuffix: Pair<String, Suffix>
    ) {
        viewModelScope.launch {
            val warehouseList = updateWarehouseUiStateSync(newTitleAndSuffix.first)
            updateState { state ->
                state.copy(
                    productKill = state.productKill.mapIndexed { i, item ->
                        if (i == index)
                            item.copy(
                                title = newTitleAndSuffix.first,
                                suffixProduct = newTitleAndSuffix.second,
                                warehouseList = warehouseList
                            )
                        else item
                    }
                )
            }
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String): List<DomainCountSuffix> {
        return warehouseRepository
            .getCurrentBalanceProductList(name, itemIdPT)
            .firstOrNull() ?: emptyList()
    }

    private fun updateCountProductKill(index: Int, newCount: String) {
        updateState { state ->
            state.copy(
                productKill = state.productKill.mapIndexed { i, item ->
                    if (i == index)
                        item.copy(
                            countProduct = newCount,
                            error = item.error.copy(
                                isErrorCount = newCount.isBlank(),
                            )
                        )
                    else item
                }
            )
        }
    }

    private fun updateSuffixProductKill(index: Int, newSuffix: Suffix) {
        updateState { state ->
            state.copy(
                productKill = state.productKill.mapIndexed { i, item ->
                    if (i == index) item.copy(suffixProduct = newSuffix)
                    else item
                }
            )
        }
    }

    private fun addProductKill() {
        updateState { state ->
            state.copy(
                productKill = state.productKill + ProductKill(
                    suffixProduct = getState().weight?.suffix ?: Suffix.KILOGRAM
                )
            )
        }
    }

    private fun removeProductKill(index: Int) {
        updateState { state ->
            state.copy(
                productKill = state.productKill.mapIndexed { i, item ->
                    if (i == index)
                        item.copy(
                            isVisibility = false
                        )
                    else item
                }
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
        isKillValidation: Boolean = false,
        mainAction: suspend () -> Unit
    ) {
        viewModelScope.launch {
            validation()
            val hasAnyError =
                if (isKillValidation == false) getState().hasAnyError else getState().hasAnyError && getState().hasFieldError
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
                error = state.error.copy(
                    isErrorCount = state.domainAnimalCountPrice.count.isBlank(),
                )
            )
        }
    }

    private fun validationWriteOff() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorCount = state.domainAnimalCountPrice.count.isBlank(),
                    isErrorCountZero = isAnimalCountZero(state.domainAnimalCountPrice.count)
                )
            )
        }
    }

    private fun validationSaleExpenses() {
        Log.i(
            "count23",
            "validationSaleExpenses: isErrorPrice: ${getState().domainAnimalCountPrice.price}"
        )
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorPrice = state.price.isBlank(), //TODO
                    isErrorCount = state.domainAnimalCountPrice.count.isBlank(),
                    isErrorCountZero = isAnimalCountZero(state.domainAnimalCountPrice.count)
                )
            )
        }
    }

    private fun validationKill() {
        val updatedProductKill = getState().productKill.map { product ->
            product.copy(
                error = product.error.copy(
                    isError = product.title.isBlank(),
                    isErrorSlash = product.title.isSlash(),
                    isErrorCount = product.countProduct.isBlank()
                )
            )
        }

        updateState { state ->
            state.copy(
                productKill = updatedProductKill,
                error = state.error.copy(
                    isErrorCount = state.domainAnimalCountPrice.count.isBlank(),
                    isErrorCountZero = isAnimalCountZero(state.domainAnimalCountPrice.count)
                )
            )
        }
    }

    private fun domainAnimalCount(): DomainAnimalCount {
        val state = getState().domainAnimalCountPrice
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
        val dateList = getState().domainAnimalCountPrice.date.split(".")

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
            animalCountId = countId ?: getState().domainAnimalCountPrice.id
        )
        Log.i("count23", "domainAdd: $domain")
        return domain
    }

    private fun domainWriteOff(countId: Long? = null, note: String? = null): DomainWriteOffTable {
        val state = getState().domainAnimalCountPrice
        val dateList = state.date.split(".")
        return DomainWriteOffTable(
            id = state.tableId ?: 0,
            title = getState().animal.name,
            count = state.count.toConvertDbDouble(),
            countSuffix = state.suffix,
            price = if (state.price != 0.00) state.price else null,
            priceAll = if (getState().isAutoPrice) state.priceAll else null,
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            status = true,
            note = note ?: resourceProvider.getString(R.string.animal_card_screen_note_sale),
            idPT = itemIdPT,
            animalCountId = countId ?: state.id
        )
    }

    private fun domainExpenses(countId: Long? = null): DomainExpensesTable {
        val state = getState().domainAnimalCountPrice
        val dateList = state.date.split(".")
        return DomainExpensesTable(
            id = state.tableId ?: 0,
            title = getState().animal.name,
            count = state.count.toConvertDbDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            price = getState().price.toConvertZeroDouble(),
            priceAll = if (getState().isAutoPrice) getState().priceAll.toConvertDbDouble() else null,
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
        val state = getState().domainAnimalCountPrice
        val dateList = state.date.split(".")
        return DomainSaleTable(
            id = state.tableId ?: 0,
            title = getState().animal.name,
            count = state.count.toConvertDbDouble(),
            countSuffix = state.suffix,
            price = getState().price.toConvertZeroDouble(),
            priceAll = if (getState().isAutoPrice) getState().priceAll.toConvertDbDouble() else null,
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
        updateState { it.copy(openWarningDialog = false,)
        }
        updateOpenWarningDialog(false, true)
    }

    private fun updateOpenWarningDeleteDialog(isOpenWarningDeleteDialog: Boolean, wait: Boolean) {
        updateState { it.copy(openWarningDeleteDialog = isOpenWarningDeleteDialog) }
        confirmDelete.complete(wait)
    }

    private fun updateWarningDelete() {
        updateState { it.copy(openWarningDeleteDialog = false) }
        updateOpenWarningDeleteDialog(false, true)
    }

    private fun validation2(transaction: Transaction, delete: Boolean? = null): Boolean {
        val countAll = getState().currentAnimal.count.toInt()
        val count = getState().domainAnimalCountPrice.count.toConvertZero()
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
                    "countAll: $countAll, transaction: $transaction version: ${getState().domainAnimalCountPrice.version}" +
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
        return when (getState().domainAnimalCountPrice.version) {
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
        return when (getState().domainAnimalCountPrice.version) {
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
        return when (getState().domainAnimalCountPrice.version) {
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
        val item: DomainAnimalCountPrice,
        val isKillAnimalSheet: Boolean = false
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
    data class TitleProductKillChanged(val index: Int, val value: String) : AnimalCountIntent()
    data class TitleAndSuffixKillClicked(val index: Int, val pair: Pair<String, Suffix>) :
        AnimalCountIntent()

    data class CountProductKillChanged(val index: Int, val value: String) : AnimalCountIntent()
    data class SuffixProductKillChanged(val index: Int, val value: Suffix) : AnimalCountIntent()
    data object AddProductKillChanged : AnimalCountIntent()
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