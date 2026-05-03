package com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.domain.models.enums.supportUi.ProductOperation
import com.zaroslikov.domain.repository.NoteRepository
import com.zaroslikov.domain.repository.ProjectRepository
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.base.viewModel.EntryNewViewModel2
import com.zaroslikov.fermacompose2.supportFun.YandexMetricRepository
import com.zaroslikov.fermacompose2.supportFun.dateToday
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
    private val projectRepository: ProjectRepository,
    private val yandexMetricRepository: YandexMetricRepository
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
            is NoteListIntent.Delete -> delete(0)
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
            showSnackbar(ProductOperation.ADD)
            yandexMetricRepository.metricalNote(getState().currentProduct.title)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun update() {
        viewModelScope.launch {
            noteRepository.updateNote(getState().currentProduct.toDomain())
            updateState { it.copy(detailDomainNoteTable = getState().currentProduct.toDomain()) }
            showSnackbar(ProductOperation.EDIT)
            loadDataForEntryOrEdit(false, null)
        }
    }

    override fun delete(id: Long) {
        viewModelScope.launch {
            getState().detailDomainNoteTable.let { product ->
                noteRepository.deleteNoteById(product.id)
                showSnackbar(ProductOperation.DELETE)
                sendIntent(NoteListIntent.OpenBottomSheetDelete(null))
            }
        }
    }

    private fun showSnackbar(productOperation: ProductOperation) {
        val title =
            if (productOperation == ProductOperation.DELETE)
                getState().detailDomainNoteTable.title
            else getState().currentProduct.title

        showMessage(
            when (productOperation) {
                ProductOperation.ADD -> resourceProvider.getString(R.string.snackbar_note_add)
                    .format(title)

                ProductOperation.EDIT -> resourceProvider.getString(R.string.snackbar_note_update)
                    .format(title)

                else -> resourceProvider.getString(R.string.snackbar_note_delete)
                    .format(title)
            }
        )
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
            title = title.trim(),
            note = note.trim(),
            date = dateToday(),
            idPT = itemIdPT
        )
    }
}