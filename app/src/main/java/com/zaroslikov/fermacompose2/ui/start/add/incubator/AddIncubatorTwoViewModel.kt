package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zaroslikov.fermacompose2.data.ItemsRepository

class AddIncubatorTwoViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    val itemId: Array<String> = checkNotNull(savedStateHandle[AddIncubatorTwoDestination.itemIdArg])


}