package com.zaroslikov.fermacompose2.ui.animal.entry


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expensesRepository: ExpensesRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val animalRepository: AnimalRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<AnimalEntryState, AnimalEntryIntent>(AnimalEntryState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalEntryDestination.itemId])
    private val isEntry: Boolean = itemId == -1L

    init {
        viewModelScope.launch {
            loadData()
            loadDataUpdate()
        }
    }

    override fun onIntent(intent: AnimalEntryIntent) {
        return when (intent) {
            is AnimalEntryIntent.AnimalGroupClicked -> updateState { it.copy(isAnimalGroup = intent.value) }
            is AnimalEntryIntent.TitleChanged -> updateTitle(intent.value)
            is AnimalEntryIntent.TypeChanged -> updateType(intent.value)
            is AnimalEntryIntent.CountChanged -> updateCount(intent.value)
            is AnimalEntryIntent.PriceChanged -> updatePrice(intent.value)
            is AnimalEntryIntent.AutoPriceClicked -> updateIsAutoPrice(intent.value)
            is AnimalEntryIntent.DateClicked -> updateDate(intent.value)
            is AnimalEntryIntent.DateFactoryClicked -> updateState { it.copy(isDateFactory = intent.value) }
            is AnimalEntryIntent.DateFactoryChanged -> updateState { it.copy(dateFactory = intent.value) }
            is AnimalEntryIntent.FoodDayChanged -> updateState { it.copy(foodDay = intent.value) }
            is AnimalEntryIntent.FoodDaySuffixClicked -> updateState { it.copy(foodDaySuffix = intent.value) }
            is AnimalEntryIntent.NoteChanged -> updateState { it.copy(note = intent.value) }
            is AnimalEntryIntent.SexClicked -> updateState { it.copy(sex = intent.value) }
            AnimalEntryIntent.Insert -> insert()
            AnimalEntryIntent.Update -> update()
            AnimalEntryIntent.Delete -> delete()
        }
    }

    suspend fun loadData() {
        val typeList = animalRepository.getTypeAnimal(itemIdPT).first()
        updateState {
            it.copy(
                isEntry = isEntry,
                countSuffix = Suffix.PIECES,
                foodDaySuffix = Suffix.GRAM,
                category = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses),
                typeList = typeList
            )
        }
    }

    suspend fun loadDataUpdate() {
        if (!isEntry) {
            val domainAnimalTable = animalRepository.getAnimal(itemId)
                .filterNotNull()
                .first()
            updateState { it.updateFromDomain(domainAnimalTable) }
        }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {

                val idAnimal = animalRepository.insertAnimalTable(
                    getState().saveAnimal(itemIdPT = itemIdPT)
                )

                val pair = getState().updateForSave(idAnimal, itemIdPT)

                animalCountRepository.insertAnimalCountTable(pair.first)

                pair.second?.let {
                    expensesRepository.insertExpenses(it)
                }
                /*
                    metricaWriteOff(writeOffUiState.copy(priceAll = autoCalculate()))
                 */
                nextUpdate(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                animalRepository.updateAnimalTable(getState().saveAnimal(itemId, itemIdPT))
                nextUpdate(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().title,
                            getState().count,
                            getState().countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            animalRepository.deleteAnimalTable(itemId)
            nextUpdate(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().title,
                        getState().count,
                        getState().countSuffix
                    )
            )
        }
    }

    private fun nextUpdate(message: String) {
        navigateTo(UiEvent.NavigateBack)
        showMessage(
            message//Todo Обновить название
        )
    }

    private fun updateTitle(title: String) {
        updateState { state ->
            state.copy(
                title = title,
                error = state.error.copy(
                    isErrorTitle = title.isBlank(),
                )
            )
        }
    }

    private fun updateType(type: String) {
        updateState { state ->
            state.copy(
                type = type,
                error = state.error.copy(
                    isErrorType = type.isBlank(),
                )
            )
        }
    }

    private fun updateCount(count: String) {
        updateState { state ->
            state.copy(
                count = count,
                error = state.error.copy(
                    isErrorCount = count.isBlank()
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                price = price
            )
        }
        updatePriceAll()
    }

    private fun updateIsAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                isAutoPrice = isAutoPrice
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState {
            it.copy(
                priceAll = if (getState().isAutoPrice) (getState().price.toConvertZeroDouble() * getState().count.toConvertZeroDouble()).formatNumber() else "0"

            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                dateBorn = date,
                dateFactory = if (getState().isDateFactory) date else ""
            )
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorTitle = state.title.isBlank(),
                    isErrorType = state.type.isBlank(),
                    isErrorCount = state.count.isBlank(),
                )
            )
        }
    }
}

sealed class AnimalEntryIntent : BaseIntent {
    data class AnimalGroupClicked(val value: Boolean) : AnimalEntryIntent()
    data class TitleChanged(val value: String) : AnimalEntryIntent()
    data class TypeChanged(val value: String) : AnimalEntryIntent()
    data class CountChanged(val value: String) : AnimalEntryIntent()
    data class SexClicked(val value: Boolean) : AnimalEntryIntent()
    data class PriceChanged(val value: String) : AnimalEntryIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalEntryIntent()
    data class DateClicked(val value: String) : AnimalEntryIntent()
    data class DateFactoryClicked(val value: Boolean) : AnimalEntryIntent()
    data class DateFactoryChanged(val value: String) : AnimalEntryIntent()
    data class FoodDayChanged(val value: String) : AnimalEntryIntent()
    data class FoodDaySuffixClicked(val value: Suffix) : AnimalEntryIntent()
    data class NoteChanged(val value: String) : AnimalEntryIntent()
    data object Insert : AnimalEntryIntent()
    data object Update : AnimalEntryIntent()
    data object Delete : AnimalEntryIntent()
}
