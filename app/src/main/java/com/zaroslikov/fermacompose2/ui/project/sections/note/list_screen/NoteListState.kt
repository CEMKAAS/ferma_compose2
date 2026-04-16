package com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen

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
    val searchList: List<DomainNoteTable> = emptyList(),
    val isOpenBottomSheetEntry: Boolean = false,
    val isOpenBottomSheetDetail: Boolean = false,
    val isOpenBottomSheetDelete: Boolean = false,
    val detailDomainNoteTable: DomainNoteTable = DomainNoteTable(),
    val index: Long = 0,
    val isSaveStateForBottomSheet: Boolean = false,
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val currentProduct: NoteEntryState2 = NoteEntryState2(),
    val isArchive: Boolean = false
) : EntryNewState()

data class NoteEntryState2(
    val itemId: Long = 0,
    val title: String = "",
    val note: String = "",
    val date: String = dateToday(),
    val itemIdPT: Long = 0,
    val error: ErrorNote = ErrorNote(),
    val isEntry: Boolean = true,
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class ErrorNote(
    val isErrorTitle: Boolean = false,
) : BaseError