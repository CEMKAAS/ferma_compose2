package com.zaroslikov.fermacompose2.ui.finance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class FinanceViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[FinanceDestination.itemIdArg])

    var currentBalanceUiState by mutableDoubleStateOf(0.00)
        private set

    var incomeUiState by mutableDoubleStateOf(0.00)
        private set

    var expensesUiState by mutableDoubleStateOf(0.00)
        private set

    init {
        viewModelScope.launch {
            currentBalanceUiState = itemsRepository.getCurrentBalance(itemId)
                .filterNotNull()
                .first().toDouble()
        }
        viewModelScope.launch {
            incomeUiState = itemsRepository.getIncome(itemId)
                .filterNotNull()
                .first().toDouble()
        }
        viewModelScope.launch {
            expensesUiState = itemsRepository.getExpenses(itemId)
                .filterNotNull()
                .first().toDouble()
        }
    }

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val month = calendar[Calendar.MONTH] + 1
    val year = calendar[Calendar.YEAR]

    val incomeCategoryUiState: StateFlow<IncomeCategoryUiState> =
        itemsRepository.getCategoryIncomeCurrentMonth(itemId,month,year).map {IncomeCategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncomeCategoryUiState()
            )

    val expensesCategoryUiState: StateFlow<IncomeCategoryUiState> =
        itemsRepository.getCategoryExpensesCurrentMonth(itemId,month,year).map { IncomeCategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncomeCategoryUiState()
            )

    val incomeExpensesUiState: StateFlow<IncomeExpensesCategoryUiState> =
        itemsRepository.getIncomeExpensesCurrentMonth(itemId,month,year).map { IncomeExpensesCategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncomeExpensesCategoryUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class IncomeCategoryUiState(val itemList: List<Fin> = listOf())
data class IncomeExpensesCategoryUiState(val itemList: List<IncomeExpensesDetails> = listOf())

data class FinanceDetails(
    val currentBalanceFinance: Double = 0.0,
    val incomeFinance: Double = 0.0,
    val expensesFinance: Double = 0.0
)

data class IncomeExpensesDetails(
    val title: String,
    val count: Double,
    val suffix: String,
    val priceAll: Double,
    val day: Int,
    val mount: Int,
    val year: Int
)