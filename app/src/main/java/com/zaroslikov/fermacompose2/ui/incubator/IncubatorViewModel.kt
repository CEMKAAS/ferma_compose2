package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
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


//    var tempStateList by mutableStateOf(tempAS())
//        private set

//    init {
//        viewModelScope.launch {
//
//
//            tempStateList = itemsRepository.getIncubatorTemp()
//                .filterNotNull()
//                .first()
//                .toIncubatorTempAs()
//        }
//    }


    val tempState: StateFlow<IncubatorUiState> =
        itemsRepository.getIncubatorTemp(itemId).map { IncubatorUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorUiState()
            )

//    val dampState: StateFlow<IncubatorDampUiState> =
//        itemsRepository.getIncubatorDamp(itemId).map { IncubatorDampUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorDampUiState()
//            )
//
//    val overState: StateFlow<IncubatorOverUiState> =
//        itemsRepository.getIncubatorOver(itemId).map { IncubatorOverUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorOverUiState()
//            )
//    val airingState: StateFlow<IncubatorAiringUiState> =
//        itemsRepository.getIncubatorAiring(itemId).map { IncubatorAiringUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorAiringUiState()
//            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}

data class IncubatorUiState(val titleList:IncubatorTemp = IncubatorTemp(0, "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",0))



//data class tempAS(
//    val day1: String = "",
//    val day2: String = "",
//    val day3: String = "",
//    val day4: String = "",
//    val day5: String = "",
//    val day6: String = "",
//    val day7: String = "",
//    val day8: String = "",
//    val day9: String = "",
//    val day10: String = "",
//    val day11: String = "",
//    val day12: String = "",
//    val day13: String = "",
//    val day14: String = "",
//    val day15: String = "",
//    val day16: String = "",
//    val day17: String = "",
//    val day18: String = "",
//    val day19: String = "",
//    val day20: String = "",
//    val day21: String = "",
//    val day22: String = "",
//    val day23: String = "",
//    val day24: String = "",
//    val day25: String = "",
//    val day26: String = "",
//    val day27: String = "",
//    val day28: String = "",
//    val day29: String = "",
//    val day30: String = "",
//)
//
//fun IncubatorTemp.toIncubatorTempAs(): tempAS = tempAS(
//    day1,
//    day2,
//    day3,
//    day4,
//    day5,
//    day6,
//    day7,
//    day8,
//    day9,
//    day10,
//    day11,
//    day12,
//    day13,
//    day14,
//    day15,
//    day16,
//    day17,
//    day18,
//    day19,
//    day20,
//    day21,
//    day22,
//    day23,
//    day24,
//    day25,
//    day26,
//    day27,
//    day28,
//    day29,
//    day30
//)
//





