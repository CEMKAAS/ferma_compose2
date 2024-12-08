package com.zaroslikov.fermacompose2.ui.warehouse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesUiState
import com.zaroslikov.fermacompose2.ui.expenses.toExpensesTable
import com.zaroslikov.fermacompose2.ui.home.HomeDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WarehouseViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[WarehouseDestination.itemIdArg])

    val homeUiState: StateFlow<WarehouseUiState> =
        itemsRepository.getCurrentBalanceWarehouse(itemId).map { WarehouseUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = WarehouseUiState()
            )

    val homeFoodUiState: StateFlow<ExpensesUiState> =
        itemsRepository.getCurrentFoodWarehouse(itemId).map { ExpensesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ExpensesUiState()
            )

    val homeExpensesUiState: StateFlow<WarehouseUiState> =
        itemsRepository.getCurrentExpensesWarehouse(itemId).map { WarehouseUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = WarehouseUiState()
            )

    suspend fun saveItem(writeOffTable: WriteOffTable, expensesTable: ExpensesTable) {
        itemsRepository.updateExpenses(expensesTable.copy(food = false, showWarehouse = false))
        itemsRepository.insertWriteOff(writeOffTable)
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class WarehouseUiState(val itemList: List<WarehouseData> = listOf())