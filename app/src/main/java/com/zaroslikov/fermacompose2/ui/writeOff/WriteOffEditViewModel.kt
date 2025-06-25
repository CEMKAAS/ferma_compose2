package com.zaroslikov.fermacompose2.ui.writeOff

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainPairDataDoubleSting
import com.zaroslikov.fermacompose2.Domain.models.DomainWritOffTable
import com.zaroslikov.fermacompose2.data.ItemsRepository

import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WriteOffEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[WriteOffEditDestination.itemIdArg])
    private val itemIdPT: Int = checkNotNull(savedStateHandle[WriteOffEditDestination.itemIdArgTwo])

    var itemUiState by mutableStateOf(DomainWritOffTable())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemWriteOff(itemId)
                .filterNotNull()
                .first()
                .toDomainMap()
        }
    }

    fun updateUiState(itemDetails: DomainWritOffTable) {
        itemUiState =
            itemDetails
    }

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsWriteoffList(itemIdPT).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataPairListState()
            )
    var countWarehouseUiState by mutableStateOf(DomainPairDataDoubleSting())
        private set

    fun updateCountWarehouseUiState(pair: Pair<String, Boolean>) {
        viewModelScope.launch {
            countWarehouseUiState = if (pair.second) {
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


    suspend fun saveItem() {
        itemsRepository.updateWriteOff(itemUiState.toRoomMap())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteWriteOff(itemUiState.toRoomMap())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}
