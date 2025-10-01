package com.zaroslikov.fermacompose2.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.dateTodayArray
import com.zaroslikov.fermacompose2.supportFun.firstDayOfMonth
import com.zaroslikov.fermacompose2.supportFun.todayOfMonth
import com.zaroslikov.fermacompose2.ui.finance.main_screen.FinanceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val month = dateTodayArray()[1]
            val year = dateTodayArray()[2]

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
                expensesRepository.getExpensesMountFin(itemId, month, year /*"$year-$month"*/)
                    .filterNotNull()
                    .first().toDouble()

            val domainIncomeExpenseList =
                financeRepository.getIncomeExpensesCurrentMonth(itemId, firstDayOfMonth().first, todayOfMonth().first)
                    .first()

            updateState { state ->
                state.copy(
                    isLoading = false,
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