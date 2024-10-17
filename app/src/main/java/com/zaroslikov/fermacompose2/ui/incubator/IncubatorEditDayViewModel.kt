package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IncubatorEditDayViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository

) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[IncubatorEditDayScreenDestination.itemIdArg])
    val day: Int = checkNotNull(savedStateHandle[IncubatorEditDayScreenDestination.itemIdArgTwo])

    var incubatorState by mutableStateOf(IncubatorUiState())
        private set

    init {
        viewModelScope.launch {
            incubatorState = itemsRepository.getIncubatorEditDay(itemId, day)
                .filterNotNull()
                .first()
                .toIncubatorUiState()
        }
    }

    fun updateUiState(itemDetails: IncubatorUiState) {
        incubatorState =
            itemDetails
    }

    suspend fun saveItem() {
        itemsRepository.updateIncubator(incubatorState.toIncubator())
    }


}

data class IncubatorUiState(
    val id: Int = 0,
    val day: Int = 0,
    val temp: String = "",
    val damp: String = "",
    var over: String = "",
    var airing: String = "",
    var idPT: Long = 0
)

fun Incubator.toIncubatorUiState(): IncubatorUiState =  IncubatorUiState(
    id, day, temp, damp, over, airing, idPT
)

fun IncubatorUiState.toIncubator(): Incubator = Incubator(
    id, day, temp, damp, over, airing, idPT
)