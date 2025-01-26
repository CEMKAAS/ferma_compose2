package com.zaroslikov.fermacompose2.ui.sale

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.ui.home.AddEntryDestination
import com.zaroslikov.fermacompose2.ui.home.AnimalUiState
import com.zaroslikov.fermacompose2.ui.home.CategoryUiState
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
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

    val titleUiState: StateFlow<AnimalUiState> =
        itemsRepository.getItemsTitleSaleList(itemId).map { AnimalUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategorySaleList(itemId).map { CategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )


    val buyerUiState: StateFlow<BuyerUiState> =
        itemsRepository.getItemsBuyerSaleList(itemId).map { BuyerUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BuyerUiState()
            )

    var itemUiState by mutableStateOf(0.0)
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