package com.zaroslikov.fermacompose2.ui.animal.animalCard

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.AddRepository
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WarehouseRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.BaseViewModel
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountDifference
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountIncrease
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.isSlash
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.animal.animalCard.AnimalCardState.CountAnimal.ProductKill
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AnimalCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalSizeRepository: AnimalSizeRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalWeightRepository: AnimalWeightRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,
    private val addRepository: AddRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository,
    private val warehouseRepository: WarehouseRepository,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<AnimalCardState, AnimalCardIntent>(AnimalCardState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalCardDestination.itemId])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    fun onIntent(intent: AnimalCardIntent) {
        when (intent) {
            // Animal Card
            is AnimalCardIntent.DialogSoloClicked -> updateSoloDialog(intent.value)
            is AnimalCardIntent.SexClicked -> updateSex(intent.value)
            AnimalCardIntent.SaveGroupPressed -> saveSoloAnimal()

            // Animal Note Animal
            is AnimalCardIntent.NoteChanged -> updateNote(intent.value)

            // Add-Expenses Count Animal
            is AnimalCardIntent.DialogAddClicked -> updateAddDialog(intent.value)
            is AnimalCardIntent.CountAddChanged -> updateCountAdd(intent.count)
            is AnimalCardIntent.PriceAddChanged -> updatePriceActionAnimal(intent.price)
            is AnimalCardIntent.AutoPriceAddClicked -> updateAutoPriceActionAnimal(intent.isAutoPrice)
            is AnimalCardIntent.NoteAddChanged -> updateNoteActionAnimal(intent.note)
            AnimalCardIntent.SaveAddPressed -> saveAddAnimal()

            // Sale Count Animal
            is AnimalCardIntent.DialogSaleClicked -> updateSaleDialog(intent.value)
            is AnimalCardIntent.CountSaleChanged -> updateCountActionAnimal(intent.value)
            is AnimalCardIntent.PriceSaleChanged -> updatePriceSale(intent.value)
            is AnimalCardIntent.AutoPriceSaleClicked -> updateAutoPriceActionAnimal(intent.value)
            is AnimalCardIntent.BuyerSaleChanged -> updateBuyerSale(intent.value)
            AnimalCardIntent.SaveSalePressed -> saveSaleAnimal()

            // WriteOff Count Animal
            is AnimalCardIntent.DialogWriteOffClicked -> updateWriteOffDialog(intent.value)
            is AnimalCardIntent.CountWriteOffChanged -> updateCountActionAnimal(intent.value)
            is AnimalCardIntent.PriceWriteOffChanged -> updatePriceActionAnimal(intent.value)
            is AnimalCardIntent.AutoPriceWriteOffClicked -> updateAutoPriceActionAnimal(intent.value)
            is AnimalCardIntent.NoteWriteOffChanged -> updateNoteActionAnimal(intent.value)
            AnimalCardIntent.SaveWriteOffPressed -> saveWriteOffAnimal()

            // Kill Count Animal
            is AnimalCardIntent.DialogKillClicked -> updateKillDialog(intent.value)
            is AnimalCardIntent.CountKillChanged -> updateCountActionAnimal(intent.value)
            is AnimalCardIntent.TitleProductKillChanged -> updateTitleProductKill(
                intent.index,
                intent.value
            )

            is AnimalCardIntent.TitleAndSuffixKillClicked -> updateTitleAndSuffixProductKill(
                intent.index,
                intent.pair
            )

            is AnimalCardIntent.CountProductKillChanged -> updateCountProductKill(
                intent.index,
                intent.value
            )

            is AnimalCardIntent.SuffixProductKillChanged -> updateSuffixProductKill(
                intent.index,
                intent.value
            )

            AnimalCardIntent.AddProductKillChanged -> addProductKill()
            is AnimalCardIntent.RemoveProductKillChanged -> removeProductKill(intent.index)
            AnimalCardIntent.SaveKillChanged -> saleKillAnimal()
        }
    }

    private suspend fun loadData() {
        combine(
            animalRepository.getAnimal(itemId),
            animalCountRepository.getCountAnimalLimit(itemId),
            animalSizeRepository.getSizeAnimalLimit(itemId),
            animalVaccinationRepository.getVaccinationAnimalLimit(itemId),
            animalWeightRepository.getWeightAnimalLimit(itemId)
        ) { animal, count, size, vacc, weight ->
            AnimalData(animal, count, size, vacc, weight)
        }.collectLatest { data ->
            updateState { animal ->
                animal.copy(
                    isLoading = false,
                    animal = data.animal,
                    countAnimal = data.count,
                    size = data.size,
                    vaccination = data.vacc,
                    weight = data.weight,
                    actionAnimal = animal.actionAnimal.copy(
                        countAnimal = if (data.animal.group) "" else "1",
                        suffixAnimal = data.count.suffix,
                    ),
                    itemId = itemId,
                    itemIdPT = itemIdPT
                )
            }
        }
    }

    private fun updateNote(note: String) {
        viewModelScope.launch {
            updateState { state ->
                state.copy(
                    animal = state.animal.copy(
                        note = note
                    )
                )
            }
            animalRepository.updateAnimalTable(getState().animal)
        }
    }

    // Action Animal
    private fun updateCountActionAnimal(countAnimal: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    countAnimal = countAnimal,
                    error = state.actionAnimal.error.copy(
                        isErrorCount = countAnimal.isBlank(),
                        isErrorCountMore = isAnimalCountIncrease(
                            countAnimal,
                            getState().countAnimal.count
                        ),
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
                actionAnimal = state.actionAnimal.copy(
                    price = price
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateAutoPriceActionAnimal(isAutoPrice: Boolean) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateNoteActionAnimal(note: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    note = note
                )
            )
        }
    }

    private fun  updatePriceAllActionAnimal() {
        updateState {
            it.copy(
                actionAnimal = it.actionAnimal.copy(
                    priceAll = if (it.actionAnimal.isAutoPrice) (it.actionAnimal.price.toConvertZeroDouble() * it.actionAnimal.countAnimal.toConvertZeroDouble()).formatNumber() else "0",
                )
            )
        }
    }

    // Add-Expenses Count Animal
    private fun updateCountAdd(countAnimal: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    countAnimal = countAnimal,
                    error = state.actionAnimal.error.copy(
                        isErrorCount = countAnimal.isBlank()
                    )
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun validationAdd() {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    actionWithAnimal = ActionWithAnimal.ADD_ANIMAL,
                    error = state.actionAnimal.error.copy(
                        isErrorCount = state.actionAnimal.countAnimal.isBlank(),
                    )
                )
            )
        }
    }

    private fun validationSale() {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    actionWithAnimal = ActionWithAnimal.SALE_ANIMAL,
                    error = state.actionAnimal.error.copy(
                        isErrorPrice = state.actionAnimal.price.isBlank(),
                        isErrorCount = state.actionAnimal.countAnimal.isBlank(),
                        isErrorCountZero = isAnimalCountZero(state.actionAnimal.countAnimal),
                        isErrorCountMore = isAnimalCountIncrease(
                            state.actionAnimal.countAnimal, state.countAnimal.count
                        )
                    )
                )
            )
        }
    }

    private fun validationWriteOff() {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    actionWithAnimal = ActionWithAnimal.WRITE_OFF_ANIMAL,
                    error = state.actionAnimal.error.copy(
                        isErrorCount = state.actionAnimal.countAnimal.isBlank(),
                        isErrorCountZero = isAnimalCountZero(state.actionAnimal.countAnimal),
                        isErrorCountMore = isAnimalCountIncrease(
                            state.actionAnimal.countAnimal, state.countAnimal.count
                        )
                    )
                )
            )
        }
    }

    private fun validationKill() {
        val updatedProductKill = getState().actionAnimal.productKill.map { product ->
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
                actionAnimal = state.actionAnimal.copy(
                    actionWithAnimal = ActionWithAnimal.KILL_ANIMAL,
                    productKill = updatedProductKill,
                    error = state.actionAnimal.error.copy(
                        isErrorCount = state.actionAnimal.countAnimal.isBlank(),
                        isErrorCountZero = isAnimalCountZero(state.actionAnimal.countAnimal),
                        isErrorCountMore = isAnimalCountIncrease(
                            state.actionAnimal.countAnimal, state.countAnimal.count
                        )
                    )
                )
            )
        }
    }

    fun saveAddAnimal() {
        viewModelScope.launch {
            validationAdd()
            if (getState().actionAnimal.hasAnyError) {
                val state = getState().actionAnimal
                val reasonNote = resourceProvider.getString(
                    if (state.note.isBlank())
                        R.string.animal_card_screen_add_no_note_reason
                    else
                        R.string.animal_card_screen_add_note_reason
                ).format(state.note)

                val domainCountAnimal = DomainAnimalCount(
                    count = state.countAnimal,
                    suffix = state.suffixAnimal,
                    date = dateToday(),
                    idAnimal = itemId,
                    note = reasonNote,
                    version = if (state.price.isBlank()) AnimalCountVersion.ADD else AnimalCountVersion.EXPENSES
                )

                val countId = animalCountRepository.insertAnimalCountTable(domainCountAnimal)
                animalRepository.updateAnimalTable(getState().animal.copy(group = true))

                // Сохраняем расходы если есть цена
                if (state.price.isNotBlank())
                    expensesRepository.insertExpenses(
                        DomainExpensesTable(
                            title = getState().animal.name,
                            count = state.countAnimal.toConvertDbDouble(),
                            day = dateTodayArray()[0],
                            month = dateTodayArray()[1],
                            year = dateTodayArray()[2],
                            price = state.price.toConvertDbDouble(),
                            priceAll = if (state.isAutoPrice) state.priceAll.toConvertDbDouble() else null,
                            countSuffix = state.suffixAnimal,
                            category = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses),
                            note = resourceProvider.getString(R.string.animal_card_screen_note_expenses),
                            isShowFood = false,
                            isShowWarehouse = false,
                            isShowAnimals = false,
                            isShowFoodHand = false,
                            idPT = itemIdPT,
                            animalId = itemId,
                            animalCountId = countId
                        )
                    )
                updateAddDialog(false)
            }
        }
    }

    // Sale Count Animal
    private fun updateAddDialog(openDialog: Boolean) {
        updateState { state ->
            state.copy(
                openAddDialog = openDialog,
                actionAnimal = state.actionAnimal.reset()
            )
        }
    }

    private fun updateSaleDialog(openDialog: Boolean) {
        viewModelScope.launch {
            val buyer =
                if (openDialog) saleRepository.getItemsBuyerSaleList(itemIdPT)
                    .first() else getState().buyerList

            updateState { state ->
                state.copy(
                    openSaleDialog = openDialog,
                    buyerList = buyer,
                    actionAnimal = state.actionAnimal.reset()
                )
            }
        }
    }

    private fun updateWriteOffDialog(openDialog: Boolean) {
        updateState { state ->
            state.copy(
                openWriteOffDialog = openDialog, actionAnimal = state.actionAnimal.reset()
            )
        }
    }

    private fun updateKillDialog(openDialog: Boolean) {
        viewModelScope.launch {
            val titleList = if (openDialog)
                addRepository.getItemsTitleAddList(itemIdPT).firstOrNull() ?: emptyList()
            else
                emptyList()

            val suffixProduct =
                getState().weight?.suffix ?: Suffix.KILOGRAM


            updateState { state ->
                state.copy(
                    openKillDialog = openDialog,
                    actionAnimal = state.actionAnimal.reset(titleList, suffixProduct)
                )
            }
        }
    }

    private fun updatePriceSale(price: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    price = price,
                    error = state.actionAnimal.error.copy(
                        isErrorPrice = price.isBlank()
                    )
                )
            )
        }
        updatePriceAllActionAnimal()
    }

    private fun updateBuyerSale(buyer: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    buyer = buyer
                )
            )
        }
    }

    fun saveSaleAnimal() {
        viewModelScope.launch {
            validationSale()
            if (getState().actionAnimal.hasAnyError) {
                val state = getState().actionAnimal
                val countAnimalAll = getState().countAnimal.count
                val countId = animalCountRepository.insertAnimalCountTable(
                    DomainAnimalCount(
                        count = state.countAnimal,
                        suffix = state.suffixAnimal,
                        date = dateToday(),
                        idAnimal = itemId,
                        note = "",
                        version = AnimalCountVersion.SALE
                    )
                )
                saleRepository.insertSale(
                    DomainSaleTable(
                        title = getState().animal.name,
                        count = state.countAnimal.toConvertDbDouble(),
                        countSuffix = state.suffixAnimal,
                        price = state.price.toConvertDbDouble(),
                        priceAll = if (state.isAutoPrice) state.priceAll.toConvertDbDouble() else null,
                        day = dateTodayArray()[0],
                        month = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        category = resourceProvider.getString(R.string.animal_card_screen_category_sale),
                        buyer = if (state.buyer.isBlank()) null else state.buyer,
                        note = resourceProvider.getString(R.string.animal_card_screen_note_sale),
                        idPT = itemIdPT,
                        animalId = itemId,
                        animalCountId = countId
                    )
                )
                endSave(
                    state.countAnimal,
                    countAnimalAll
                ) { updateSaleDialog(false) }
            }
        }
    }

    fun saveWriteOffAnimal() {
        viewModelScope.launch {
            validationWriteOff()
            if (getState().actionAnimal.hasAnyError) {
                val state = getState().actionAnimal
                val countAnimalAll = getState().countAnimal.count
                val reasonNote = resourceProvider.getString(
                    if (state.note.isBlank())
                        R.string.animal_card_screen_add_no_note_reason
                    else
                        R.string.animal_card_screen_add_note_reason
                ).format(state.note)

                val countId = animalCountRepository.insertAnimalCountTable(
                    DomainAnimalCount(
                        count = state.countAnimal,
                        suffix = state.suffixAnimal,
                        date = dateToday(),
                        idAnimal = itemId,
                        note = reasonNote,
                        version = AnimalCountVersion.WRITE_OFF
                    )
                )
                writeOffRepository.insertWriteOff(
                    DomainWriteOffTable(
                        title = getState().animal.name,
                        count = state.countAnimal.toConvertDbDouble(),
                        countSuffix = state.suffixAnimal,
                        price = if (state.price.isNotBlank()) state.price.toConvertDbDouble() else null,
                        priceAll = if (state.isAutoPrice) state.priceAll.toConvertDbDouble() else null,
                        day = dateTodayArray()[0],
                        month = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        status = true,
                        note = resourceProvider.getString(R.string.animal_card_screen_note_sale),
                        idPT = itemIdPT,
                        animalCountId = countId
                    )
                )
                endSave(state.countAnimal, countAnimalAll) { updateWriteOffDialog(false) }
            }
        }
    }

    private fun saleKillAnimal() {
        viewModelScope.launch {
            validationKill()
            if (getState().actionAnimal.hasAnyError && getState().actionAnimal.hasFieldError) {
                Log.i("animal", "saleKillAnimal: true")
                val state = getState().actionAnimal
                val countAnimalAll = getState().countAnimal.count
                val productList = state.productKill

                val reasonNote =
                    resourceProvider.getString(R.string.animal_card_screen_kill_add_product)
                val resultText = reasonNote + productList.mapIndexed { index, it ->
                    "${index + 1}. ${it.title} - ${it.countProduct} ${it.suffixProduct}"
                }.joinToString("\n")

                val countId = animalCountRepository.insertAnimalCountTable(
                    DomainAnimalCount(
                        count = state.countAnimal,
                        suffix = state.suffixAnimal,
                        date = dateToday(),
                        idAnimal = itemId,
                        note = resultText,
                        version = AnimalCountVersion.KILL
                    )
                )
                writeOffRepository.insertWriteOff(
                    DomainWriteOffTable(
                        title = getState().animal.name,
                        count = state.countAnimal.toConvertDbDouble(),
                        countSuffix = state.suffixAnimal,
                        day = dateTodayArray()[0],
                        month = dateTodayArray()[1],
                        year = dateTodayArray()[2],
                        status = false,
                        note = resultText,
                        idPT = itemIdPT,
                        animalCountId = countId
                    )
                )
                productList.forEach { product ->
                    addRepository.insertAdd(
                        DomainAddTable(
                            title = product.title,
                            count = product.countProduct.toConvertZeroDouble(),
                            countSuffix = product.suffixProduct,
                            day = dateTodayArray()[0],
                            month = dateTodayArray()[1],
                            year = dateTodayArray()[2],
                            category = resourceProvider.getString(R.string.animal_card_screen_category_kill),
                            note = resourceProvider.getString(R.string.animal_card_screen_note_kill),
                            price = 0.0,
                            idPT = itemIdPT,
                            animalId = itemId,
                        )
                    )
                }
                endSave(state.countAnimal, countAnimalAll) { updateKillDialog(false) }
            }
        }
    }

    private fun endSave(
        countAnimal: String,
        countAnimalAll: String,
        updateDialog: () -> Unit
    ) {
        val count = isAnimalCountDifference(countAnimal, countAnimalAll).toInt()
        val isAnimalGroup = getState().animal.group

        if (count == 0)
            nextUpdate("Животное перенесено в архив")
        else {
            updateDialog()
            updateSoloDialog(count == 1 && isAnimalGroup)
        }
    }

    private fun nextUpdate(message: String) {
        navigateTo(UiEvent.NavigateBack)
        showMessage(message)
    }

    // Kill Animal Count
    private fun updateTitleProductKill(index: Int, newTitle: String) {
        viewModelScope.launch {
            val warehouseList = updateWarehouseUiStateSync(newTitle)
            updateState { state ->
                state.copy(
                    actionAnimal = state.actionAnimal.copy(
                        productKill = state.actionAnimal.productKill.mapIndexed { i, item ->
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
                    actionAnimal = state.actionAnimal.copy(
                        productKill = state.actionAnimal.productKill.mapIndexed { i, item ->
                            if (i == index)
                                item.copy(
                                    title = newTitleAndSuffix.first,
                                    suffixProduct = newTitleAndSuffix.second,
                                    warehouseList = warehouseList
                                )
                            else item
                        }
                    )
                )
            }
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String): List<DomainCountSuffix> {
        return warehouseRepository
            .getCurrentBalanceProductList(name, itemIdPT.toLong())
            .filterNotNull()
            .firstOrNull() ?: emptyList()
    }

    private fun updateCountProductKill(index: Int, newCount: String) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    productKill = state.actionAnimal.productKill.mapIndexed { i, item ->
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
            )
        }
    }

    private fun updateSuffixProductKill(index: Int, newSuffix: Suffix) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    productKill = state.actionAnimal.productKill.mapIndexed { i, item ->
                        if (i == index) item.copy(suffixProduct = newSuffix)
                        else item
                    }
                )
            )
        }
    }

    private fun addProductKill() {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    productKill = state.actionAnimal.productKill + ProductKill(
                        suffixProduct = getState().weight?.suffix ?: Suffix.KILOGRAM
                    )
                )
            )
        }
    }

    private fun removeProductKill(index: Int) {
        updateState { state ->
            state.copy(
                actionAnimal = state.actionAnimal.copy(
                    productKill = state.actionAnimal.productKill.toMutableList().also {
                        if (index in it.indices) it.removeAt(index)
                    }
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
        showMessage("Пол изменен") //TODO Изменить название
    }
}

sealed class AnimalCardIntent : BaseIntent {
    // Animal Card
    data class DialogSoloClicked(val value: Boolean) : AnimalCardIntent()
    data class SexClicked(val value: Boolean) : AnimalCardIntent()
    data object SaveGroupPressed : AnimalCardIntent()

    // Animal Note Animal
    data class NoteChanged(val value: String) : AnimalCardIntent()

    // Add-Expenses Count Animal
    data class DialogAddClicked(val value: Boolean) : AnimalCardIntent()
    data class CountAddChanged(val count: String) : AnimalCardIntent()
    data class PriceAddChanged(val price: String) : AnimalCardIntent()
    data class AutoPriceAddClicked(val isAutoPrice: Boolean) : AnimalCardIntent()
    data class NoteAddChanged(val note: String) : AnimalCardIntent()
    data object SaveAddPressed : AnimalCardIntent()

    // Sale Count Animal
    data class DialogSaleClicked(val value: Boolean) : AnimalCardIntent()
    data class CountSaleChanged(val value: String) : AnimalCardIntent()
    data class PriceSaleChanged(val value: String) : AnimalCardIntent()
    data class AutoPriceSaleClicked(val value: Boolean) : AnimalCardIntent()
    data class BuyerSaleChanged(val value: String) : AnimalCardIntent()
    data object SaveSalePressed : AnimalCardIntent()

    // WriteOff Count Animal
    data class DialogWriteOffClicked(val value: Boolean) : AnimalCardIntent()
    data class CountWriteOffChanged(val value: String) : AnimalCardIntent()
    data class PriceWriteOffChanged(val value: String) : AnimalCardIntent()
    data class AutoPriceWriteOffClicked(val value: Boolean) : AnimalCardIntent()
    data class NoteWriteOffChanged(val value: String) : AnimalCardIntent()
    data object SaveWriteOffPressed : AnimalCardIntent()

    // Kill Count Animal
    data class DialogKillClicked(val value: Boolean) : AnimalCardIntent()
    data class CountKillChanged(val value: String) : AnimalCardIntent()
    data class TitleProductKillChanged(val index: Int, val value: String) : AnimalCardIntent()
    data class TitleAndSuffixKillClicked(val index: Int, val pair: Pair<String, Suffix>) :
        AnimalCardIntent()

    data class CountProductKillChanged(val index: Int, val value: String) : AnimalCardIntent()
    data class SuffixProductKillChanged(val index: Int, val value: Suffix) : AnimalCardIntent()
    data object AddProductKillChanged : AnimalCardIntent()
    data class RemoveProductKillChanged(val index: Int) : AnimalCardIntent()
    data object SaveKillChanged : AnimalCardIntent()
}

data class AnimalData(
    val animal: DomainAnimalTable,
    val count: DomainAnimalCount,
    val size: DomainAnimalSize?,
    val vacc: DomainAnimalVaccination?,
    val weight: DomainAnimalWeight?
)