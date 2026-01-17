//package com.zaroslikov.fermacompose2.ui.sections.note.entry
//
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.viewModelScope
//import com.zaroslikov.domain.repository.NoteRepository
//import com.zaroslikov.fermacompose2.R
//import com.zaroslikov.fermacompose2.base.intent.BaseIntent
//import com.zaroslikov.fermacompose2.base.viewModel.EntryViewModel
//import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
//import com.zaroslikov.fermacompose2.utils.ResourceProvider
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class NoteEntryViewModel @Inject constructor(
//    savedStateHandle: SavedStateHandle,
//    private val noteRepository: NoteRepository,
//    private val resourceProvider: ResourceProvider,
//) : EntryViewModel<NoteEntryState, NoteEntryIntent>(NoteEntryState()) {
//
//    private val itemIdPT: Long = checkNotNull(savedStateHandle[NoteEntryDestination.itemIdPT])
//    private val itemId: Long = checkNotNull(savedStateHandle[NoteEntryDestination.itemId])
//    private val isEntry: Boolean = itemId == -1L
//
//    init {
//        viewModelScope.launch {
//            loadInitialData/**/()
//            loadDataUpdate()
//        }
//    }
//
//    override fun onIntent(intent: NoteEntryIntent) {
//        return when (intent) {
//            is NoteEntryIntent.TitleChanged -> updateTitle(intent.value)
//            is NoteEntryIntent.NoteChanged -> updateSuffix(intent.value)
//            NoteEntryIntent.Insert -> insert()
//            NoteEntryIntent.Update -> update()
//            NoteEntryIntent.Delete -> delete()
//        }
//    }
//
//    private fun loadInitialData() {
//            updateState { state ->
//                state.copy(
//                    isEntry = isEntry,
//                    domainNoteTable = state.domainNoteTable.copy(
//                        idPT = itemIdPT
//                    )
//                )
//        }
//    }
//
//    private suspend fun loadDataUpdate() {
//        if (!isEntry) {
//            val domainNoteTable = noteRepository.getNote(itemId).first()
//            updateState { state ->
//                state.copy(domainNoteTable = domainNoteTable)
//            }
//        }
//    }
//
//    override fun insert() {
//        viewModelScope.launch {
//            if (!isError()) {
//                noteRepository.insertNote(getState().domainNoteTable)
//                navigateTo(UiEvent.NavigateBack)
//                showMessage(
//                    resourceProvider.getString(R.string.toast_delete_s)
//                        .format(
//                            getState().domainNoteTable.title,
//                        )
//                )
//            }
//        }
////            metricaNote(title)
//    }
//
//    override fun update() {
//        viewModelScope.launch {
//            if (!isError())
//                noteRepository.updateNote(getState().domainNoteTable)
//            navigateTo(UiEvent.NavigateBack)
//            showMessage(
//                resourceProvider.getString(R.string.toast_delete_s)
//                    .format(
//                        getState().domainNoteTable.title,
//                    )
//            )
//        }
//    }
//
//    override fun delete() {
//        viewModelScope.launch {
//            noteRepository.deleteNote(getState().domainNoteTable)
//            navigateTo(UiEvent.NavigateBack)
//            showMessage(
//                resourceProvider.getString(R.string.toast_delete_s)
//                    .format(
//                        getState().domainNoteTable.title,
//                    )
//            )
//        }
//    }
//
//    override fun validation() {
//        updateState { state ->
//            state.copy(
//                error = state.error.copy(
//                    isErrorTitle = state.domainNoteTable.title.isBlank()
//                )
//            )
//        }
//    }
//
//    private fun updateTitle(title: String) {
//        updateState { state ->
//            state.copy(
//                domainNoteTable = state.domainNoteTable.copy(
//                    title = title
//                ),
//                error = state.error.copy(
//                    isErrorTitle = title.isBlank()
//                )
//            )
//        }
//    }
//
//    private fun updateSuffix(note: String) {
//        updateState { state ->
//            state.copy(
//                domainNoteTable = state.domainNoteTable.copy(
//                    note = note
//                )
//            )
//        }
//    }
//}
//
//sealed class NoteEntryIntent : BaseIntent {
//    data class TitleChanged(val value: String) : NoteEntryIntent()
//    data class NoteChanged(val value: String) : NoteEntryIntent()
//    data object Insert : NoteEntryIntent()
//    data object Update : NoteEntryIntent()
//    data object Delete : NoteEntryIntent()
//}
