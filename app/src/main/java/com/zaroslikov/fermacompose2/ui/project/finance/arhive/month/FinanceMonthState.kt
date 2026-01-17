package com.zaroslikov.fermacompose2.ui.project.finance.arhive.month

import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.supportFun.firstDayOfMonth
import com.zaroslikov.fermacompose2.supportFun.todayOfMonth
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceMonthState(
    val currentPeriod: String = "",
    val isOpenCalendarDialog: Boolean = false,
    val incomeMonth: Double = 0.0,
    val expensesMonth: Double = 0.0,
    val ownNeedMonth: Double = 0.0,
    val scrapMonth: Double = 0.0,
    val incomeCategory: List<DomainCategoryPrice> = emptyList(),
    val expensesCategory: List<DomainCategoryPrice> = emptyList(),
    val dateBegin: Pair<String, Long> = firstDayOfMonth(),
    val dateEnd: Pair<String, Long> = todayOfMonth(),
    override val isLoading: Boolean = false,
    override val idPT: Long = 0,
    override val navigate: UiEvent? = null
) : ListState()