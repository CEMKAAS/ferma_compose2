package com.zaroslikov.fermacompose2.ui.animal.indicators.size

import android.app.Dialog
import com.zaroslikov.domain.models.table.DomainAnimalSize
import com.zaroslikov.fermacompose2.base.state.BaseError
import com.zaroslikov.fermacompose2.base.state.EntryState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class AnimalSizeState(
    val size: String = "",
    val suffix: String = "",
    val date: String = "",
    val note: String = "",
    val sizeList: List<DomainAnimalSize> = emptyList(),
    val idPT: Long = 0,
    val isAddOpenDialog: Boolean = false,
    val isEditOpenDialog: Boolean = false,
    override val isEntry: Boolean = false,
    override val error: Error = Error(),
    override val hasAnyError: Boolean = false,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : EntryState() {
    data class Error(
        val isError: Boolean = false
    ) : BaseError
}
