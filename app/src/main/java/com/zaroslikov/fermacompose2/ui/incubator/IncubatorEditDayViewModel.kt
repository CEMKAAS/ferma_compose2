package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.incubator.IncubatorAiring
import com.zaroslikov.fermacompose2.data.incubator.IncubatorDamp
import com.zaroslikov.fermacompose2.data.incubator.IncubatorOver
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.finance.FinanceCategoryDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IncubatorEditDayViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository

) : ViewModel() {

     val itemId: Int = checkNotNull(savedStateHandle[IncubatorEditDayScreenDestination.itemIdArg])


    val day: Int = checkNotNull(savedStateHandle[IncubatorEditDayScreenDestination.itemIdArgTwo])

    var tempStateList by mutableStateOf(IncubatorState())
        private set

    var dampStateList by mutableStateOf(IncubatorState())
        private set

    var overStateList by mutableStateOf(IncubatorState())
        private set
    var airingStateList by mutableStateOf(IncubatorState())
        private set


    init {
        viewModelScope.launch {

            tempStateList = itemsRepository.getIncubatorTemp(itemId)
                .filterNotNull()
                .first()
                .toIncubatorState()

            dampStateList = itemsRepository.getIncubatorDamp(itemId)
                .filterNotNull()
                .first()
                .toIncubatorState()

            overStateList = itemsRepository.getIncubatorOver(itemId)
                .filterNotNull()
                .first()
                .toIncubatorState()

            airingStateList = itemsRepository.getIncubatorAiring(itemId)
                .filterNotNull()
                .first()
                .toIncubatorState()
        }
    }



    suspend fun saveIncubator(
        incubatorTemp: IncubatorTemp,
        incubatorDamp: IncubatorDamp,
        incubatorOver: IncubatorOver,
        incubatorAiring: IncubatorAiring
    ) {
        itemsRepository.updateIncubatorTemp(incubatorTemp)
        itemsRepository.updateIncubatorDamp(incubatorDamp)
        itemsRepository.updateIncubatorOver(incubatorOver)
        itemsRepository.updateIncubatorAiring(incubatorAiring)
    }



}


