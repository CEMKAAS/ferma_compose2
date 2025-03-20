package com.zaroslikov.fermacompose2.ui.sale

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toRoomMap
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SaleEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[SaleEditDestination.itemIdArg])
    private val itemIdPT: Int = checkNotNull(savedStateHandle[SaleEditDestination.itemIdArgTwo])

    var itemUiState by mutableStateOf(DomainSaleTable())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemSale(itemId)
                .filterNotNull()
                .first()
                .toDomainMap()
        }
    }

    fun updateUiState(itemDetails: DomainSaleTable) {
        itemUiState =
            itemDetails
    }

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsTitleSaleList(itemIdPT).map { DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataPairListState()
            )

    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategorySaleList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    val buyerUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsBuyerSaleList(itemIdPT).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    var countWarehouseUiState by mutableDoubleStateOf(0.0)
        private set

    fun updateCountWarehouseUiState(pair: Pair<String, String>) {
        viewModelScope.launch {
            countWarehouseUiState = when (pair.second) {
                "Моя Продукция" -> {
                    itemsRepository.getCurrentBalanceProduct(pair.first, itemId.toLong())
                        .filterNotNull()
                        .first()
                        .toDouble()
                }
                "Купленный товар" -> {
                    itemsRepository.getCurrentExpensesProduct(pair.first, itemId.toLong())
                        .filterNotNull()
                        .first()
                        .toDouble()
                }
                else -> 0.0
            }
        }
    }

    suspend fun saveItem() {
        itemsRepository.updateSale(itemUiState.toRoomMap())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteSale(itemUiState.toRoomMap())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

