package com.zaroslikov.fermacompose2.ui.animal.indicators.size

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.AnimalSizeRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        TODO("Not yet implemented")
    }

    override fun update() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }

    override fun validation() {
        TODO("Not yet implemented")
    }

    override fun onIntent(intent: AnimalSizeIntent) {
        return when (intent) {
            is AnimalSizeIntent.AddOpenDialogClicked -> updateAddOpenDialog(intent.value)
            is AnimalSizeIntent.EditOpenDialogClicked -> updateEditOpenDialog(intent.value)
            is AnimalSizeIntent.SizeChanged -> updateSize(intent.value)
            is AnimalSizeIntent.DateClicked -> updateDate(intent.value)
            is AnimalSizeIntent.NoteChanged -> updateNote(intent.value)
            is AnimalSizeIntent.SuffixClicked -> updateSuffix(intent.value)
        }
    }

    private fun updateAddOpenDialog(openDialog: Boolean) {
        updateState {
            it.copy(
                isAddOpenDialog = openDialog
            )
        }
    }

    private fun updateEditOpenDialog(openDialog: Boolean) {
        updateState {
            it.copy(
                isEditOpenDialog = openDialog
            )
        }
    }

    private fun updateSuffix(suffix: String) {
        updateState {
            it.copy(
                suffix = suffix
            )
        }
    }

    private fun updateSize(size: String) {
        updateState {
            it.copy(
                size = size
            )
        }
    }

    private fun updateDate(date: String) {
        updateState {
            it.copy(
                date = date
            )
        }
    }

    private fun updateNote(note: String) {
        updateState {
            it.copy(
                note = note
            )
        }
    }

}

sealed class AnimalSizeIntent : BaseIntent {
    data class AddOpenDialogClicked(val value: Boolean) : AnimalSizeIntent()
    data class EditOpenDialogClicked(val value: Boolean) : AnimalSizeIntent()
    data class SizeChanged(val value: String) : AnimalSizeIntent()
    data class SuffixClicked(val value: String) : AnimalSizeIntent()
    data class DateClicked(val value: String) : AnimalSizeIntent()
    data class NoteChanged(val value: String) : AnimalSizeIntent()
}