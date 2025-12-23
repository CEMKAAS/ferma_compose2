package com.zaroslikov.fermacompose2.ui.animal.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.animal.entry.AnimalEntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.list_screen.SaleListIntent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val expensesRepository: ExpensesRepository,
    private val resourceProvider: ResourceProvider
) : EntryNewViewModel<AnimalListState, AnimalListIntent>(AnimalListState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalDestination.itemIdArg])

    init {
        loadDate()
    }

    override fun onIntent(intent: AnimalListIntent) {
        return when (intent) {
            is AnimalListIntent.AnimalGroupClicked -> updateGroup(intent.value)
            is AnimalListIntent.TitleChanged -> updateTitle(intent.value)
            is AnimalListIntent.TypeChanged -> updateType(intent.value)
            is AnimalListIntent.CountChanged -> updateCount(intent.value)
            is AnimalListIntent.SuffixClicked -> updateSuffix(intent.value)
            is AnimalListIntent.PriceChanged -> updatePrice(intent.value)
            is AnimalListIntent.AutoPriceClicked -> updateIsAutoPrice(intent.value)
            is AnimalListIntent.DateClicked -> updateDate(intent.value)
            is AnimalListIntent.DateFactoryClicked -> updateIsDateFactory(intent.value)
            is AnimalListIntent.DateFactoryChanged -> updateDateFactory(intent.value)
            is AnimalListIntent.FoodDayChanged -> updateFoodDay(intent.value)
            is AnimalListIntent.FoodDaySuffixClicked -> updateFoodDaySuffix(intent.value)
            is AnimalListIntent.NoteChanged -> updateNote(intent.value)
            is AnimalListIntent.SexClicked -> updateSex(intent.value)
            AnimalListIntent.Insert -> insert()
            AnimalListIntent.Update -> update()
            is AnimalListIntent.Delete -> delete(intent.value)

            is AnimalListIntent.CountWarehouse -> {}
            is AnimalListIntent.GroupClicked -> updateGroup(intent.value)
            is AnimalListIntent.OpenBottomSheetEntry -> openBottomSheetEntry(
                intent.value,
                intent.item
            )

            is AnimalListIntent.SearchChanged -> updateSearch(intent.value)
        }
    }

    private fun openBottomSheetEntry(
        openBottomSheetEntry: Boolean,
        domainAnimal: DomainAnimalTable? = null
    ) {
        if (openBottomSheetEntry)
            viewModelScope.launch {
                val typeList = animalRepository.getTypeAnimal(itemIdPT).first()
                updateState {
                    it.copy(
                        openBottomSheetEntry = true,
                        currentProduct = AnimalEntryState2(
                            /* itemIdPT = itemIdPT,*/
                            category = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses),
                            error = ErrorEntryAnimal(),
                            typeList = typeList
                        )
                    )
                }
                /*domainWriteOffTable?.let {
                    val writeOffTable = writeOffRepository.getItemWriteOff(domainWriteOffTable.id)
                        .filterNotNull()
                        .first()

                    updateState {
                        it.copy(
                            currentProduct = it.currentProduct.updateFromDomain(writeOffTable)
                        )
                    }

                    val suffix = getState().currentProduct.titleList
                        .firstOrNull { it.title == getState().currentProduct.title }
                        ?.category

                    suffix?.let {
                        updateWarehouseUiStateSync(getState().currentProduct.title, it)
                    }
                }*/
            }
        else updateState { it.copy(openBottomSheetEntry = false) }
    }

    private fun loadDate() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            animalRepository.getAllAnimal(itemIdPT).collectLatest { list ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateSearch(search: String) {
        updateState {
            it.copy(
                textSearch = search
            )
        }
    }

    private fun updateGroup(isGroup: Boolean) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    isAnimalGroup = isGroup
                )
            )
        }
    }

    private fun updateTitle(title: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    title = title,
                    error = state.currentProduct.error.copy(
                        isErrorTitle = title.isBlank(),
                    )
                )
            )
        }
    }

    private fun updateType(type: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    type = type,
                    error = state.currentProduct.error.copy(
                        isErrorType = type.isBlank(),
                    )
                )
            )
        }
    }

    private fun updateCount(count: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    count = count,
                    error = state.currentProduct.error.copy(
                        isErrorCount = count.isBlank()
                    )
                )
            )
        }
        updatePriceAll()
    }

    private fun updateSuffix(suffix: Suffix) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    countSuffix = suffix,
                )
            )
        }
    }

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    price = price
                )
            )
        }
        updatePriceAll()
    }

    private fun updateIsAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isAutoPrice = isAutoPrice
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    priceAll = if (it.currentProduct.isAutoPrice) (it.currentProduct.price.toConvertZeroDouble() * it.currentProduct.count.toConvertZeroDouble()).formatNumber() else "0"
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    dateBorn = date,
                    dateFactory = if (getState().currentProduct.isDateFactory) date else dateToday()
                )
            )
        }
    }

    private fun updateIsDateFactory(isDateFactory: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isDateFactory = isDateFactory,
                )
            )
        }
    }

    private fun updateDateFactory(dateFactory: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    dateFactory = dateFactory
                )
            )
        }
    }

    private fun updateFoodDay(foodDay: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    foodDay = foodDay
                )
            )
        }
    }

    private fun updateFoodDaySuffix(foodDaySuffix: Suffix) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    foodDaySuffix = foodDaySuffix
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    note = note
                )
            )
        }
    }

    private fun updateSex(sex: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    sex = sex
                )
            )
        }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                val idAnimal = animalRepository.insertAnimalTable(
                    getState().currentProduct.saveAnimal(itemIdPT = itemIdPT)
                )

                val pair = getState().currentProduct.updateForSave(idAnimal, itemIdPT)

                animalCountRepository.insertAnimalCountTable(pair.first)

                pair.second?.let {
                    expensesRepository.insertExpenses(it)
                }
                nextUpdate(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                /*  animalRepository.updateAnimalTable(
                      getState().currentProduct.saveAnimal(
                          itemId,
                          itemIdPT
                      )
                  )*/
                nextUpdate(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().currentProduct.title,
                            getState().currentProduct.count,
                            getState().currentProduct.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            animalRepository.deleteAnimalTable(id)
            nextUpdate(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    )
            )
        }
    }

    private fun nextUpdate(message: String) {
        openBottomSheetEntry(false, null)
        showMessage(
            message//Todo Обновить название
        )
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorTitle = state.currentProduct.title.isBlank(),
                        isErrorType = state.currentProduct.type.isBlank(),
                        isErrorCount = state.currentProduct.count.isBlank(),
                    )
                )
            )
        }
    }

    private fun AnimalEntryState2.saveAnimal(
        id: Long = 0,
        itemIdPT: Long
    ): DomainAnimalTable {
        return DomainAnimalTable(
            id = id,
            name = title,
            type = type,
            date = dateBorn,
            dateFactory = if (isDateFactory) null else dateFactory,
            group = isAnimalGroup,
            sex = sex,
            note = note,
            image = null,
            archive = archive,
            foodDay = if (foodDay.isBlank()) 0.0 else foodDay.toConvertDbDouble(),
            foodDaySuffix = foodDaySuffix,
            idPT = itemIdPT
        )
    }

    private fun AnimalEntryState2.updateForSave(
        idAnimal: Long,
        itemIdPT: Long
    ): Pair<DomainAnimalCount, DomainExpensesTable?> {
        val dateFactory2 = if (isDateFactory) dateBorn else dateFactory
        val dateList = dateFactory2.split(".")
        val countAnimal = if (count.isBlank()) "1" else count

        return DomainAnimalCount(
            count = countAnimal,
            suffix = countSuffix,
            date = if (!isDateFactory) dateFactory else dateBorn,
            idAnimal = idAnimal,
            note = "",
            version = AnimalCountVersion.ADD
        ) to
                if (price.isNotBlank())
                    DomainExpensesTable(
                        title = title,
                        count = countAnimal.toConvertZeroDouble(),
                        day = dateList[0].toInt(),
                        month = dateList[1].toInt(),
                        year = dateList[2].toInt(),
                        price = price.toConvertDbDouble(),
                        priceAll = if (isAutoPrice && isAnimalGroup) priceAll.toConvertDbDouble() else null,
                        countSuffix = countSuffix,
                        category = category,
                        note = "",
                        isShowFood = false,
                        isShowFoodHand = false,
                        isShowWarehouse = false,
                        isShowAnimals = false,
                        feedFood = null,
                        feedFoodSuffix = null,
                        countAnimal = countAnimal.toInt(),
                        foodDesignedDay = null,
                        lastDayFood = null,
                        weight = null,
                        weightSuffix = null,
                        idPT = itemIdPT,
                        animalId = idAnimal,
                        animalVaccinationId = null,
                        animalCountId = null,
                    ) else null
    }
}

sealed class AnimalListIntent : BaseIntent {
    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainAnimalTable? = null
    ) : AnimalListIntent()

    data class AnimalGroupClicked(val value: Boolean) : AnimalListIntent()
    data class TitleChanged(val value: String) : AnimalListIntent()
    data class TypeChanged(val value: String) : AnimalListIntent()
    data class CountChanged(val value: String) : AnimalListIntent()
    data class SuffixClicked(val value: Suffix) : AnimalListIntent()
    data class SexClicked(val value: Boolean) : AnimalListIntent()
    data class PriceChanged(val value: String) : AnimalListIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalListIntent()
    data class DateClicked(val value: String) : AnimalListIntent()
    data class DateFactoryClicked(val value: Boolean) : AnimalListIntent()
    data class DateFactoryChanged(val value: String) : AnimalListIntent()
    data class FoodDayChanged(val value: String) : AnimalListIntent()
    data class FoodDaySuffixClicked(val value: Suffix) : AnimalListIntent()
    data class NoteChanged(val value: String) : AnimalListIntent()

    data object Insert : AnimalListIntent()
    data object Update : AnimalListIntent()
    data class Delete(val value: Long) : AnimalListIntent()

    data class CountWarehouse(val value: List<DomainCountSuffix>) : AnimalListIntent()
    data class GroupClicked(val value: Boolean) : AnimalListIntent()
    data class SearchChanged(val value: String) : AnimalListIntent()
}
