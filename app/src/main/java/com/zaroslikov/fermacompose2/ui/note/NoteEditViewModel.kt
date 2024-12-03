package com.zaroslikov.fermacompose2.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.NoteTable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NoteEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[NoteEditDestination.itemIdArg])

    var itemUiState by mutableStateOf(NoteTableUiState())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getNote(itemId)
                .filterNotNull()
                .first()
                .toNoteTableUiState()
        }
    }

    fun updateUiState(itemDetails: NoteTableUiState) {
        itemUiState =
            itemDetails
    }
    suspend fun saveItem() {
        itemsRepository.updateNote(itemUiState.toNoteTable())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteNote(itemUiState.toNoteTable())
    }

}

data class NoteTableUiState(
    val id: Long = 0,
    val title: String = "",
    val note:String = "",
    val date: String = "",
    val idPT: Long = 0
)

fun NoteTable.toNoteTableUiState(): NoteTableUiState = NoteTableUiState(
    id, title, note, date, idPT
)

fun NoteTableUiState.toNoteTable(): NoteTable = NoteTable(
    id, title,note, date, idPT
)