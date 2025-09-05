package com.zaroslikov.fermacompose2.ui.animal.list_screen

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalListState(
    val list: List<DomainAnimalWithCount> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null
) : ListState()
