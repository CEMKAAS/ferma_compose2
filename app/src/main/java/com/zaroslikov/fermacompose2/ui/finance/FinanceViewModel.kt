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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class FinanceViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[FinanceDestination.itemIdArg])

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val month = calendar[Calendar.MONTH] + 1
    val year = calendar[Calendar.YEAR]

    private fun calBegin(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        calendar.set(year, month, 1)
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(calendar.timeInMillis)
    }


    private fun calEnd(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(calendar.timeInMillis)
    }


    var currentBalanceUiState by mutableDoubleStateOf(0.00)
        private set

    var incomeUiState by mutableDoubleStateOf(0.00)
        private set

    var expensesUiState by mutableDoubleStateOf(0.00)
        private set

    var ownNeedUiState by mutableDoubleStateOf(0.00)
        private set
    var scrapUiState by mutableDoubleStateOf(0.00)
        private set


    var incomeMountUiState by mutableDoubleStateOf(0.00)
        private set
    var expensesMountUiState by mutableDoubleStateOf(0.00)
        private set


    init {
        viewModelScope.launch {

            currentBalanceUiState = itemsRepository.getCurrentBalance(itemId)
                .filterNotNull()
                .first().toDouble()

            incomeUiState = itemsRepository.getIncome(itemId)
                .filterNotNull()
                .first().toDouble()

            expensesUiState = itemsRepository.getExpenses(itemId)
                .filterNotNull()
                .first().toDouble()

            ownNeedUiState = itemsRepository.getOwnNeed(itemId)
                .filterNotNull()
                .first().toDouble()

            scrapUiState = itemsRepository.getScrap(itemId)
                .filterNotNull()
                .first().toDouble()

            incomeMountUiState = itemsRepository.getIncomeMountFin(itemId, month, year)
                .filterNotNull()
                .first().toDouble()

            expensesMountUiState = itemsRepository.getExpensesMountFin(itemId, month, year, "$year-$month")
                .filterNotNull()
                .first().toDouble()
        }
    }

    val incomeExpensesUiState: StateFlow<IncomeExpensesCategoryUiState> =
        itemsRepository.getIncomeExpensesCurrentMonth(itemId, calBegin(), calEnd())
            .map { IncomeExpensesCategoryUiState(it) }
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

data class IncomeExpensesDetails(
    val title: String,
    val count: Double,
    val suffix: String,
    val priceAll: Double,
    val date: String
)