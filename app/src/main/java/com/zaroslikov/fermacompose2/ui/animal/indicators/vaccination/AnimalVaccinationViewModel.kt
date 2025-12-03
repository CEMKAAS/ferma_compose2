package com.zaroslikov.fermacompose2.ui.animal.indicators.vaccination

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.dateTodayNextYear
import com.zaroslikov.fermacompose2.supportFun.isAnimalCountZero
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
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
) : EntryNewViewModel<AnimalVaccinationState, AnimalVaccinationIntent>(
    AnimalVaccinationState()
) {
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
            is AnimalVaccinationIntent.DeletePressed -> delete(intent.value)
            is AnimalVaccinationIntent.DateFactoryClicked -> updateIsDateFactory(intent.value)
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
            animalVaccinationRepository.getVaccinationExpensesAnimal(itemId)
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
                val vaccination = getState().currentProduct
                val vaccinationId =
                    animalVaccinationRepository.insertAnimalVaccinationTable(vaccination.toDomainMap())
                if (vaccination.price.isNotBlank())
                    expensesRepository.insertExpenses(vaccination.toUiMap222(vaccinationId))
                updateEndDialog()
                showMessage("Добавлен размер")
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                val vaccination = getState().currentProduct
                Log.i("vaccination2", "update: ${vaccination.toDomainMap()}")
                animalVaccinationRepository.updateAnimalVaccinationTable(vaccination.toDomainMap())
                when {
                    vaccination.idExpenses == null && vaccination.price.isNotBlank() ->
                        expensesRepository.insertExpenses(vaccination.toUiMap222())

                    vaccination.idExpenses != null && vaccination.price.isNotBlank() -> {
                        Log.i("vaccination2", "update: ${vaccination.idExpenses} ")
                        expensesRepository.updateExpenses(vaccination.toUiMap222())
                    }

                    vaccination.idExpenses != null && vaccination.price.isBlank() ->
                        expensesRepository.deleteExpensesById(vaccination.idExpenses)
                }
                updateEndDialog()
                showMessage("Редактировать размер")
            }
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            animalVaccinationRepository.deleteAnimalVaccinationTableById(id)
            updateEndDialog()
            showMessage("Удалить размер")
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorVaccination = state.currentProduct.vaccination.isBlank(),
                        isErrorCount = if (state.isAnimalGroup) state.currentProduct.countVaccination.isBlank() else false,
                        isErrorCountZero = if (state.isAnimalGroup) isAnimalCountZero(state.currentProduct.countVaccination) else false,
                    )
                )
            )
        }
    }

    private fun updateOpenDialog(
        isEntry: Boolean,
        domainAnimal: AnimalVaccinationExpensesDomain?
    ) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isOpenDialog = true,
                    currentProduct = Vaccination(idAnimal = itemId, isEntry = isEntry)
                )
            }
            domainAnimal?.let {
                val expensesVaccination =
                    expensesRepository.getItemExpensesForVaccination(domainAnimal.id).firstOrNull()

                updateState {
                    it.copy(
                        currentProduct = it.currentProduct.toUiMap22(
                            domainAnimal,
                            expensesVaccination?.id
                        )
                    )
                }
            }
        }
    }

    private fun updateEndDialog() {
        updateState {
            it.copy(
                isOpenDialog = false,
                currentProduct = Vaccination()
            )
        }
    }

    private fun updateVaccination(vaccination: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    vaccination = vaccination
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    date = date
                )
            )
        }
    }

    private fun updateDateNext(dateNext: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    nextDate = dateNext
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

    private fun updateCount(count: String) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    countVaccination = count
                )
            )
        }
        updatePriceAll()
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

    private fun updateAutoPrice(isAutoPrice: Boolean) {
        updateState {
            it.copy(
                currentProduct = it.currentProduct.copy(
                    isAutoCalculate = isAutoPrice
                )
            )
        }
        updatePriceAll()
    }

    private fun updatePriceAll() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    priceAll = if (state.currentProduct.isAutoCalculate)
                        (state.currentProduct.price.toConvertZeroDouble() * state.currentProduct.countVaccination.toConvertZeroDouble()).formatNumber()
                    else "0"
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

    private fun Vaccination.toUiMap222(
        animalVaccinationId: Long? = null
    ): DomainExpensesTable {
        val dateList = date.split(".")
        val category = resourceProvider.getString(R.string.vaccination_screen_title)
        return DomainExpensesTable(
            id = idExpenses ?: 0,
            title = vaccination,
            count = countVaccination.toConvertZeroDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            price = price.toConvertZeroDouble(),
            priceAll = if (isAutoCalculate) priceAll.toConvertZeroDouble() else null,
            countSuffix = getState().suffixAnimal,
            category = category,
            note = note,
            isShowFood = false,
            isShowFoodHand = false,
            isShowWarehouse = false,
            isShowAnimals = false,
            idPT = itemIdPT,
            animalVaccinationId = animalVaccinationId ?: id
        )
    }

    private fun Vaccination.toUiMap22(
        domain: AnimalVaccinationExpensesDomain,
        idExpenses: Long?
    ): Vaccination {
        return copy(
            id = domain.id,
            vaccination = domain.vaccination,
            countVaccination = domain.countVaccination.formatNumber(),
            date = domain.date,
            nextDate = domain.nextDate ?: dateTodayNextYear(),
            note = domain.note,
            idAnimal = domain.idAnimal,
            price = domain.price?.formatNumber(false) ?: "",
            priceAll = domain.priceAll?.formatNumber() ?: "",
            isAutoCalculate = domain.priceAll != null,
            isDateFactory = domain.nextDate == null,
            idExpenses = idExpenses
        )
    }

    private fun Vaccination.toDomainMap(): DomainAnimalVaccination {
        return DomainAnimalVaccination(
            id = id,
            vaccination = vaccination,
            countVaccination = countVaccination.toInt(),
            date = date,
            nextVaccination = if (!isDateFactory) nextDate else null,
            idAnimal = idAnimal,
            note = note,
        )
    }
}

sealed class AnimalVaccinationIntent : BaseIntent {
    data class OpenDialogClicked(
        val isEntry: Boolean,
        val domainAnimalVaccination: AnimalVaccinationExpensesDomain? = null
    ) : AnimalVaccinationIntent()

    data object EndDialogClicked : AnimalVaccinationIntent()
    data class VaccinationChanged(val value: String) : AnimalVaccinationIntent()
    data class CountChanged(val value: String) : AnimalVaccinationIntent()
    data class PriceChanged(val value: String) : AnimalVaccinationIntent()
    data class AutoPriceClicked(val value: Boolean) : AnimalVaccinationIntent()
    data class DateClicked(val value: String) : AnimalVaccinationIntent()
    data class DateFactoryClicked(val value: Boolean) : AnimalVaccinationIntent()
    data class DateNextClicked(val value: String) : AnimalVaccinationIntent()
    data class NoteChanged(val value: String) : AnimalVaccinationIntent()
    data object InsertPressed : AnimalVaccinationIntent()
    data object UpdatePressed : AnimalVaccinationIntent()
    data class DeletePressed(val value: Long) : AnimalVaccinationIntent()
}
