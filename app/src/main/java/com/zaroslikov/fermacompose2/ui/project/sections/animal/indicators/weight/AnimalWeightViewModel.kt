package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.domain.repository.AnimalWeightRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.convertWeight
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber2
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class AnimalWeightViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalWeightRepository: AnimalWeightRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<AnimalWeightState, AnimalWeightIntent>(AnimalWeightState()) {
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalWeightDestination.itemId])
    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalWeightDestination.itemIdPT])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    override fun onIntent(intent: AnimalWeightIntent) {
        return when (intent) {
            is AnimalWeightIntent.OpenDialogClicked -> updateOpenDialog(
                intent.isEntry,
                intent.domainAnimalWeight
            )

            AnimalWeightIntent.EndDialogClicked -> updateEndDialog()
            is AnimalWeightIntent.WeightChanged -> updateWeight(intent.value)
            is AnimalWeightIntent.DateClicked -> updateDate(intent.value)
            is AnimalWeightIntent.NoteChanged -> updateNote(intent.value)
            is AnimalWeightIntent.SuffixClicked -> updateSuffix(intent.value)
            AnimalWeightIntent.InsertPressed -> insert()
            AnimalWeightIntent.UpdatePressed -> update()
            AnimalWeightIntent.DeletePressed -> delete()
        }
    }

    suspend fun loadData() {
        updateState { it.copy(isLoading = true) }
        animalWeightRepository.getWeightAnimal(itemId).collectLatest { weightList ->
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    weightList = weightList,
                    isLoading = false
                )
            }
        }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                Log.i("Size", "insert: ${getState().domainAnimalWeight.copy(idAnimal = itemId)}")
                animalWeightRepository.insertAnimalWeightTable(
                    getState().domainAnimalWeight.copy(
                        idAnimal = itemId
                    )
                )
                updateEndDialog()
                showMessage("Добавлен размер")
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                animalWeightRepository.updateAnimalWeightTable(getState().domainAnimalWeight)
                updateEndDialog()
                showMessage("Редактировать размер")
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            animalWeightRepository.deleteAnimalWeightTable(getState().domainAnimalWeight)
            updateEndDialog()
            showMessage("Удалить размер")
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorWeight = state.domainAnimalWeight.weight.isBlank()
                )
            )
        }
    }

    private fun updateOpenDialog(
        isEntry: Boolean,
        domainAnimalWeight: DomainAnimalWeight
    ) {
        updateState {
            it.copy(
                isOpenDialog = true,
                isEntry = isEntry,
                domainAnimalWeight = domainAnimalWeight
            )
        }
    }

    private fun updateEndDialog() {
        updateState {
            it.copy(
                isOpenDialog = false
            )
        }
    }

    private fun updateSuffix(suffix: Suffix) {
        updateState {
            it.copy(
                domainAnimalWeight = it.domainAnimalWeight.copy(
                    suffix = suffix
                )
            )
        }
    }

    private fun updateWeight(size: String) {
        updateState {
            it.copy(
                domainAnimalWeight = it.domainAnimalWeight.copy(
                    weight = size
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                domainAnimalWeight = it.domainAnimalWeight.copy(
                    date = date
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                domainAnimalWeight = it.domainAnimalWeight.copy(
                    note = note
                )
            )
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
}

sealed class AnimalWeightIntent : BaseIntent {
    data class OpenDialogClicked(
        val isEntry: Boolean,
        val domainAnimalWeight: DomainAnimalWeight = DomainAnimalWeight(date = dateToday())
    ) : AnimalWeightIntent()

    data object EndDialogClicked : AnimalWeightIntent()
    data class WeightChanged(val value: String) : AnimalWeightIntent()
    data class SuffixClicked(val value: Suffix) : AnimalWeightIntent()
    data class DateClicked(val value: String) : AnimalWeightIntent()
    data class NoteChanged(val value: String) : AnimalWeightIntent()
    data object InsertPressed : AnimalWeightIntent()
    data object UpdatePressed : AnimalWeightIntent()
    data object DeletePressed : AnimalWeightIntent()
}