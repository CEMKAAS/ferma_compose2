package com.zaroslikov.fermacompose2.ui.animal.indicators.count

import com.zaroslikov.domain.models.dto.animal.DomainAnimalCountPrice
import com.zaroslikov.domain.models.dto.sale.DomainBuyerPrice
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalCountState(
    val domainAnimalCountPrice: DomainAnimalCountPrice = DomainAnimalCountPrice(),
    val countList: List<DomainAnimalCountPrice> = emptyList(),
    val buyerList: List<String> = emptyList(),
    val isOpenDialog: Boolean = false,
    val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null,
    override val isEntry: Boolean = false,
    override val error: Error = Error(),
) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    data class Error(
        val isErrorCount: Boolean = false,
        val isErrorCountZero: Boolean = false,
        val isErrorPrice: Boolean = false
    ) : BaseError {
        val hasAnyError: Boolean
            get() = isErrorCount || isErrorCountZero
    }

}