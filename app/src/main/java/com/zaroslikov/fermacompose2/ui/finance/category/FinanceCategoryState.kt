package com.zaroslikov.fermacompose2.ui.finance.category

import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceCategoryState(
    val financeCategory: List<DomainTitleSuffixPrice> = emptyList(),
    override val isLoading: Boolean = false,
    override val idPT: Long = 0,
    override val navigate: UiEvent? = null
) : ListState()