package com.zaroslikov.fermacompose2.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.ui.finance.main_screen.FinanceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    init {
        observeData()
    }

    private fun observeData() {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = today.withDayOfMonth(1).format(formatter)
        val end = today.withDayOfMonth(today.lengthOfMonth()).format(formatter)

        viewModelScope.launch {
            combine(
                financeRepository.getCurrentBalance(itemId),
                saleRepository.getIncome(itemId),
                expensesRepository.getExpenses(itemId),
                writeOffRepository.getOwnNeed(itemId),
                writeOffRepository.getScrap(itemId),
                saleRepository.getIncomeMount(itemId, start, end),
                expensesRepository.getExpensesMount(itemId, start, end),
                financeRepository.getIncomeExpensesCurrentMonth(itemId, start, end)
            ) { values: Array<Any?> ->
                val currentBalance = (values[0] as? Double) ?: 0.0
                val income = (values[1] as? Double) ?: 0.0
                val expenses = (values[2] as? Double) ?: 0.0
                val ownNeed = (values[3] as? Double) ?: 0.0
                val scrap = (values[4] as? Double) ?: 0.0
                val incomeMount = (values[5] as? Double) ?: 0.0
                val expensesMount = (values[6] as? Double) ?: 0.0
                val domainList =
                    (values[7] as? List<DomainIncomeExpenses>) ?: emptyList<DomainIncomeExpenses>()

                FinanceCombined(
                    currentBalance,
                    income,
                    expenses,
                    ownNeed,
                    scrap,
                    income + ownNeed - expenses - scrap,
                    incomeMount,
                    expensesMount,
                    domainList
                )
            }.collect { combined ->
                updateState {
                    it.copy(
                        isLoading = false,
                        idPT = itemId,
                        currentBalance = combined.currentBalance,
                        income = combined.income,
                        expenses = combined.expenses,
                        ownNeed = combined.ownNeed,
                        scrap = combined.scrap,
                        profit = combined.profit,
                        incomeMount = combined.incomeMount,
                        expensesMount = combined.expensesMount,
                        domainIncomeExpenseList = combined.list
                    )
                }
            }
        }
    }
}

data class FinanceCombined(
    val currentBalance: Double,
    val income: Double,
    val expenses: Double,
    val ownNeed: Double,
    val scrap: Double,
    val profit: Double,
    val incomeMount: Double,
    val expensesMount: Double,
    val list: List<DomainIncomeExpenses>
)
sealed class FinanceIntent() : BaseIntent