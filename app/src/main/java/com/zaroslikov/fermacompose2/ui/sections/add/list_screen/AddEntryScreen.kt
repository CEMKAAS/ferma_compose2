package com.zaroslikov.fermacompose2.ui.sections.add.list_screen

import com.zaroslikov.domain.models.DomainAddTable
import com.zaroslikov.domain.models.dto.add.BrieflyAddDomain
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AddListState(
    override val idPT: Long =0,
    val list: List<DomainAddTable> = emptyList(),
    val briefly: List<BrieflyAddDomain> = emptyList(),
    val listBriefly: List<DomainAddTable> = emptyList(),
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null

) : ListState()

