package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.vaccination

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.dto.animal.AnimalVaccinationExpensesDomain
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalVaccinationRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.dateTodayNextYear
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    private val resourceProvider: ResourceProvider,
    private val settingsRepository: SettingsRepository,
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
) : EntryNewViewModel2<AnimalVaccinationState, AnimalVaccinationIntent, AnimalVaccinationReduce>(
    AnimalVaccinationState(),
    AnimalVaccinationReduce()
) {
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalVaccinationDestination.itemId])
    private val itemIdPT: Long =
        checkNotNull(savedStateHandle[AnimalVaccinationDestination.itemIdPT])

    init {
        loadData()
    }

    override fun onIntent(intent: AnimalVaccinationIntent) {
        sendIntent(intent)
        return when (intent) {
            is AnimalVaccinationIntent.OpenEntryBottomSheetByItem -> loadDataForEntryOrEdit(
                intent.isOpen,
                intent.domainAnimalVaccination,
                intent.isSaveStateForBottomSheet
            )

            AnimalVaccinationIntent.InsertPressed -> insert()
            AnimalVaccinationIntent.UpdatePressed -> update()
            is AnimalVaccinationIntent.DeletePressed -> delete(0)
            else -> Unit
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val isArchiveProject = projectRepository.getIsArchiveProject(itemIdPT).first()
            val isArchiveAnimal = animalRepository.getAnimal(itemId).first().archive
            combine(
                animalVaccinationRepository.getTitleVaccinationAnimalList(itemId),
                animalVaccinationRepository.getVaccinationExpensesAnimal(itemId),
                settingsRepository.getSettings(itemIdPT)
            ) { titleVaccinationList, vaccination, settings ->
                Triple(titleVaccinationList, vaccination, settings)
            }.collectLatest { data ->
                updateState {
                    it.copy(
                        settings = data.third,
                        vaccinationList = data.second,
                        titleVaccinationList = data.first,
                        idPT = itemIdPT,
                        isLoading = false,
                        isArchive = isArchiveProject || isArchiveAnimal
                    )
                }
            }
        }
    }

    override fun insert() {
        viewModelScope.launch {
            val vaccination = getState().currentProduct
            val vaccinationId =
                animalVaccinationRepository.insertAnimalVaccinationTable(vaccination.toDomainMap())
            if (vaccination.price.isNotBlank())
                expensesRepository.insertExpenses(vaccination.toDomainExpensesTable(vaccinationId))
            yandexMetricRepository.metricalAnimalVaccination(getState().currentProduct)
            showSnackbar(ProductOperation.ADD)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            val vaccination = getState().currentProduct
            animalVaccinationRepository.updateAnimalVaccinationTable(vaccination.toDomainMap())
            when {
                vaccination.idExpenses == null && vaccination.price.isNotBlank() ->
                    expensesRepository.insertExpenses(vaccination.toDomainExpensesTable())

                vaccination.idExpenses != null && vaccination.price.isNotBlank() ->
                    expensesRepository.updateExpenses(vaccination.toDomainExpensesTable())

                vaccination.idExpenses != null && vaccination.price.isBlank() ->
                    expensesRepository.deleteExpensesById(vaccination.idExpenses)
            }
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().deleteVaccination?.let {
                animalVaccinationRepository.deleteAnimalVaccinationTableById(it.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(AnimalVaccinationIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    private fun showSnackbar(productOperation: ProductOperation) {
        val count =
            if (productOperation == ProductOperation.DELETE)
                getState().deleteVaccination?.vaccination ?: ""
            else getState().currentProduct.vaccination

        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_vaccine_add)
                    .format(count)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_vaccine_update)
                    .format(count)

                else -> resourceProvider.getString(R.string.snackbar_vaccine_delete).format(count)
            }
        )
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: AnimalVaccinationExpensesDomain?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else Vaccination()
                onIntent(
                    AnimalVaccinationIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }

            val newState = if (!getState().isSaveStateForBottomSheet || domain != null) {
                val animalDeferred = async { animalRepository.getAnimal(itemId).first().group }
                val animalCountDeferred =
                    async { animalCountAnimalRepository.getCountAnimalLimit(itemId).first() }
                val settingsDeferred = async { settingsRepository.getSettings(itemIdPT).first() }

                val baseState = Vaccination(
                    isAnimalGroup = animalDeferred.await(),
                    countAnimalAll = animalCountDeferred.await()?.count,
                    animalSuffix = animalCountDeferred.await()?.suffix ?: Suffix.PIECES,
                    priceSuffix = settingsDeferred.await().currencySuffix,
                    idAnimal = itemId
                )
                domain?.let {
                    val expensesVaccination =
                        expensesRepository.getItemExpensesForVaccination(domain.id).firstOrNull()
                    baseState.toUi(domain, expensesVaccination?.id)
                } ?: baseState
            } else getState().currentProduct
            sendIntent(
                AnimalVaccinationIntent.RefreshEntryBottomSheetState(true, newState)
            )
        }
    }

    private fun Vaccination.toDomainExpensesTable(
        animalVaccinationId: Long? = null
    ): DomainExpensesTable {
        val dateList = date.split(".")
        val category = resourceProvider.getString(R.string.vaccination_screen_title)
        return DomainExpensesTable(
            id = idExpenses ?: 0,
            title = vaccination.trim(),
            count = countVaccination.toConvertZeroDouble(),
            day = dateList[0].toInt(),
            month = dateList[1].toInt(),
            year = dateList[2].toInt(),
            price = price.toConvertZeroDouble(),
            priceAll = if (isAutoCalculate) priceAll.toConvertZeroDouble() else null,
            priceSuffix = getState().settings.currencySuffix,
            countSuffix = animalSuffix,
            category = category,
            note = note.trim(),
            isShowFood = false,
            idPT = itemIdPT,
            animalVaccinationId = animalVaccinationId ?: id,
            isFood = false
        )
    }

    private fun Vaccination.toUi(
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
            isEntry = false,
            idExpenses = idExpenses
        )
    }

    private fun Vaccination.toDomainMap(): DomainAnimalVaccination {
        return DomainAnimalVaccination(
            id = id,
            vaccination = vaccination.trim(),
            countVaccination = countVaccination.toInt(),
            date = date,
            nextVaccination = if (!isDateFactory) nextDate else null,
            idAnimal = idAnimal,
            note = note.trim()
        )
    }
}