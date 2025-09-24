package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalCountState(
    val countList: List<DomainAnimalCountPrice> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState()