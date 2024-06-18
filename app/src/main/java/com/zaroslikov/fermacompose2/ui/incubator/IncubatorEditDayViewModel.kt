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

    fun updateTempUiState(itemDetails: IncubatorState) {
        tempStateList =
            itemDetails
    }

    fun updateDampUiState(itemDetails: IncubatorState) {
        dampStateList =
            itemDetails
    }

    fun updateOverUiState(itemDetails: IncubatorState) {
        overStateList =
            itemDetails
    }

    fun updateAiringUiState(itemDetails: IncubatorState) {
        airingStateList =
            itemDetails
    }

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

    suspend fun saveItem() {
        itemsRepository.updateIncubatorTemp(tempStateList.toIncubatorTemp())
        itemsRepository.updateIncubatorDamp(dampStateList.toIncubatorDamp())
        itemsRepository.updateIncubatorOver(overStateList.toIncubatorOver())
        itemsRepository.updateIncubatorAiring(airingStateList.toIncubatorAiring())
    }

//    suspend fun saveIncubator(
//        incubatorTemp: IncubatorTemp,
//        incubatorDamp: IncubatorDamp,
//        incubatorOver: IncubatorOver,
//        incubatorAiring: IncubatorAiring
//    ) {
//        itemsRepository.updateIncubatorTemp(incubatorTemp)
//        itemsRepository.updateIncubatorDamp(incubatorDamp)
//        itemsRepository.updateIncubatorOver(incubatorOver)
//        itemsRepository.updateIncubatorAiring(incubatorAiring)
//    }

}

fun  IncubatorState.toIncubatorTemp():IncubatorTemp = IncubatorTemp(
    id, day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30, idPT
)

fun  IncubatorState.toIncubatorDamp():IncubatorDamp = IncubatorDamp(
    id, day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30, idPT
)

fun  IncubatorState.toIncubatorOver():IncubatorOver = IncubatorOver(
    id, day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30, idPT
)

fun  IncubatorState.toIncubatorAiring():IncubatorAiring = IncubatorAiring(
    id, day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30, idPT
)