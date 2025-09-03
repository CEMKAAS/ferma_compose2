package com.zaroslikov.fermacompose2.ui.sections.note.entry

import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class NoteEntryState(
    val domainNoteTable: DomainNoteTable = DomainNoteTable(
        id = 0,
        title = "",
        note = "",
        date = dateToday(),
        idPT = 0
    ),
    override val error: Error = Error(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false
) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    data class Error(
        val isErrorTitle: Boolean = false
    ) : BaseError {
        val hasAnyError: Boolean
            get() = isErrorTitle
    }
}
