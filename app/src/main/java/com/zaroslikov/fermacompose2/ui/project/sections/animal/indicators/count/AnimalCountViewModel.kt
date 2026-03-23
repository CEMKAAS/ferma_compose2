package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.count

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
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
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
) : EntryNewViewModel2<AnimalCountState, AnimalCountIntent, AnimalCountReduce>(
    AnimalCountState(),
    AnimalCountReduce()
) {
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemId])
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCountDestination.itemIdPT])
    private var confirmDelete = CompletableDeferred<Boolean?>()

    init {
        loadData()
    }

    override fun onIntent(intent: AnimalCountIntent) {
        sendIntent(intent)
        return when (intent) {
            // Animal Card
            AnimalCountIntent.SaveGroupPressed -> saveSoloAnimal()

            // Open Bottom Sheet Animal
            is AnimalCountIntent.DialogClicked -> loadDataForEntryOrEdit(
                isOpen = intent.isEntry,
                domain = intent.item,
                version = intent.version,
                isSaveStateForBottomSheet = intent.isSaveStateForBottomSheet
            )

            // Warning Alert Dialog
            is AnimalCountIntent.WarningEndDialogClicked ->
                updateOpenWarningDialog(
                    isOpenWarningsDialog = false, wait = intent.wait,
                    statusWarning = WarningAnimalCount.UPDATE_KILL,
                )

            // Update Count Animal
            is AnimalCountIntent.DeleteCountPressed -> deleteAnimal(intent.value)

            // Add Count Animal
            AnimalCountIntent.InsertAddPressed -> insertAddAnimal()
            AnimalCountIntent.UpdateAddPressed -> updateAddAnimal()

            // Sale Count Animal
            AnimalCountIntent.InsertSalePressed -> insertSaleAnimal()
            AnimalCountIntent.UpdateSalePressed -> editSaleAnimal()

            // WriteOff Count Animal
            AnimalCountIntent.InsertWriteOffPressed -> insertWriteOffAnimal()
            AnimalCountIntent.UpdateWriteOffPressed -> updateWriteOffAnimal()

            // ExpensesCount Animal
            AnimalCountIntent.InsertExpensesPressed -> insertExpensesAnimal()
            AnimalCountIntent.UpdateExpensesPressed -> editExpensesAnimal()

            // Kill Count Animal
            AnimalCountIntent.InsertKillChanged -> insertKillAnimal()
            AnimalCountIntent.EditKillChanged -> editKillAnimal()
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
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
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        animal = data.first,
                        countAnimal = data.second?.count,
                        countAnimalSuffix = data.second?.suffix ?: Suffix.PIECES,
                        countList = data.third,
                        weight = weight,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: DomainAnimalCountPriceUi?,
        version: AnimalCountVersion = AnimalCountVersion.ADD,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else CountItem()
                onIntent(
                    AnimalCountIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet, version
                    )
                )
                return@launch
            }
            val newState =
                if (!getState().isSaveStateForEntry || domain != null || version != getState().saveAnimalCountVersion) {
                    val titleDeferred =
                        async { addRepository.getItemsTitleAddList(itemIdPT).first() }
                    val buyerDeferred =
                        async { saleRepository.getItemsBuyerSaleList(itemIdPT).first() }

                    val baseState = CountItem(
                        version = version,
                        itemIdPT = itemIdPT,
                        buyerList = buyerDeferred.await(),
                        titleList = titleDeferred.await(),
                    )
                    domain?.let { baseState.toUiMap22(it) } ?: baseState
                } else getState().currentProduct
            onIntent(
                AnimalCountIntent.RefreshEntryBottomSheetState(
                    true, newState
                )
            )
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
        return mappedProducts.ifEmpty { emptyList() }
    }

    // Action Animal
    private fun validation(
        transaction: Transaction,
        mainAction: suspend () -> Unit
    ) {
        viewModelScope.launch {
            val isError = validationCalculate(transaction)
            val confirmed = awaitConfirmationIfNeeded(isError)
            if (confirmed) {
                mainAction()
                loadDataForEntryOrEdit(false, null)
                transferToIndividual()
            }
        }
    }

    private fun deleteAnimal(id: Long) {
        viewModelScope.launch {
            val isError = validationCalculate(Transaction.DELETE, true, id)
            val confirmed = awaitConfirmationIfNeeded(isError)
            if (confirmed) {
                animalCountRepository.deleteAnimalCountTable(id)
                transferToIndividual()
            }
        }
    }

    // Add Count Animal
    fun insertAddAnimal() {
        viewModelScope.launch {
            animalCountRepository.insertAnimalCountTable(domainAnimalCount())
            animalRepository.updateAnimalTable(getState().animal.copy(group = true))
            loadDataForEntryOrEdit(false, null)
            transferToIndividual()
        }
    }

    private fun updateAddAnimal() {
        validation(
            transaction = Transaction.UPDATE,
            mainAction = {
                animalCountRepository.updateAnimalCountTable(domainAnimalCount())
            }
        )
    }

    // Sale Count Animal
    private fun insertSaleAnimal() {
        validation(
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
            transaction = Transaction.UPDATE,
            mainAction = {
                animalCountRepository.updateAnimalCountTable(domainAnimalCount())
                saleRepository.updateSale(domainSale())
            }
        )
    }

    // Write Off
    private fun insertWriteOffAnimal() {
        validation(
            transaction = Transaction.INSERT,
            mainAction = {
                val countId = animalCountRepository.insertAnimalCountTable(domainAnimalCount())
                writeOffRepository.insertWriteOff(domainWriteOff(countId))
            }
        )
    }

    private fun updateWriteOffAnimal() {
        validation(
            transaction = Transaction.UPDATE,
            mainAction = {
                animalCountRepository.updateAnimalCountTable(domainAnimalCount())
                writeOffRepository.updateWriteOff(domainWriteOff())
            }
        )
    }

    //Expenses Animal
    private fun insertExpensesAnimal() {
        validation(
            transaction = Transaction.INSERT,
            mainAction = {
                val countId = animalCountRepository.insertAnimalCountTable(domainAnimalCount())
                expensesRepository.insertExpenses(domainExpenses(countId))
                animalRepository.updateAnimalTable(getState().animal.copy(group = true))
            }
        )
    }

    private fun editExpensesAnimal() {
        validation(
            transaction = Transaction.UPDATE,
            mainAction = {
                animalCountRepository.updateAnimalCountTable(domainAnimalCount())
                expensesRepository.updateExpenses(domainExpenses())
            }
        )
    }

    // Kill Animal
    private fun insertKillAnimal() {
        validation(
            transaction = Transaction.INSERT,
            mainAction = {
                val productList = getState().currentProduct.productKillList
                val countId =
                    animalCountRepository.insertAnimalCountTable(domainAnimalCount())
                writeOffRepository.insertWriteOff(
                    domainWriteOff(
                        countId,
                        reasonNote(productList)
                    )
                )
                productList.forEach { product ->
                    addRepository.insertAdd(domainAdd(product, countId))
                }
            }
        )
    }

    private fun editKillAnimal() {
        validation(
            transaction = Transaction.UPDATE,
        ) {
            val productList = getState().currentProduct.productKillList
            animalCountRepository.updateAnimalCountTable(domainAnimalCount())
            writeOffRepository.updateWriteOff(domainWriteOff(note = reasonNote(productList)))
            productList.forEach { product ->
                when {
                    product.idProduct == 0L && product.isVisibility ->
                        addRepository.insertAdd(domainAdd(product))

                    product.idProduct != 0L && !product.isVisibility ->
                        addRepository.deleteAddById(product.idProduct)

                    product.idProduct != 0L && product.isVisibility ->
                        addRepository.updateAdd(domainAdd(product))

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
    private suspend fun updateWarehouseUiStateSync(name: String): List<DomainCountSuffix> {
        return warehouseRepository
            .getCurrentBalanceProductList(name, itemIdPT)
            .firstOrNull() ?: emptyList()
    }

    // Animal Card
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

    private fun validationCalculate(
        transaction: Transaction,
        isDelete: Boolean = false,
        id: Long? = null
    ): Boolean {
        val currentProduct = getState().currentProduct
        val choiceAnimal = id?.let { getState().countList.find { it.id == id } }

        val countAll = getState().countAnimal?.toInt() ?: 0
        val count = choiceAnimal?.count?.toConvertZero() ?: currentProduct.count.toConvertZero()
        val countOld = currentProduct.oldCount.toConvertZero()
        val version = choiceAnimal?.version ?: currentProduct.version

        val isErrorMinus = if (countAll >= 0) when (transaction) {
            Transaction.INSERT -> insert2(countAll, count, version)
            Transaction.UPDATE -> edit2(countAll, countOld, count, version)
            Transaction.DELETE -> delete2(countAll, count, version)
        } else false

        val statusWarning = when {
            isErrorMinus && isDelete && AnimalCountVersion.KILL == version -> WarningAnimalCount.DELETE_MINUS_KILL
            !isErrorMinus && isDelete && AnimalCountVersion.KILL == version -> WarningAnimalCount.DELETE_KILL

            isErrorMinus && isDelete && AnimalCountVersion.KILL != version -> WarningAnimalCount.DELETE_MINUS
            !isErrorMinus && isDelete && AnimalCountVersion.KILL != version -> WarningAnimalCount.DELETE

            isErrorMinus && !isDelete && AnimalCountVersion.KILL == version -> WarningAnimalCount.UPDATE_MINUS_KILL
            !isErrorMinus && !isDelete && AnimalCountVersion.KILL == version -> WarningAnimalCount.UPDATE_KILL

            isErrorMinus && !isDelete && AnimalCountVersion.KILL != version -> WarningAnimalCount.UPDATE_MINUS
            else -> null
        }

        return if (statusWarning != null) {
            updateOpenWarningDialog(
                isOpenWarningsDialog = true,
                wait = true,
                statusWarning = statusWarning
            )
            true
        } else false
    }

    private fun updateOpenWarningDialog(
        isOpenWarningsDialog: Boolean,
        wait: Boolean,
        statusWarning: WarningAnimalCount
    ) {
        if (!isOpenWarningsDialog) {
            updateState {
                it.copy(
                    openWarningDialog = isOpenWarningsDialog
                )
            }
            confirmDelete.complete(wait)
            return
        }

        val textWarning = resourceProvider.getString(
            when (statusWarning) {
                WarningAnimalCount.DELETE_KILL -> R.string.animal_count_screen_warning_product_delete_all_text
                WarningAnimalCount.DELETE_MINUS_KILL -> R.string.animal_count_screen_warning_minus_and_delete_text

                WarningAnimalCount.DELETE -> R.string.animal_count_screen_warning_delete
                WarningAnimalCount.DELETE_MINUS -> R.string.animal_count_screen_warning_delete_minus

                WarningAnimalCount.UPDATE_KILL -> R.string.animal_count_screen_warning_product_update_kill
                WarningAnimalCount.UPDATE_MINUS_KILL -> R.string.animal_count_screen_warning_product_update_minus_kill

                WarningAnimalCount.UPDATE_MINUS -> R.string.animal_count_screen_warning_text
            }
        )
        updateState {
            it.copy(
                openWarningDialog = isOpenWarningsDialog,
                statusWarningDialog = statusWarning,
                textWarning = textWarning
            )
        }
        confirmDelete.complete(wait)
    }

    private fun insert2(countAll: Int, count: Int, version: AnimalCountVersion): Boolean {
        return when (version) {
            AnimalCountVersion.ADD, AnimalCountVersion.EXPENSES, AnimalCountVersion.INCUBATOR ->
                false

            AnimalCountVersion.SALE, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.KILL ->
                0 > countAll - count
        }
    }

    private fun edit2(
        countAll: Int,
        countOld: Int,
        count: Int,
        version: AnimalCountVersion
    ): Boolean {
        return when (version) {
            AnimalCountVersion.ADD, AnimalCountVersion.EXPENSES, AnimalCountVersion.INCUBATOR ->
                0 > (countAll - (countOld - count))

            AnimalCountVersion.SALE, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.KILL ->
                0 > (countAll + (countOld - count))
        }
    }

    private fun delete2(countAll: Int, count: Int, version: AnimalCountVersion): Boolean {
        return when (version) {
            AnimalCountVersion.ADD, AnimalCountVersion.EXPENSES, AnimalCountVersion.INCUBATOR ->
                0 > countAll - count

            AnimalCountVersion.SALE, AnimalCountVersion.WRITE_OFF, AnimalCountVersion.KILL ->
                0 > countAll + count
        }
    }

    private suspend fun transferToIndividual() {
        val countAnimalAll = animalCountRepository
            .getCountAnimalLimit(itemId)
            .first()?.count?.toInt() ?: 0

        val isAnimalGroup = getState().animal.group
        when {
            countAnimalAll == 1 && isAnimalGroup ->
                sendIntent(AnimalCountIntent.OpenSoloDialogClicked(true))

            countAnimalAll != 1 && !isAnimalGroup ->
                animalRepository.updateAnimalTable(getState().animal.copy(group = true))

            else -> Unit
        }
    }

    private fun saveSoloAnimal() {
        viewModelScope.launch {
            animalRepository.updateAnimalTable(getState().animal.copy(group = false))
            showMessage("Пол изменен")
        }
    }

    override fun insert() {}
    override fun update() {}
    override fun delete(id: Long) {}

    private fun CountItem.toUiMap22(
        domain: DomainAnimalCountPriceUi
    ): CountItem {
        return copy(
            id = domain.id,
            count = domain.count,
            suffix = domain.suffix,
            oldCount = domain.count,
            date = domain.date,
            animalId = domain.animalId,
            note = domain.note,
            version = domain.version ?: AnimalCountVersion.ADD,
            price = domain.price?.formatNumber(false) ?: "",
            priceAll = domain.priceAll?.formatNumber() ?: "",
            isAutoCalculate = domain.priceAll != null,
            buyer = domain.buyer ?: "",
            tableId = domain.tableId ?: 0,
            itemIdPT = domain.idPT ?: 0,
            isEntry = false,
            productKillList = domain.productKill,
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
        val dateList = getState().currentProduct.date.split(".")

        val domain = DomainAddTable(
            id = productKill.idProduct,
            title = productKill.title,
            count = productKill.countProduct.toConvertZeroDouble(),
            countSuffix = productKill.suffixProduct,
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
            id = state.tableId,
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
            id = state.tableId,
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
            id = state.tableId,
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
}
//1157