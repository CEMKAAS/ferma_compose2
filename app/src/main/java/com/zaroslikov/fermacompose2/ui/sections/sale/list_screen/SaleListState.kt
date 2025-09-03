package com.zaroslikov.fermacompose2.ui.sections.sale.list_screen

import com.zaroslikov.domain.models.DomainSaleTable
import com.zaroslikov.domain.models.dto.sale.BrieflySaleDomain
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class SaleListState(
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val list: List<DomainSaleTable> = emptyList(),
    val briefly: List<BrieflySaleDomain> = emptyList()
) : ListState()
