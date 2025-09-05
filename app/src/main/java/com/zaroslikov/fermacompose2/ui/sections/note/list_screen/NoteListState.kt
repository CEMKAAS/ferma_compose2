package com.zaroslikov.fermacompose2.ui.sections.note.list_screen

import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class NoteListState(
    override val idPT: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val list: List<DomainNoteTable> = emptyList()
) : ListState()
