package com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.repository.NoteRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
    private val resourceProvider: ResourceProvider,
    private val projectRepository: ProjectRepository
) : EntryNewViewModel2<NoteListState, NoteListIntent, NoteListReduce>(
    NoteListState(),
    NoteListReduce(resourceProvider)
) {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[NoteDestination.itemIdArg])

    init {
        loadDate()
    }

    override fun onIntent(intent: NoteListIntent) {
        sendIntent(intent)
        return when (intent) {
            is NoteListIntent.OpenBottomSheetEntry -> loadDataForEntryOrEdit(
                intent.value,
                intent.item
            )

            NoteListIntent.Insert -> insert()
            NoteListIntent.Update -> update()
            is NoteListIntent.Delete -> delete(intent.value)
            else -> Unit
        }
    }

    private fun loadDate() {
        viewModelScope.launch {
            sendIntent(NoteListIntent.LoadingChanged(true))
            val isArchive = projectRepository.getIsArchiveProject(itemIdPT).first()
            noteRepository.getAllNote(itemIdPT).collectLatest { list ->
                sendIntent(NoteListIntent.LoadData(list))
                updateState { state -> state.copy(isArchive = isArchive) }
                sendIntent(NoteListIntent.LoadingChanged(false))
            }
        }
    }


    private fun loadDataForEntryOrEdit(
        isOpen: Boolean,
        domain: DomainNoteTable?,
        isSaveStateForBottomSheet: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isOpen) {
                val state =
                    if (isSaveStateForBottomSheet) getState().currentProduct
                    else NoteEntryState2()
                onIntent(
                    NoteListIntent.RefreshEntryBottomSheetState(
                        false, state, isSaveStateForBottomSheet
                    )
                )
                return@launch
            }

            val newState = if (!getState().isSaveStateForBottomSheet || domain != null) {

                val baseState = NoteEntryState2(
                    itemIdPT = itemIdPT,
                )

                domain?.toUi() ?: baseState
            } else getState().currentProduct
            onIntent(NoteListIntent.RefreshEntryBottomSheetState(true, newState))
        }
    }

    override fun insert() {
        viewModelScope.launch {
            noteRepository.insertNote(getState().currentProduct.toDomain())
            loadDataForEntryOrEdit(false, null)
            /* showMessage(
                 resourceProvider.getString(R.string.toast_delete_s)
                     .format(
                         getState().domainNoteTable.title,
                     )
             )*/
        }
//            metricaNote(title)
    }

    override fun update() {
        viewModelScope.launch {
            noteRepository.updateNote(getState().currentProduct.toDomain())
            updateState { it.copy(detailDomainNoteTable = getState().currentProduct.toDomain()) }
            loadDataForEntryOrEdit(false, null)
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

    private fun DomainNoteTable.toUi(): NoteEntryState2 {
        return NoteEntryState2(
            itemId = id,
            title = title,
            note = note,
            date = date,
            itemIdPT = itemIdPT,
            isEntry = false
        )
    }

    private fun NoteEntryState2.toDomain(): DomainNoteTable {
        return DomainNoteTable(
            id = itemId,
            title = title,
            note = note,
            date = dateToday(),
            idPT = itemIdPT
        )
    }
}