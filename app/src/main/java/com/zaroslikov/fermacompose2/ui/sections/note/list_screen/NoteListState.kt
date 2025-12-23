package com.zaroslikov.fermacompose2.ui.sections.note.list_screen

import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class NoteListState(
    val textSearch: String = "",
    val idPT: Long = 0,
    val list: List<DomainNoteTable> = emptyList(),
    val openBottomSheetEntry: Boolean = false,
    val openBottomSheetDetail: Boolean = false,
    val detailDomainNoteTable: DomainNoteTable = DomainNoteTable(),
    val index: Long = 0,
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val currentProduct: NoteEntryState2 = NoteEntryState2()
) : EntryNewState()

data class NoteEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val note: String = "",
    val date: String = dateToday(),
    val itemIdPT: Long = 0,
    val error: ErrorNote = ErrorNote(),
    val isEntry: Boolean = true,
) : BaseProduct() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled =
            title.isNotBlank()
        return !isEnabled
    }
}

data class ErrorNote(
    val isErrorTitle: Boolean = false,
) : BaseError {
    val hasAnyError: Boolean
        get() = isErrorTitle
}