package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size

import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.BaseProduct
import com.zaroslikov.fermacompose2.base.state.EntryNewState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalSizeState(
    val sizeList: List<AnimalSizeUi> = emptyList(),
    val idPT: Long = 0,
    val isOpenEntryBottomSheet: Boolean = false,
    val isSaveStateForEntry: Boolean = false,
    override val isEntry: Boolean = false,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    override val currentProduct: CurrentAnimalSize = CurrentAnimalSize()
) : EntryNewState()

data class CurrentAnimalSize(
    val id: Long = 0,
    val size: String = "",
    val suffix: Suffix = Suffix.MILLIMETERS,
    val date: String = dateToday(),
    val idAnimal: Long = 0,
    val note: String = "",
    val error: ErrorAnimalSize = ErrorAnimalSize(),
    val isEntry: Boolean = true,
    override val hasAnyError: Boolean = false
) : BaseProduct()

data class AnimalSizeUi(
    val id: Long = 0,
    val size: String = "",
    val suffix: Suffix = Suffix.MILLIMETERS,
    val date: String ="",
    val idAnimal: Long = 0,
    val note: String = "",
    val totalValue: String = "",
    val indicationStatus: IndicationStatus = IndicationStatus.POSITIVE
)

data class ErrorAnimalSize(
    val isErrorSize: Boolean = false
) : BaseError

