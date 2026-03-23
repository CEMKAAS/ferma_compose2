package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.weight

import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalWeightState(
    val weightList: List<AnimalWeightUi> = emptyList(),
    val idPT: Long = 0,
    val isOpenEntryBottomSheet: Boolean = false,
    val isSaveStateForEntry: Boolean = false,
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val currentProduct: CurrentAnimalWeight = CurrentAnimalWeight()
) : EntryNewState()

data class CurrentAnimalWeight(
    val id: Long = 0,
    val weight: String = "",
    val suffix: Suffix = Suffix.KILOGRAM,
    val date: String = dateToday(),
    val idAnimal: Long = 0,
    val note: String = "",
    val error: ErrorAnimalWeight = ErrorAnimalWeight(),
    val isEntry: Boolean = true,
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class AnimalWeightUi(
    val id: Long = 0,
    val weight: String = "",
    val suffix: Suffix = Suffix.KILOGRAM,
    val date: String = "",
    val idAnimal: Long = 0,
    val note: String = "",
    val totalValue: String = "",
    val indicationStatus: IndicationStatus = IndicationStatus.POSITIVE
)

data class ErrorAnimalWeight(
    val isErrorWeight: Boolean = false
) : BaseError