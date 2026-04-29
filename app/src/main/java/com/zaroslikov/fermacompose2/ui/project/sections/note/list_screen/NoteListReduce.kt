package com.zaroslikov.fermacompose2.ui.project.sections.note.list_screen

import com.zaroslikov.domain.models.DomainNoteTable
import com.zaroslikov.fermacompose2.base.reduce.BaseReducer
import com.zaroslikov.fermacompose2.supportFun.dateBuilder
import com.zaroslikov.fermacompose2.supportFun.monthToResString
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import kotlin.collections.find

class NoteListReduce(
    private val resourceProvider: ResourceProvider
) : BaseReducer<NoteListState, NoteListIntent>() {
    override fun reducer(
        state: NoteListState,
        intent: NoteListIntent
    ): NoteListState {
        return when (intent) {
            is NoteListIntent.LoadingChanged -> state.updateLoadingChanged(intent.value)
            is NoteListIntent.LoadData -> state.updateLoadData(intent.value)

            is NoteListIntent.OpenBottomSheetDetail -> state.openBottomSheetDetail(intent.value)
            is NoteListIntent.OpenBottomSheetDelete -> state.updateOpenBottomSheetDelete(intent.value)

            is NoteListIntent.RefreshEntryBottomSheetState -> state.updateEntryBottomSheet(
                isOpenEntryBottomSheet = intent.isOpen,
                entryState2 = intent.state,
                isSaveStateForBottomSheet = intent.isSaveStateForBottomSheet
            ).updateValidation()

            is NoteListIntent.SearchChanged -> state.updateSearch(intent.value)

            is NoteListIntent.TitleChanged -> state.updateTitle(intent.value).updateValidation()
            is NoteListIntent.NoteChanged -> state.updateNote(intent.value)
            else -> state

        }
    }

    private fun NoteListState.updateOpenBottomSheetDelete(id: Long?): NoteListState {
        return if (id == null)
            copy(isOpenBottomSheetDelete = false)
        else {
            val domain = list.find { it.id == id }
           domain?.let {
                copy(
                    isOpenBottomSheetDelete  = true,
                    detailDomainNoteTable = domain
                )
            } ?: copy(
               isOpenBottomSheetDelete  = false
            )
        }
    }

    private fun NoteListState.updateLoadData(list: List<DomainNoteTable>): NoteListState {
        return copy(
            list = list,
            searchList = list,
            isLoading = false
        )
    }

    private fun NoteListState.updateLoadingChanged(isLoading: Boolean): NoteListState {
        return copy(
            isLoading = isLoading
        )
    }

    private fun NoteListState.updateEntryBottomSheet(
        isOpenEntryBottomSheet: Boolean,
        entryState2: NoteEntryState2,
        isSaveStateForBottomSheet: Boolean
    ): NoteListState {
        return copy(
            isOpenBottomSheetEntry = isOpenEntryBottomSheet,
            currentProduct = entryState2,
            isSaveStateForBottomSheet = isSaveStateForBottomSheet
        )
    }

    fun NoteListState.updateValidation(): NoteListState {
        return copy(
            currentProduct = currentProduct.copy(
                hasAnyError = currentProduct.title.isNotBlank()
            )
        )
    }

    private fun NoteListState.updateTitle(title: String): NoteListState {
        return copy(
            currentProduct = currentProduct.copy(
                title = title,
                error = currentProduct.error.copy(
                    isErrorTitle = title.isBlank()
                )
            )
        )
    }

    private fun NoteListState.updateNote(note: String): NoteListState {
        return copy(
            currentProduct = currentProduct.copy(
                note = note
            )
        )
    }

    private fun NoteListState.openBottomSheetDetail(
        id: Long?
    ): NoteListState {
        return if (id == null)
            copy(isOpenBottomSheetDetail = false)
        else {
            val domain = list.find { it.id == id }
            domain?.let {
                copy(
                    isOpenBottomSheetDetail = true,
                    detailDomainNoteTable = domain
                )
            } ?: copy(
                isOpenBottomSheetDetail = false
            )
        }
    }


    private fun NoteListState.updateSearch(textSearch: String): NoteListState {
        val query = textSearch.trim().lowercase()
        val searchList = if (query.isBlank()) list
        else
            list.filter { item ->
                val date = item.date.split(".")
                val monthText = resourceProvider.getString(id = monthToResString(date[1].toInt()))
                item.title.lowercase().contains(query) ||
                        item.note.lowercase().contains(query) ||
                        dateBuilder(date[0].toInt(), monthText, date[2].toInt()).lowercase()
                            .contains(query)
            }

        return copy(
            textSearch = textSearch,
            searchList = searchList
        )
    }
}