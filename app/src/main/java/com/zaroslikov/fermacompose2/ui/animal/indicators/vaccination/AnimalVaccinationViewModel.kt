package com.zaroslikov.fermacompose2.ui.animal.indicators.vaccination

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.dateTodayNextYear
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalVaccinationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalCountAnimalRepository: AnimalCountRepository,
    private val animalRepository: AnimalRepository,
    private val animalVaccinationRepository: AnimalVaccinationRepository,
    private val expensesRepository: ExpensesRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<AnimalVaccinationState, AnimalVaccinationIntent>(AnimalVaccinationState()) {
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalVaccinationDestination.itemId])
    private val itemIdPT: Long =
        checkNotNull(savedStateHandle[AnimalVaccinationDestination.itemIdPT])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    override fun onIntent(intent: AnimalVaccinationIntent) {
        return when (intent) {
            is AnimalVaccinationIntent.OpenDialogClicked -> updateOpenDialog(
                intent.isEntry,
                intent.domainAnimalVaccination
            )

            AnimalVaccinationIntent.EndDialogClicked -> updateEndDialog()
            is AnimalVaccinationIntent.CountChanged -> updateCount(intent.value)
            is AnimalVaccinationIntent.PriceChanged -> updatePrice(intent.value)
            is AnimalVaccinationIntent.AutoPriceClicked -> updateAutoPrice(intent.value)
            is AnimalVaccinationIntent.VaccinationChanged -> updateVaccination(intent.value)
            is AnimalVaccinationIntent.DateClicked -> updateDate(intent.value)
            is AnimalVaccinationIntent.DateNextClicked -> updateDateNext(intent.value)
            is AnimalVaccinationIntent.NoteChanged -> updateNote(intent.value)
            AnimalVaccinationIntent.InsertPressed -> insert()
            AnimalVaccinationIntent.UpdatePressed -> update()
            AnimalVaccinationIntent.DeletePressed -> delete()
        }
    }

    suspend fun loadData() {
        updateState { it.copy(isLoading = true) }
        val animalGroup = animalRepository.getAnimal(itemId).first().group
        val animalCount = animalCountAnimalRepository.getCountAnimalLimit(itemId).first()
        updateState {
            it.copy(
                isAnimalGroup = animalGroup,
                countAnimalAll = animalCount.count,
                suffixAnimal = animalCount.suffix
            )
        }
        combine(
            animalVaccinationRepository.getTitleVaccinationAnimalList(itemId),
            animalVaccinationRepository.getVaccinationAnimal(itemId)
        ) { titleVaccinationList, vaccination ->
            titleVaccinationList to vaccination
        }.collectLatest { data ->
            updateState {
                it.copy(
                    vaccinationList = data.second,
                    titleVaccinationList = data.first,
                    idPT = itemIdPT,
                    isLoading = false
                )
            }
        }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                val vaccination = getState().vaccination
                val domainVaccination = getState().vaccination.domainAnimalVaccination.copy(
                    idAnimal = itemId
                )
                val vaccinationId =
                    animalVaccinationRepository.insertAnimalVaccinationTable(domainVaccination)

                if (vaccination.price.isNotBlank()) {
                    val dateList = domainVaccination.date.split(".")
                    val note = // TODO Не знаю что написать
                        resourceProvider.getString(R.string.animal_indicators_animals_vaccination_no_price_s)
                    val category = resourceProvider.getString(R.string.vaccination_screen_title)
                    expensesRepository.insertExpenses(
                        DomainExpensesTable(
                            title = vaccination.domainAnimalVaccination.vaccination,
                            count = if (getState().isAnimalGroup) vaccination.countAnimal.toConvertZeroDouble() else getState().countAnimalAll.toConvertZeroDouble(),
                            day = dateList[0].toInt(),
                            month = dateList[1].toInt(),
                            year = dateList[2].toInt(),
                            price = vaccination.price.toConvertZeroDouble(),
                            priceAll = if (vaccination.isAutoCalculate) vaccination.priceAll.toConvertZeroDouble() else null,
                            countSuffix = getState().suffixAnimal,
                            category = category,
                            note = vaccination.domainAnimalVaccination.note,
                            isShowFood = false,
                            isShowFoodHand = false,
                            isShowWarehouse = false,
                            isShowAnimals = false,
                            idPT = itemIdPT,
                            animalVaccinationId = vaccinationId
                        )
                    )
                }
                updateEndDialog()
                showMessage("Добавлен размер")
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                animalVaccinationRepository.updateAnimalVaccinationTable(getState().vaccination.domainAnimalVaccination)

                /*if (vaccination.price.isNotBlank()) {
                    val dateList = domainVaccination.date.split(".")
                    val note = // TODO Не знаю что написать
                        resourceProvider.getString(R.string.animal_indicators_animals_vaccination_no_price_s)
                    val category = resourceProvider.getString(R.string.vaccination_screen_title)
                    expensesRepository.insertExpenses(
                        DomainExpensesTable(
                            title = vaccination.domainAnimalVaccination.vaccination,
                            count = if (getState().isAnimalGroup) vaccination.countAnimal.toConvertZeroDouble() else getState().countAnimalAll.toConvertZeroDouble(),
                            day = dateList[0].toInt(),
                            month = dateList[1].toInt(),
                            year = dateList[2].toInt(),
                            price = vaccination.price.toConvertZeroDouble(),
                            priceAll = if (vaccination.isAutoCalculate) vaccination.priceAll.toConvertZeroDouble() else null,
                            countSuffix = getState().suffixAnimal,
                            category = category,
                            note = vaccination.domainAnimalVaccination.note,
                            isShowFood = false,
                            isShowFoodHand = false,
                            isShowWarehouse = false,
                            isShowAnimals = false,
                            idPT = itemIdPT,
                            animalVaccinationId = vaccinationId
                        )
                    )
                }*/
                updateEndDialog()
                showMessage("Редактировать размер")
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            animalVaccinationRepository.deleteAnimalVaccinationTable(getState().vaccination.domainAnimalVaccination)
            updateEndDialog()
            showMessage("Удалить размер")
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                vaccination = state.vaccination.copy(
                    error = state.error.copy(
                        isErrorVaccination = state.vaccination.domainAnimalVaccination.vaccination.isBlank(),
                        isErrorCount = if (state.isAnimalGroup) state.vaccination.countAnimal.isBlank() else false,
                        isErrorCountZero = if (state.isAnimalGroup) isAnimalCountZero(state.vaccination.countAnimal) else false,
                    )
                )
            )
        }
    }

    private fun updateOpenDialog(
        isEntry: Boolean,
        domainAnimalVaccination: DomainAnimalVaccination
    ) {
        viewModelScope.launch {
            val expensesVaccination = if (!isEntry)
                expensesRepository.getItemExpensesForVaccination(domainAnimalVaccination.id)
                    .firstOrNull()
            else null

            updateState {
                it.copy(
                    isOpenDialog = true,
                    isEntry = isEntry,
                    vaccination = it.vaccination.copy(
                        domainAnimalVaccination = domainAnimalVaccination,
                        countAnimal = expensesVaccination?.count?.formatNumber(false) ?: "",
                        price = expensesVaccination?.price?.formatNumber(false) ?: "",
                        priceAll = expensesVaccination?.priceAll?.formatNumber(false) ?: "",
                        isAutoCalculate = expensesVaccination?.priceAll != null
                    )
                )
            }
        }
    }

    private fun updateEndDialog() {
        updateState {
            it.copy(
                isOpenDialog = false,
                vaccination = AnimalVaccinationState.Vaccination()
            )
        }
    }

    private fun updateVaccination(vaccination: String) {
        updateState {
            it.copy(
                vaccination = it.vaccination.copy(
                    domainAnimalVaccination = it.vaccination.domainAnimalVaccination.copy(
                        vaccination = vaccination
                    )
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                vaccination = it.vaccination.copy(
                    domainAnimalVaccination = it.vaccination.domainAnimalVaccination.copy(
                        date = date
                    )
                )
            )
        }
    }

    private fun updateDateNext(dateNext: String) {
        updateState {
            it.copy(
                vaccination = it.vaccination.copy(
                    domainAnimalVaccination = it.vaccination.domainAnimalVaccination.copy(
                        nextVaccination = dateNext
                    )
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                vaccination = it.vaccination.copy(
                    domainAnimalVaccination = it.vaccination.domainAnimalVaccination.copy(
                        note = note
                    )
                )
            )
        }
    }

    private fun updateCount(count: String) {
        updateState {
            it.copy(
                vaccination = it.vaccination.copy(
                    countAnimal = count
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePrice(price: String) {
        updateState {
            it.copy(
                vaccination = it.vaccination.copy(
                    price = price
                )
            )
        }
        updatePriceAll()
    }

    private fun updateAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                vaccination = it.vaccination.copy(
                    isAutoCalculate = isAutoPrice
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState { state ->
            state.copy(
                vaccination = state.vaccination.copy(
                    priceAll = if (state.vaccination.isAutoCalculate)
                        (state.vaccination.price.toConvertZeroDouble() * state.vaccination.countAnimal.toConvertZeroDouble()).formatNumber()
                    else "0"
                )
            )
        }
    }
}

sealed class AnimalVaccinationIntent : BaseIntent {
    data class OpenDialogClicked(
        val isEntry: Boolean,
        val domainAnimalVaccination: DomainAnimalVaccination = DomainAnimalVaccination(
            date = dateToday(),
            nextVaccination = dateTodayNextYear(),
        )
    ) : AnimalVaccinationIntent()

    data object EndDialogClicked : AnimalVaccinationIntent()
    data class VaccinationChanged(val value: String) : AnimalVaccinationIntent()
    data class CountChanged(val value: String) : AnimalVaccinationIntent()
    data class PriceChanged(val value: String) : AnimalVaccinationIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalVaccinationIntent()
    data class DateClicked(val value: String) : AnimalVaccinationIntent()
    data class DateNextClicked(val value: String) : AnimalVaccinationIntent()
    data class NoteChanged(val value: String) : AnimalVaccinationIntent()
    data object InsertPressed : AnimalVaccinationIntent()
    data object UpdatePressed : AnimalVaccinationIntent()
    data object DeletePressed : AnimalVaccinationIntent()
}