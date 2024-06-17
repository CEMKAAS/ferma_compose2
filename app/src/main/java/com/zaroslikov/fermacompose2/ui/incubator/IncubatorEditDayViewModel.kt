package com.zaroslikov.fermacompose2.ui.incubator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zaroslikov.fermacompose2.data.ItemsRepository

class IncubatorEditDayViewModel (
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository

) : ViewModel() {}