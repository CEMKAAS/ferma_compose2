package com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.repository.AnimalCountRepository
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalRepository: AnimalRepository,
    private val animalCountRepository: AnimalCountRepository,
    private val expensesRepository: ExpensesRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository
) : EntryNewViewModel2<AnimalListState, AnimalListIntent, AnimalListReduce>(
    AnimalListState(),
    AnimalListReduce(resourceProvider)
) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalDestination.itemIdArg])

    init {
        loadDate()
    }

    override fun onIntent(intent: AnimalListIntent) {
        sendIntent(intent)
        return when (intent) {
            is AnimalListIntent.OpenBottomSheetEntry ->
                loadDataForEntryOrEdit(intent.value, intent.isSaveStateForBottomSheet)

            AnimalListIntent.Insert -> insert()
            is AnimalListIntent.Archive -> archive(intent.id, intent.isArchive)
            is AnimalListIntent.Delete -> delete(intent.value)

            else -> Unit
        }
    }

    private fun archive(id: Long, isArchive: Boolean) {
        viewModelScope.launch {
            animalRepository.updateArchiveAnimalTableById(isArchive, id)
        }
    }

    private fun loadDate() {
        viewModelScope.launch {
            val isArchiveProject = projectRepository.getIsArchiveProject(itemIdPT).first()
            animalRepository.getAllAnimal(itemIdPT).collectLatest { list ->
                val baseList = list.map { animal -> animal.toUi() }
                val archiveAnimalList = baseList.filter { it.isArchive }
                val animalList = baseList.filter { !it.isArchive }
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        list = animalList,
                        searchList = animalList,
                        archiveList = archiveAnimalList,
                        searchArchiveList = archiveAnimalList,
                        isLoading = false,
                        isArchiveProject = isArchiveProject
                    )
                }
            }
        }
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else AnimalEntryState2()
                onIntent(
                    AnimalListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForEntry) {
                val typeList = animalRepository.getTypeAnimal(itemIdPT).first()
                AnimalEntryState2(
                    itemIdPT = itemIdPT,
                    typeList = typeList
                )
            } else getState().currentProduct
            onIntent(
                AnimalListIntent.RefreshEntryBottomSheetState(true, newState)
            )
        }
    }


    override fun insert() {
        viewModelScope.launch {
            val idAnimal =
                animalRepository.insertAnimalTable(
                    getState().currentProduct.saveAnimal()
                )

            val pair = getState().currentProduct.updateForSave(idAnimal, itemIdPT)

            animalCountRepository.insertAnimalCountTable(pair.first)

            pair.second?.let {
                expensesRepository.insertExpenses(it)
            }
            loadDataForEntryOrEdit(false)
            showMessage(
                resourceProvider.getString(R.string.toast_sale_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    )
            )
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            animalRepository.deleteAnimalTable(id)
            loadDataForEntryOrEdit(false)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        getState().currentProduct.title,
                        getState().currentProduct.count,
                        getState().currentProduct.countSuffix
                    )
            )
        }
    }

    private fun AnimalEntryState2.saveAnimal(): DomainAnimalTable {
        return DomainAnimalTable(
            id = itemId,
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
        val countAnimal = count.ifBlank { "1" }

        return DomainAnimalCount(
            count = countAnimal,
            suffix = countSuffix,
            date = if (!isDateFactory) dateFactory else dateBorn,
            idAnimal = idAnimal,
            note = "",
            version = if (price.isNotBlank()) AnimalCountVersion.EXPENSES else AnimalCountVersion.ADD
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
                        category = resourceProvider.getString(R.string.animal_card_screen_add_category_expenses),
                        note = "",
                        isShowFood = false,
                        idPT = itemIdPT,
                        animalId = idAnimal,
                        animalVaccinationId = null,
                        animalCountId = null,
                        isFood = false,
                    ) else null
    }

    private suspend fun DomainAnimalWithCount.toUi(): AnimalListUi {
        val animalCount = animalCountRepository.getCountAnimalLimit(id).first()
        return AnimalListUi(
            id = id,
            name = name,
            type = type,
            date = getAgeFromDate(dateFactory ?: date),
            isGroup = group,
            sex = if (group) null else sex,
            count = animalCount?.count?.let { if (it.toInt() > 1) it else null },
            suffix = animalCount?.suffix ?: Suffix.KILOGRAM_DAY,
            foodDay = foodDay,
            foodDaySuffix = foodDaySuffix,
            isArchive = isArchive,
            imagePath = imagePath,
            currentIcon = currentIcon ?: R.drawable.baseline_pets_24
        )
    }

    fun getAgeFromDate(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val birthDate = LocalDate.parse(dateString, formatter)
        val currentDate = LocalDate.now()

        if (birthDate.isAfter(currentDate)) {
            return "Дата в будущем"
        }

        val period = Period.between(birthDate, currentDate)
        val years = period.years
        val months = period.months

        val yeatsString = resourceProvider.getString(R.string.date_years)
        val monthsString = resourceProvider.getString(R.string.date_months)

        return when {
            years >= 1 -> "$years $yeatsString $months $monthsString"
            else -> {
                val totalMonths = ChronoUnit.MONTHS.between(birthDate, currentDate)
                "$totalMonths $monthsString"
            }
        }
    }

    override fun update() {}
}