package com.zaroslikov.fermacompose2.ui.project.sections.animal.indicators.size

import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalSizeState(
    val domainAnimalSize: DomainAnimalSize = DomainAnimalSize(),
    val sizeList: List<DomainAnimalSize> = emptyList(),
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
        val isEnabled = domainAnimalSize.size.isNotBlank() && !hasAnyError
        return !isEnabled
    }

    data class Error(
        val isErrorSize: Boolean = false
    ) : BaseError {
        val hasAnyError: Boolean
            get() = isErrorSize
    }
}


