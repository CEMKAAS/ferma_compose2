package com.zaroslikov.fermacompose2.ui.note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.repository.ItemsRepository
import kotlinx.coroutines.launch

class NoteEditViewModel(
    savedStateHandle: SavedStateHandle, private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[NoteEditDestination.itemIdArg])

    var itemUiState by mutableStateOf(DomainNoteTable())
        private set

    init {
        viewModelScope.launch {
//            itemUiState = itemsRepository.getNote(itemId).filterNotNull().first().toDomainMap()
        }
    }

    fun updateUiState(itemDetails: DomainNoteTable) {
        itemUiState = itemDetails
    }

    suspend fun saveItem() {
//        itemsRepository.updateNote(itemUiState.toRoomMap())
    }

    suspend fun deleteItem() {
//        itemsRepository.deleteNote(itemUiState.toRoomMap())
    }

}

