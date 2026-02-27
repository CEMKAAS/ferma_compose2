package com.zaroslikov.fermacompose2.ui.incubator_project.finance

import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorHistory
import com.zaroslikov.domain.models.dto.incubator.DomainFinanceIncubatorMain
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceIncubatorState(
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val domainFinanceIncubatorHistory: List<DomainFinanceIncubatorHistory> = emptyList(),
    val domainFinanceIncubatorMain: DomainFinanceIncubatorMain = DomainFinanceIncubatorMain(),
    val priceSuffix : Suffix= Suffix.RUBLE
) : ListState()
