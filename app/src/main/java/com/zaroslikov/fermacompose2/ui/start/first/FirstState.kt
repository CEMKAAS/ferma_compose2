package com.zaroslikov.fermacompose2.ui.start.first

import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FirstState(
    val list: List<DomainProjectTable> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState()