package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.incubator.IncubatorAiring
import com.zaroslikov.fermacompose2.data.incubator.IncubatorDamp
import com.zaroslikov.fermacompose2.data.incubator.IncubatorOver
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IncubatorViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[IncubatorScreenDestination.itemIdArg])


//    var sexState by mutableIntStateOf(0)
//        private set
//    init {
//        viewModelScope.launch {
//            sexState = itemsRepository.getIncubatorTemp()
//                .filterNotNull()
//                .first()
//                .toInt()
//        }
//    }


    val tempState: StateFlow<IncubatorUiState> =
        itemsRepository.getIncubatorTemp(itemId).map { IncubatorUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorUiState()
            )

    val dampState: StateFlow<IncubatorDampUiState> =
        itemsRepository.getIncubatorDamp(itemId).map { IncubatorDampUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorDampUiState()
            )

    val overState: StateFlow<IncubatorOverUiState> =
        itemsRepository.getIncubatorOver(itemId).map { IncubatorOverUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorOverUiState()
            )
    val airingState: StateFlow<IncubatorAiringUiState> =
        itemsRepository.getIncubatorAiring(itemId).map { IncubatorAiringUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorAiringUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}

/**
 * Ui State for HomeScreen
 */
data class IncubatorUiState(val itemList: List<IncubatorTemp> = listOf())
data class IncubatorDampUiState(val itemList: List<IncubatorDamp> = listOf())
data class IncubatorOverUiState(val itemList: List<IncubatorOver> = listOf())
data class IncubatorAiringUiState(val itemList: List<IncubatorAiring> = listOf())



