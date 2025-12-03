package com.zaroslikov.fermacompose2.ui.animal.indicators.weight

import com.zaroslikov.domain.models.table.DomainAnimalWeight
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalWeightState(
    val domainAnimalWeight: DomainAnimalWeight = DomainAnimalWeight(),
    val weightList: List<DomainAnimalWeight> = emptyList(),
    val idPT: Long = 0,
    val isOpenDialog: Boolean = false,
    override val isEntry: Boolean = false,
    override val error: Error = Error(),
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : EntryState() {
    override val hasAnyError: Boolean
        get() = error.hasAnyError

    fun enabledButton(): Boolean {
        val isEnabled = domainAnimalWeight.weight.isNotBlank() && !hasAnyError
        return !isEnabled
    }

    data class Error(
        val isErrorWeight: Boolean = false
    ) : BaseError {
        val hasAnyError: Boolean
            get() = isErrorWeight
    }
}