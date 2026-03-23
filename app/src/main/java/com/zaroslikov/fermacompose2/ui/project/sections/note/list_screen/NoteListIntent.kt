package com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen

import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class NoteListIntent() : BaseIntent {
    data class LoadingChanged(val value: Boolean) : NoteListIntent()
    data class LoadData(val value: List<DomainNoteTable>) : NoteListIntent()
    data class OpenBottomSheetDetail(
        val value: Long? = null
    ) : NoteListIntent()

    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainNoteTable? = null,
        val isSaveStateForBottomSheet: Boolean = false
    ) : NoteListIntent()

    data class RefreshEntryBottomSheetState(
        val isOpen: Boolean,
        val state: NoteEntryState2,
        val isSaveStateForBottomSheet: Boolean = false
    ) : NoteListIntent()

    data class TitleChanged(val value: String) : NoteListIntent()
    data class NoteChanged(val value: String) : NoteListIntent()
    data class SearchChanged(val value: String) : NoteListIntent()

    data object Insert : NoteListIntent()
    data object Update : NoteListIntent()
    data class Delete(val value: Long) : NoteListIntent()
}