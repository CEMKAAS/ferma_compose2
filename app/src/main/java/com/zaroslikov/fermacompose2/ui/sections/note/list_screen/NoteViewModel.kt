package com.zaroslikov.fermacompose2.ui.sections.note.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.NoteRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : ListViewModel<NoteListState, NoteListIntent>(NoteListState()) {
    private val itemIdPT: Long = checkNotNull(savedStateHandle[NoteDestination.itemIdArg])

    init {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val list = noteRepository.getAllNote(itemIdPT).first()
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    list = list,
                    isLoading = false
                )
            }
        }
    }
}

sealed class NoteListIntent() : BaseIntent
