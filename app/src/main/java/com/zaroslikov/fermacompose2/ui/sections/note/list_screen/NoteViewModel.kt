package com.zaroslikov.fermacompose2.ui.sections.note.list_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.repository.NoteRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository
) : EntryNewViewModel<NoteListState, NoteListIntent>(NoteListState()) {

    private val itemIdPT2: Long = checkNotNull(savedStateHandle[NoteDestination.itemIdArg])

    init {
        Log.i("note", "itit: $itemIdPT2")
        loadDate()
    }

    private fun loadDate() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            noteRepository.getAllNote(itemIdPT2).collectLatest { list ->
                Log.i("note", "loadDate: $list")
                updateState {
                    it.copy(
                        idPT = itemIdPT2,
                        list = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateTitle(title: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    title = title,
                    error = state.currentProduct.error.copy(
                        isErrorTitle = title.isBlank()
                    )
                )
            )
        }
    }

    private fun updateNote(note: String) {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    note = note
                )
            )
        }
    }

    private fun updateSearch(search: String) {
        updateState { it.copy(textSearch = search) }
    }

    private fun openBottomSheetEntry(
        openBottomSheetEntry: Boolean,
        domainSaleTable: DomainNoteTable? = null
    ) {
        if (openBottomSheetEntry) {
            updateState {
                it.copy(
                    openBottomSheetEntry = true,
                    currentProduct = NoteEntryState2()
                )
            }
            domainSaleTable?.let { domain ->
                updateState { it.copy(currentProduct = domain.toNoteStateList2()) }
            }

        } else updateState { it.copy(openBottomSheetEntry = false) }
    }

    private fun openBottomSheetDetail(
        openBottomSheetEntry: Boolean,
        domainSaleTable: Int? = null
    ) {
        if (openBottomSheetEntry) {
            updateState {
                it.copy(
                    openBottomSheetDetail = true,
                    currentProduct = NoteEntryState2()
                )
            }
            domainSaleTable?.let { domain ->
                updateState { it.copy(detailDomainNoteTable = getState().list[domain]) }
            }
        } else updateState { it.copy(openBottomSheetDetail = false) }
    }


    override fun insert() {
        viewModelScope.launch {
            if (!isError()) {
                noteRepository.insertNote(getState().currentProduct.toNoteStateList2())
                openBottomSheetEntry(false)
                /*navigateTo(UiEvent.NavigateBack)*/
                /* showMessage(
                     resourceProvider.getString(R.string.toast_delete_s)
                         .format(
                             getState().domainNoteTable.title,
                         )
                 )*/
            }
        }
//            metricaNote(title)
    }

    override fun update() {
        viewModelScope.launch {
            if (!isError())
                noteRepository.updateNote(getState().currentProduct.toNoteStateList2())
            updateState { it.copy(detailDomainNoteTable = getState().currentProduct.toNoteStateList2()) }
            openBottomSheetEntry(false)
            /* navigateTo(UiEvent.NavigateBack)*/
            /* showMessage(
                 resourceProvider.getString(R.string.toast_delete_s)
                     .format(
                         getState().domainNoteTable.title,
                     )
             )*/
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNoteById(id)
            navigateTo(UiEvent.NavigateBack)

            /*  showMessage(
                  resourceProvider.getString(R.string.toast_delete_s)
                      .format(
                          getState().domainNoteTable.title,
                      )
              )*/
        }
    }

    override fun onIntent(intent: NoteListIntent) {
        return when (intent) {
            is NoteListIntent.OpenBottomSheetEntry -> openBottomSheetEntry(
                intent.value,
                intent.item
            )

            is NoteListIntent.OpenBottomSheetDetail -> openBottomSheetDetail(
                intent.value,
                intent.item
            )

            is NoteListIntent.SearchChanged -> updateSearch(intent.value)

            is NoteListIntent.TitleChanged -> updateTitle(intent.value)
            is NoteListIntent.NoteChanged -> updateNote(intent.value)
            NoteListIntent.Insert -> insert()
            NoteListIntent.Update -> update()
            is NoteListIntent.Delete -> delete(intent.value)
        }
    }

    override fun validation() {
        updateState { state ->
            state.copy(
                currentProduct = state.currentProduct.copy(
                    error = state.currentProduct.error.copy(
                        isErrorTitle = state.currentProduct.title.isBlank()
                    )
                )
            )
        }
    }

    private fun DomainNoteTable.toNoteStateList2(): NoteEntryState2 {
        return NoteEntryState2(
            itemId = this.id,
            title = this.title,
            note = this.note,
            date = this.date,
            itemIdPT = itemIdPT2,
            isEntry = false
        )
    }

    fun NoteEntryState2.toNoteStateList2(): DomainNoteTable {
        return DomainNoteTable(
            id = this.itemId,
            title = this.title,
            note = this.note,
            date = dateToday(),
            idPT = itemIdPT2
        )
    }
}

sealed class NoteListIntent() : BaseIntent {
    data class OpenBottomSheetEntry(
        val value: Boolean,
        val item: DomainNoteTable? = null
    ) : NoteListIntent()

    data class OpenBottomSheetDetail(
        val value: Boolean,
        val item: Int? = null
    ) : NoteListIntent()

    data class TitleChanged(val value: String) : NoteListIntent()
    data class NoteChanged(val value: String) : NoteListIntent()
    data class SearchChanged(val value: String) : NoteListIntent()

    data object Insert : NoteListIntent()
    data object Update : NoteListIntent()
    data class Delete(val value: Long) : NoteListIntent()
}
