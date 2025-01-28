package com.zaroslikov.fermacompose2.ui.warehouse

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.data.water.ExpensesUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.TimeZone

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

    val fastAddUiState: StateFlow<FastAddUiState> =
        itemsRepository.getFastAddProduct(itemId.toLong()).map { FastAddUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FastAddUiState()
            )

    suspend fun saveItem(writeOffTable: WriteOffTable, expensesTable: ExpensesTable) {
        itemsRepository.updateExpenses(expensesTable.copy(showFood = false, showWarehouse = false))
        itemsRepository.insertWriteOff(writeOffTable)
    }

    suspend fun saveFastAddItem(fastAdd: FastAdd) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        itemsRepository.insertItem(
            item = AddTable(
                0,
                title = fastAdd.title,
                count = fastAdd.disc,
                day = calendar[Calendar.DAY_OF_MONTH],
                mount = calendar[Calendar.MONTH] + 1,
                year = calendar[Calendar.YEAR],
                priceAll = 0.0,
                suffix = fastAdd.suffix,
                category = fastAdd.category,
                idAnimal = fastAdd.idAnimal,
                animal = fastAdd.animal,
                note = "",
                idPT = itemId
            )
        )
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


data class WarehouseUiState(val itemList: List<WarehouseData> = listOf())
data class FastAddUiState(val itemList: List<FastAdd> = listOf())

data class FastAdd(
    val title: String,
    val disc: Double,
    val suffix : String,
    val category: String,
    val idAnimal: Long,
    val animal: String,
    val count: Int
)