package com.zaroslikov.fermacompose2.ui.sections.writeOff.entry


import android.util.Log
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
import com.zaroslikov.fermacompose2.ui.composeElement.Category
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.entry.SaleEntryDestination
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
import kotlin.collections.firstOrNull

@HiltViewModel
class WriteOffEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val itemIdPT: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1


    var writeOffUiState by mutableStateOf(
        WriteOffEntryState().copy(
            isEntry = isEntry,
            countSuffix = resourceProvider.getString(R.string.suffix_pieces),
        )
    )
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            if (!isEntry) {
                val writeOffTable = itemsRepository.getItemWriteOff(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()
                writeOffUiState = writeOffUiState.updateFromDomain(writeOffTable)
            }
            val titleList = itemsRepository.getItemsWriteoffList(itemIdPT).first()
            writeOffUiState = writeOffUiState.updateList(titleList)

            val suffix = writeOffUiState.titleList
                .firstOrNull { it.first == writeOffUiState.title }
                ?.third

            suffix?.let {
                if (!isEntry) updateWarehouseUiStateSync(writeOffUiState.title, it)
            }
        }
    }

    fun updateUiState(state: WriteOffEntryState) {
        writeOffUiState =
            state
    }

    fun updateWarehouseUiState(name: String, category: Category) {
        viewModelScope.launch {
            updateWarehouseUiStateSync(name, category)
        }
    }

    private suspend fun updateWarehouseUiStateSync(name: String, category: Category) {
        val pair = if (category == Category.EXPENSES) {
            itemsRepository.getCurrentExpensesProductList(name, itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()
        } else {
            itemsRepository
                .getCurrentBalanceProductList(name, itemIdPT.toLong())
                .filterNotNull()
                .firstOrNull()
        }
        Log.i("Expenses_table", "updateWarehouseUiStateSync: $pair")
        Log.i("Expenses_table", "updateWarehouseUiStateSync: $category")
        if (pair != null) {
            writeOffUiState = writeOffUiState.updateCountWarehouse(pair)
        }
    }


    fun insertItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.insertWriteOff(
                    writeOffUiState.updateForSave(itemIdPT = itemIdPT.toLong()).toRoomMap()
                )
//            metricaWriteOff(writeOffUiState.copy(priceAll = autoCalculate()))
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_sale_s)
                        .format(
                            writeOffUiState.title,
                            writeOffUiState.count,
                            writeOffUiState.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            if (!isError()) {
                itemsRepository.updateWriteOff(
                    writeOffUiState.updateForSave(id = itemId.toLong(), itemIdPT.toLong())
                        .toRoomMap()
                )
                _eventFlow.emit(UiEvent.NavigateBack)
                showMessage(
                    resourceProvider.getString(R.string.toast_refresh_s_s)
                        .format(
                            writeOffUiState.title,
                            writeOffUiState.count,
                            writeOffUiState.countSuffix
                        ) //Todo Обновить название
                )
            }
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteWriteOff(itemId.toLong())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        writeOffUiState.title,
                        writeOffUiState.count,
                        writeOffUiState.countSuffix
                    ) //Todo Обновить название
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

    fun isError(): Boolean {
        updateUiState(writeOffUiState.validate())
        return writeOffUiState.error.hasAnyError
    }
}