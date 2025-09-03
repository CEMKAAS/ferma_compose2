package com.zaroslikov.fermacompose2.ui.sections.expenses.list_screen

import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDomain
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class ExpensesListState(
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val list: List<DomainExpensesTable> = emptyList(),
    val briefly: List<BrieflyExpensesDomain> = emptyList(),
) : ListState()
