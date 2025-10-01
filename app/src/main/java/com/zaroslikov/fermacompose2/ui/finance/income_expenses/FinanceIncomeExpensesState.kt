package com.zaroslikov.fermacompose2.ui.finance.income_expenses

import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceIncomeExpensesState(
    val financeCategoryList: List<DomainCategoryPrice> = emptyList(),
    val financeProductList: List<DomainTitleSuffixPrice> = emptyList(),
//    val animalExpenses:
    val isIncome: Boolean = false,
    override val isLoading: Boolean = false,
    override val idPT: Long = 0,
    override val navigate: UiEvent? = null
) : ListState()

