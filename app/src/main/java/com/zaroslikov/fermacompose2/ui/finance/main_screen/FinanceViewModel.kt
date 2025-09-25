package com.zaroslikov.fermacompose2.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.ui.finance.main_screen.FinanceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val financeRepository: FinanceRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository
) : ListViewModel<FinanceState, FinanceIntent>(FinanceState()) {

    private val itemId: Long = checkNotNull(savedStateHandle[FinanceDestination.itemIdArg])

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

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val currentBalance = financeRepository.getCurrentBalance(itemId)
                .filterNotNull()
                .first().toDouble()

            val income = saleRepository.getIncome(itemId)
                .filterNotNull()
                .first().toDouble()

            val expenses = expensesRepository.getExpenses(itemId)
                .filterNotNull()
                .first().toDouble()

            val ownNeed = writeOffRepository.getOwnNeed(itemId)
                .filterNotNull()
                .first().toDouble()

            val scrap = writeOffRepository.getScrap(itemId)
                .filterNotNull()
                .first().toDouble()

            val incomeMount = saleRepository.getIncomeMountFin(itemId, month, year)
                .filterNotNull()
                .first().toDouble()

            val expensesMount =
                expensesRepository.getExpensesMountFin(itemId, month, year, "$year-$month")
                    .filterNotNull()
                    .first().toDouble()

            val domainIncomeExpenseList =
                financeRepository.getIncomeExpensesCurrentMonth(itemId, calBegin(), calEnd())
                    .first()

            updateState { state ->
                state.copy(
                    idPT = itemId,
                    currentBalance = currentBalance,
                    income = income,
                    expenses = expenses,
                    ownNeed = ownNeed,
                    scrap = scrap,
                    incomeMount = incomeMount,
                    expensesMount = expensesMount,
                    domainIncomeExpenseList = domainIncomeExpenseList
                )
            }
        }
    }
}

sealed class FinanceIntent() : BaseIntent

data class IncomeCategoryUiState(val itemList: List<Fin> = listOf())
data class IncomeExpensesCategoryUiState(val itemList: List<IncomeExpensesDetails> = listOf())

data class IncomeExpensesDetails(
    val title: String,
    val count: Double,
    val suffix: String,
    val priceAll: Double,
    val date: String
)