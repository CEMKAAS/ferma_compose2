package com.zaroslikov.fermacompose2.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.NoteTable


class NoteEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Long = checkNotNull(savedStateHandle[NoteEntryDestination.itemIdArg])

    suspend fun saveItem(noteTable: NoteTable) {
        itemsRepository.insertNote(noteTable)
    }
}
