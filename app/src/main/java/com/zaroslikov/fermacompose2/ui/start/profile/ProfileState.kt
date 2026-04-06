package com.zaroslikov.fermacompose2.ui.start.profile

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class ProfileState(
    val name: String = "Фермер",
    val currentFinanceCurrency: ProfileFinance = ProfileFinance(),
    val currencyList: List<ProfileFinance> = emptyList(),
    val isEditMode: Boolean = false,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
) : BaseState

data class ProfileFinance(
    val currentBalance: Double = 0.0,
    val priceSuffix: Suffix = Suffix.RUBLE,
    val projectFinance: ProjectFinance? = null,
    val incubatorFinance: IncubatorFinance? = null,
    val isSelected: Boolean = false
)

data class ProjectFinance(
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val scrap: Double = 0.0,
    val ownNeed: Double = 0.0,
)

data class IncubatorFinance(
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val bredEggs: Double = 0.0,
    val hatchedEggs: Double = 0.0,
)