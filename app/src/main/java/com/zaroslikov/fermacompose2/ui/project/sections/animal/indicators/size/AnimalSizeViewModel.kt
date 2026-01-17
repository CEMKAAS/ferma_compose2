package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.supportFun.convertSize
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.ui.formatNumber
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class AnimalSizeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val animalSizeRepository: AnimalSizeRepository,
    private val resourceProvider: ResourceProvider
) : EntryViewModel<AnimalSizeState, AnimalSizeIntent>(AnimalSizeState()) {
    val itemId: Long = checkNotNull(savedStateHandle[AnimalSizeDestination.itemId])
    val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalSizeDestination.itemIdPT])

    init {
        viewModelScope.launch {
            loadData()
        }
    }

    override fun onIntent(intent: AnimalSizeIntent) {
        return when (intent) {
            is AnimalSizeIntent.OpenDialogClicked -> updateOpenDialog(
                intent.isEntry,
                intent.domainAnimalSize
            )

            AnimalSizeIntent.EndDialogClicked -> updateEndDialog()
            is AnimalSizeIntent.SizeChanged -> updateSize(intent.value)
            is AnimalSizeIntent.DateClicked -> updateDate(intent.value)
            is AnimalSizeIntent.NoteChanged -> updateNote(intent.value)
            is AnimalSizeIntent.SuffixClicked -> updateSuffix(intent.value)
            AnimalSizeIntent.InsertPressed -> insert()
            AnimalSizeIntent.UpdatePressed -> update()
            AnimalSizeIntent.DeletePressed -> delete()
        }
    }

    suspend fun loadData() {
        updateState { it.copy(isLoading = true) }
        animalSizeRepository.getSizeAnimal(itemId).collectLatest { sizeList ->
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    sizeList = sizeList,
                    isLoading = false
                )
            }
        }
    }

    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                Log.i("Size", "insert: ${getState().domainAnimalSize.copy(idAnimal = itemId)}")
                animalSizeRepository.insertAnimalSizeTable(getState().domainAnimalSize.copy(idAnimal = itemId))
                updateEndDialog()
                showMessage("Добавлен размер")
            }
        }
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError()) {
                animalSizeRepository.updateAnimalSizeTable(getState().domainAnimalSize)
                updateEndDialog()
                showMessage("Редактировать размер")
            }
        }
    }

    override fun delete() {
        viewModelScope.launch {
            animalSizeRepository.deleteAnimalSizeTable(getState().domainAnimalSize)
            updateEndDialog()
            showMessage("Удалить размер")
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                error = state.error.copy(
                    isErrorSize = state.domainAnimalSize.size.isBlank()
                )
            )
        }
    }

    private fun updateOpenDialog(
        isEntry: Boolean,
        domainAnimalSize: DomainAnimalSize
    ) {
        updateState {
            it.copy(
                isOpenDialog = true,
                isEntry = isEntry,
                domainAnimalSize = domainAnimalSize
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
                domainAnimalSize = it.domainAnimalSize.copy(
                    suffix = suffix
                )
            )
        }
    }

    private fun updateSize(size: String) {
        updateState {
            it.copy(
                domainAnimalSize = it.domainAnimalSize.copy(
                    size = size
                )
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                domainAnimalSize = it.domainAnimalSize.copy(
                    date = date
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                domainAnimalSize = it.domainAnimalSize.copy(
                    note = note
                )
            )
        }
    }

    fun positeive(
        domainAnimalSize: DomainAnimalSize,
        previousDomainAnimalSize: DomainAnimalSize?
    ): Pair<String, IndicationStatus> {
        if (previousDomainAnimalSize == null) return domainAnimalSize.size to IndicationStatus.POSITIVE
        else {
            val size = domainAnimalSize.size.toConvertZeroDouble()
            val previousSize = previousDomainAnimalSize.size.toConvertZeroDouble()

            val suffix = domainAnimalSize.suffix
            val suffixPrevious = previousDomainAnimalSize.suffix

            val sizeConverted = size.convertSize(suffix, to = Suffix.MILLIMETERS)
            val previousCountConverted =
                previousSize.convertSize(suffixPrevious, to = Suffix.MILLIMETERS)

            val totalValue = (sizeConverted - previousCountConverted).convertSize(
                Suffix.MILLIMETERS,
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

sealed class AnimalSizeIntent : BaseIntent {
    data class OpenDialogClicked(
        val isEntry: Boolean,
        val domainAnimalSize: DomainAnimalSize = DomainAnimalSize(date = dateToday())
    ) : AnimalSizeIntent()

    data object EndDialogClicked : AnimalSizeIntent()
    data class SizeChanged(val value: String) : AnimalSizeIntent()
    data class SuffixClicked(val value: Suffix) : AnimalSizeIntent()
    data class DateClicked(val value: String) : AnimalSizeIntent()
    data class NoteChanged(val value: String) : AnimalSizeIntent()
    data object InsertPressed : AnimalSizeIntent()
    data object UpdatePressed : AnimalSizeIntent()
    data object DeletePressed : AnimalSizeIntent()
}