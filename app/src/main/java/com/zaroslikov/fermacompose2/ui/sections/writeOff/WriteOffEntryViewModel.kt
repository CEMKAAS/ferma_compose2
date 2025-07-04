package com.zaroslikov.fermacompose2.ui.sections.writeOff


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.Domain.models.DomainWriteOffTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import com.zaroslikov.fermacompose2.supportFun.calculatePriceAll
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent
import com.zaroslikov.fermacompose2.ui.sections.sale.SaleEntryDestination
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import com.zaroslikov.fermacompose2.utils.SnackbarController
import com.zaroslikov.fermacompose2.utils.SnackbarEvent
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
class WriteOffEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val itemIdPT: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdPT])
    private val itemId: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemId])
    val isEntry: Boolean = itemId == -1
    var isIndicatorsValue = false

    val isAutoCalculate = mutableStateOf(false)

    var writeOffUiState by mutableStateOf(
        DomainWriteOffTable().copy(
            suffix = resourceProvider.getString(R.string.suffix_pieces),
        )
    )
        private set

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (!isEntry)
            viewModelScope.launch {
                writeOffUiState = itemsRepository.getItemWriteOff(itemId)
                    .filterNotNull()
                    .first()
                    .toDomainMap()
                isIndicatorsValue = writeOffUiState.animalCountId != null
            }
    }

    fun updateUiState(domainTable: DomainWriteOffTable) {
        writeOffUiState =
            domainTable
    }

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsWriteoffList(itemId).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataPairListState()
            )


    var itemUiState by mutableStateOf(DomainPairDataDoubleSting())
        private set

    fun updateWarehouseUiState(pair: Pair<String, Boolean>) {
        viewModelScope.launch {
            itemUiState = if (pair.second) {
                itemsRepository.getCurrentBalanceProduct(pair.first, itemId.toLong())
                    .filterNotNull()
                    .first()
                    .toDomainMap()
            } else {
                DomainPairDataDoubleSting()
//                itemsRepository.getCurrentExpensesProduct(pair.first, itemId.toLong())
//                    .filterNotNull()
//                    .first()
//                    .toDouble()
            }
        }
    }


    fun insertItem() {
        viewModelScope.launch {
            itemsRepository.insertWriteOff(
                writeOffUiState.copy(
                    priceAll = autoCalculate(),
                    idPT = itemIdPT.toLong()
                ).toRoomMap()
            )
//            metricaWriteOff(writeOffUiState.copy(priceAll = autoCalculate()))
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_sale_s)
                    .format(
                        writeOffUiState.title,
                        writeOffUiState.count,
                        writeOffUiState.suffix
                    ) //Todo Обновить название
            )
        }
    }

    fun updateItem() {
        viewModelScope.launch {
            autoCalculate()
            itemsRepository.updateWriteOff(writeOffUiState.copy(priceAll = autoCalculate()).toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_refresh_s_s)
                    .format(
                        writeOffUiState.title,
                        writeOffUiState.count,
                        writeOffUiState.suffix
                    ) //Todo Обновить название
            )
        }
    }

    fun deleteItem() {
        viewModelScope.launch {
            itemsRepository.deleteWriteOff(writeOffUiState.toRoomMap())
            _eventFlow.emit(UiEvent.NavigateBack)
            showMessage(
                resourceProvider.getString(R.string.toast_delete_s)
                    .format(
                        writeOffUiState.title,
                        writeOffUiState.count,
                        writeOffUiState.suffix
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

    fun autoCalculate(): String = if (isAutoCalculate.value) calculatePriceAll(
        writeOffUiState.priceAll,
        writeOffUiState.count
    ) else writeOffUiState.priceAll

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}