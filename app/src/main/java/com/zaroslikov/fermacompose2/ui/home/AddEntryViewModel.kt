package com.zaroslikov.fermacompose2.ui.home

import android.util.Log
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import com.zaroslikov.fermacompose2.supportFun.DataTripleListState
import com.zaroslikov.fermacompose2.supportFun.metricAdd
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {

    private val itemIdPT: Int = checkNotNull(savedStateHandle[AddEntryDestination.itemIdPT])
    private val itemId: Int = checkNotNull(savedStateHandle[AddEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1
    var addUiState by mutableStateOf(
        DomainAddTable().copy(
            category = resourceProvider.getString(R.string.support_text_no_category),
            suffix = resourceProvider.getString(R.string.suffix_kilogram)
        )
    )
        private set


    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (!isEntry)
            viewModelScope.launch {
                addUiState = itemsRepository.getItemAdd(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()
            }
    }

    fun updateUiState(domainAddTable: DomainAddTable) {
        addUiState =
            domainAddTable
    }

    val titleUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsTitleAddList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategoryAddList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val animalUiState: StateFlow<DataTripleListState> =
        itemsRepository.getItemsAnimalAddList(itemId).map { DataTripleListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataTripleListState()
            )


    var itemUiState by mutableStateOf(DomainPairDataDoubleSting())
        private set

    fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            itemUiState = itemsRepository.getCurrentBalanceProduct(name, itemId.toLong())
                .filterNotNull()
                .first()
                .toDomainMap()
        }
    }

    fun insertItem() {
        viewModelScope.launch {
            itemsRepository.insertItem(addUiState.copy(idPT = itemIdPT.toLong()).toRoomMap())
            metricAdd(addUiState)
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_add_s)
                    .format(addUiState.title, addUiState.count, addUiState.suffix)
            )
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            Log.i("add", "updateItem: $addUiState")
            itemsRepository.updateItem(addUiState.toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_refresh_s)
                    .format(addUiState.title, addUiState.count, addUiState.suffix)
            )
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteItem(addUiState.toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(addUiState.title, addUiState.count, addUiState.suffix)
            )
        }
    }

    fun showMessage(message: String) {
//        viewModelScope.launch
//        {
//            .snackbarHostState.showSnackbar(message)
////            _eventFlow.emit(UiEvent.ShowSnackbar(message))
//        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}



