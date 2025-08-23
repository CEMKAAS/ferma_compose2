package com.zaroslikov.fermacompose2.ui.sections.add.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.mapper.AnimaMapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.AnimaMapper.toRoomMap
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
        AddEntryState().copy(
            isEntry = isEntry,
            category = resourceProvider.getString(R.string.support_text_no_category),
            countSuffix = resourceProvider.getString(R.string.suffix_pieces)
        )
    )
        private set

    fun updateUiState(domainAddTable: AddEntryState) {
        addUiState =
            domainAddTable
    }

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            if (!isEntry) {
                val domainAddTable = itemsRepository.getItemAdd(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()

                addUiState = addUiState.updateFromDomain(domainAddTable)

                updateWarehouseUiStateSync(addUiState.title)

                addUiState.animalId?.let {
                    addUiState = addUiState.updateAnimalNameById(
                        itemsRepository.getAnimalById(it).first()
                    )
                }
            }

            val titleList = itemsRepository.getItemsTitleAddList(itemIdPT).first()
            val categoryList = itemsRepository.getItemsCategoryAddList(itemIdPT).first()
            val animalList = itemsRepository.getItemsAnimalAddList(itemIdPT).first()
            addUiState = addUiState.updateList(titleList, categoryList, animalList)
        }
    }

    fun updateWarehouseUiState(name: String) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String) {
        val pair = itemsRepository
            .getCurrentBalanceProductList(name, itemIdPT.toLong())
            .filterNotNull()
            .firstOrNull()

        if (pair != null) {
            addUiState = addUiState.updateCountWarehouse(pair)
        }
    }




    fun insertItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.insertItem(
                    addUiState.updateForSave(itemIdPT = itemIdPT.toLong()).toRoomMap()
                )
//                metricAdd(addUiState)
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_add_s)
                        .format(addUiState.title, addUiState.count, addUiState.countSuffix)
                )
            }
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.updateItem(
                    addUiState.updateForSave(id = itemId.toLong(), itemIdPT = itemIdPT.toLong())
                        .toRoomMap()
                )
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s)
                        .format(addUiState.title, addUiState.count, addUiState.countSuffix)
                )
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteAddById(itemId.toLong())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(addUiState.title, addUiState.count, addUiState.countSuffix)
            )
        }
    }

    fun showMessage(message: String) {
        viewModelScope.launch {
            SnackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message
                )
            )
        }
    }

    private fun isError(): Boolean {
        updateUiState(addUiState.validate())
        return addUiState.error.hasAnyError
    }
}



