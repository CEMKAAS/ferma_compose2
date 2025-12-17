package com.zaroslikov.fermacompose2.ui.warehouse.warehouseScreen

import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class WarehouseState(
    val productList: List<DomainTitleCountSuffix> = emptyList(),
    val expensesList: List<DomainTitleCountSuffix> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState()