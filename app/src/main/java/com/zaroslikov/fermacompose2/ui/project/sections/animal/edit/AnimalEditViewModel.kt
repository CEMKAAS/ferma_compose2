package com.zaroslikov.fermacompose2.ui.project.sections.animal.edit


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<AnimalEditState, AnimalEntryIntent>(AnimalEditState()) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalEditDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalEditDestination.itemId])


    init {
        viewModelScope.launch {
            loadDataUpdate()
        }
    }

    override fun onIntent(intent: AnimalEntryIntent) {
        return when (intent) {
            is AnimalEntryIntent.TitleChanged -> updateName(intent.value)
            is AnimalEntryIntent.TypeChanged -> updateType(intent.value)
            is AnimalEntryIntent.DateClicked -> updateDate(intent.value)
            is AnimalEntryIntent.DateFactoryClicked -> updateIsDateFactory(intent.value)
            is AnimalEntryIntent.DateFactoryChanged -> updateDateFactory(intent.value)
            is AnimalEntryIntent.FoodDayChanged -> updateFoodDay(intent.value)
            is AnimalEntryIntent.FoodDaySuffixClicked -> updateFoodSuffix(intent.value)
            is AnimalEntryIntent.SexClicked -> updateSex(intent.value)
            AnimalEntryIntent.Update -> update()
        }
    }

    suspend fun loadDataUpdate() {
        updateState { it.copy(isLoading = true) }
        val typeList = animalRepository.getTypeAnimal(itemIdPT).first()
        val domainAnimalTable = animalRepository.getAnimal(itemId)
            .filterNotNull()
            .first()
        updateState { state ->
            state.copy(
                isLoading = false,
                typeList = typeList,
                animalUi = domainAnimalTable.toUi()
            )
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                animalRepository.updateAnimalTable(getState().animalUi.toDomain())
                nextUpdate(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            getState().animalUi.name,
                            getState().animalUi.type,
                            getState().animalUi.note
                        )
                )
            }
        }
    }

    private fun DomainAnimalTable.toUi(): AnimalUi {
        return AnimalUi(
            id = id,
            name = name,
            type = type,
            isDateFactory = dateFactory == null,
            dateBorn = date,
            dateFactory = dateFactory ?: date,
            isAnimalGroup = group,
            sex = sex,
            note = note,
            foodDay = foodDay.formatNumber(),
            foodDaySuffix = foodDaySuffix,
            image = image,
            archive = archive,
            idPT = idPT
        )
    }

    private fun AnimalUi.toDomain(): DomainAnimalTable {
        return DomainAnimalTable(
            id = id,
            name = name,
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
            idPT = idPT
        )
    }


    override fun insert() {}
    override fun delete() {}

    private fun nextUpdate(message: String) {
        navigateTo(UiEvent.NavigateBack)
        showMessage(
            message//Todo Обновить название
        )
    }

    private fun updateName(name: String) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    name = name
                ),
                error = state.error.copy(
                    isErrorTitle = name.isBlank(),
                )
            )
        }
    }

    private fun updateIsDateFactory(isDateFactory: Boolean) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    isDateFactory = isDateFactory
                )
            )
        }
    }

    private fun updateDateFactory(dateFactory: String) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    dateFactory = dateFactory
                )
            )
        }
    }

    private fun updateFoodDay(foodDay: String) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    foodDay = foodDay
                )
            )
        }
    }

    private fun updateFoodSuffix(foodDaySuffix: Suffix) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    foodDaySuffix = foodDaySuffix
                )
            )
        }
    }

    private fun updateSex(sex: Boolean) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    sex = sex
                )
            )
        }
    }

    private fun updateType(type: String) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    type = type
                ),
                error = state.error.copy(
                    isErrorType = type.isBlank(),
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState { state ->
            state.copy(
                animalUi = state.animalUi.copy(
                    dateBorn = date,
                    dateFactory = if (getState().animalUi.isDateFactory) date else ""
                )
            )
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorTitle = state.animalUi.name.isBlank(),
                    isErrorType = state.animalUi.type.isBlank()
                )
            )
        }
    }
}

sealed class AnimalEntryIntent : BaseIntent {
    data class TitleChanged(val value: String) : AnimalEntryIntent()
    data class TypeChanged(val value: String) : AnimalEntryIntent()
    data class SexClicked(val value: Boolean) : AnimalEntryIntent()

    data class DateClicked(val value: String) : AnimalEntryIntent()
    data class DateFactoryClicked(val value: Boolean) : AnimalEntryIntent()
    data class DateFactoryChanged(val value: String) : AnimalEntryIntent()
    data class FoodDayChanged(val value: String) : AnimalEntryIntent()
    data class FoodDaySuffixClicked(val value: Suffix) : AnimalEntryIntent()
    data object Update : AnimalEntryIntent()
}
