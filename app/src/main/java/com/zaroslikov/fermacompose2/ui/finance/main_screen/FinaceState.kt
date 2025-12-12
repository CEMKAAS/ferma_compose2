package com.zaroslikov.fermacompose2.ui.finance.main_screen

import com.zaroslikov.domain.models.dto.finance.DomainIncomeExpenses
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent


data class FinanceState(
    val currentBalance: Double = 0.0,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val ownNeed: Double = 0.0,
    val scrap: Double = 0.0,
    val profit: Double = 0.0,
    val incomeMount: Double = 0.0,
    val expensesMount: Double = 0.0,
    val domainIncomeExpenseList: List<DomainIncomeExpenses> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState()