package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.repository.AnimalRepository
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber2
import com.zaroslikov.fermacompose2.ui.formatNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class AnimalWeightViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalWeightRepository: AnimalWeightRepository,
    private val projectRepository: ProjectRepository,
    private val animalRepository: AnimalRepository,
) : EntryNewViewModel2<AnimalWeightState, AnimalWeightIntent, AnimalWeightReduce>(
    AnimalWeightState(),
    AnimalWeightReduce()
) {
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalWeightDestination.itemId])
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalWeightDestination.itemIdPT])

    init {
        loadData()
    }

    override fun onIntent(intent: AnimalWeightIntent) {
        sendIntent(intent)
        return when (intent) {
            is AnimalWeightIntent.OpenDialogClicked -> loadDataForEntryOrEdit(
                intent.isEntry,
                intent.animalWeightUi,
                intent.isSaveStateForBottomSheet
            )

            AnimalWeightIntent.InsertPressed -> insert()
            AnimalWeightIntent.UpdatePressed -> update()
            is AnimalWeightIntent.DeletePressed -> delete(intent.value)
            else -> Unit
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val isArchiveProject = projectRepository.getIsArchiveProject(itemIdPT).first()
            val isArchiveAnimal = animalRepository.getAnimal(itemId).first().archive
            animalWeightRepository.getWeightAnimal(itemId).collectLatest { weightList ->
                updateState {
                    it.copy(
                        idPT = itemIdPT,
                        weightList = settingsList(weightList),
                        isLoading = false,
                        isArchive = isArchiveProject || isArchiveAnimal
                    )
                }
            }
        }
    }

    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: AnimalWeightUi?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else CurrentAnimalWeight()
                onIntent(
                    AnimalWeightIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }
            val newState = if (!getState().isSaveStateForEntry || domain != null) {
                val baseState = CurrentAnimalWeight(
                    idAnimal = itemId
                )
                domain?.toCurrentAnimalWeight() ?: baseState
            } else getState().currentProduct
            sendIntent(
                AnimalWeightIntent.RefreshEntryBottomSheetState(
                    true, newState
                )
            )
        }
    }


    override fun insert() {
        viewModelScope.launch {
            animalWeightRepository.insertAnimalWeightTable(
                getState().currentProduct.toDomain()
            )
            loadDataForEntryOrEdit(false, null)
            showMessage("Добавлен размер")
        }
    }


    override fun update() {
        viewModelScope.launch {
            animalWeightRepository.updateAnimalWeightTable(
                getState().currentProduct.toDomain()
            )
            loadDataForEntryOrEdit(false, null)
            showMessage("Редактировать размер")
        }
    }


    override fun delete(id: Long) {
        viewModelScope.launch {
            animalWeightRepository.deleteAnimalWeightTableById(id)
            loadDataForEntryOrEdit(false, null)
            showMessage("Удалить размер")
        }
    }

    fun positeive(
        domainAnimalWeight: DomainAnimalWeight,
        previousDomainAnimalWeight: DomainAnimalWeight?
    ): Pair<String, IndicationStatus> {
        if (previousDomainAnimalWeight == null) return domainAnimalWeight.weight.toFormatNumber2() to IndicationStatus.POSITIVE
        else {
            val size = domainAnimalWeight.weight.toConvertZeroDouble()
            val previousSize = previousDomainAnimalWeight.weight.toConvertZeroDouble()

            val suffix = domainAnimalWeight.suffix
            val suffixPrevious = previousDomainAnimalWeight.suffix

            val sizeConverted = size.convertWeight(suffix, to = Suffix.GRAM)
            val previousCountConverted =
                previousSize.convertWeight(suffixPrevious, to = Suffix.GRAM)

            val totalValue = (sizeConverted - previousCountConverted).convertWeight(
                Suffix.GRAM,
                suffix
            ).absoluteValue.formatNumber()
            val status = when {
                sizeConverted > previousCountConverted -> IndicationStatus.POSITIVE
                sizeConverted == previousCountConverted -> IndicationStatus.NEUTRAL
                else -> IndicationStatus.NEGATIVE
            }
            return totalValue to status
        }
    }

    private fun settingsList(list: List<DomainAnimalWeight>): List<AnimalWeightUi> {
        return list.mapIndexed { index, domain ->
            val previousDomain =
                if (index < list.size - 1) list[index + 1] else null

            val (totalValue, indicationStatus) = if (previousDomain == null)
                domain.weight.toFormatNumber2() to IndicationStatus.POSITIVE
            else {
                val size = domain.weight.toConvertZeroDouble()
                val previousSize = previousDomain.weight.toConvertZeroDouble()

                val suffix = domain.suffix
                val suffixPrevious = previousDomain.suffix

                val sizeConverted = size.convertWeight(suffix, to = Suffix.GRAM)
                val previousCountConverted =
                    previousSize.convertWeight(suffixPrevious, to = Suffix.GRAM)

                val totalValue = (sizeConverted - previousCountConverted).convertWeight(
                    Suffix.GRAM,
                    suffix
                ).absoluteValue.formatNumber()
                val status = when {
                    sizeConverted > previousCountConverted -> IndicationStatus.POSITIVE
                    sizeConverted == previousCountConverted -> IndicationStatus.NEUTRAL
                    else -> IndicationStatus.NEGATIVE
                }
                totalValue to status
            }
            domain.toAnimalWeightUi(
                totalValue = totalValue,
                indicationStatus = indicationStatus
            )
        }
    }

    private fun AnimalWeightUi.toCurrentAnimalWeight(): CurrentAnimalWeight {
        return CurrentAnimalWeight(
            id = id,
            weight = weight,
            suffix = suffix,
            date = date,
            idAnimal = idAnimal,
            note = note,
            isEntry = false
        )
    }

    private fun CurrentAnimalWeight.toDomain(): DomainAnimalWeight {
        return DomainAnimalWeight(
            id = id,
            weight = weight,
            suffix = suffix,
            date = date,
            idAnimal = idAnimal,
            note = note
        )
    }

    private fun DomainAnimalWeight.toAnimalWeightUi(
        totalValue: String,
        indicationStatus: IndicationStatus
    ): AnimalWeightUi {
        return AnimalWeightUi(
            id = id,
            weight = weight,
            suffix = suffix,
            date = date,
            idAnimal = idAnimal,
            note = note,
            totalValue = totalValue,
            indicationStatus = indicationStatus
        )
    }
}