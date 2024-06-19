package com.zaroslikov.fermacompose2.ui.incubator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class IncubatorOvoscopViewModel (
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dayVM: Int = checkNotNull(savedStateHandle[IncubatorOvoscopDestination.itemIdArg])
    val typeBirds: String =
        checkNotNull(savedStateHandle[IncubatorOvoscopDestination.itemIdArgTwo])
}