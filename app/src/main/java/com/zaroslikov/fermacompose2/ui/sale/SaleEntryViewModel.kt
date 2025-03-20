package com.zaroslikov.fermacompose2.ui.sale

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf

import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.supportFun.DataPairListState
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SaleEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[SaleEntryDestination.itemIdArg])

    val titleUiState: StateFlow<DataPairListState> =
        itemsRepository.getItemsTitleSaleList(itemId).map {  DataPairListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue =  DataPairListState()
            )

    val categoryUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsCategorySaleList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )


    val buyerUiState: StateFlow<DataStringListState> =
        itemsRepository.getItemsBuyerSaleList(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )

    var itemUiState by mutableDoubleStateOf(0.0)
        private set

    fun updateUiState(pair: Pair<String, String>) {
        viewModelScope.launch {
            itemUiState = when (pair.second) {
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

    suspend fun saveItem(saleTable: SaleTable) {
        itemsRepository.insertSale(saleTable)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}