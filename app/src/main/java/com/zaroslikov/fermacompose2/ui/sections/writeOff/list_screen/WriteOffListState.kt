package com.zaroslikov.fermacompose2.ui.sections.writeOff.list_screen

import com.zaroslikov.domain.models.dto.write_off.BrieflyWriteOffDomain
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class WriteOffListState(
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    val writeOffBoolean : Boolean =false,
    val list : List<DomainWriteOffTable> = emptyList(),
    val listBriefly : List<BrieflyWriteOffDomain> = emptyList()

) : ListState()
