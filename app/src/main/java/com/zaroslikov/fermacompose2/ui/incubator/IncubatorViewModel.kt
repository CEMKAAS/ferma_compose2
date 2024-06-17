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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class IncubatorViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[IncubatorScreenDestination.itemIdArg])


//    val projectIncubatorUIList: StateFlow<IncubatorProjectState> =
//        itemsRepository.getProject(itemId).map { IncubatorProjectState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorProjectState()
//            )


//    val tempState: StateFlow<IncubatorState> =
//        itemsRepository.getIncubatorTemp(itemId).map { IncubatorState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorState()
//            )
//
//    val dampState: StateFlow<IncubatorState> =
//        itemsRepository.getIncubatorDamp(itemId).map { IncubatorState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorState()
//            )
//
//    //
//    val overState: StateFlow<IncubatorState> =
//        itemsRepository.getIncubatorOver(itemId).map { IncubatorState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorState()
//            )
//
//    val airingState: StateFlow<IncubatorState> =
//        itemsRepository.getIncubatorAiring(itemId).map { IncubatorState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorState()
//            )

    var projectState by mutableStateOf(IncubatorProjectState())
        private set

    var tempStateList by mutableStateOf(IncubatorState())
        private set

    var dampStateList by mutableStateOf(IncubatorState())
        private set

    var overStateList by mutableStateOf(IncubatorState())
        private set
    var airingStateList by mutableStateOf(IncubatorState())
        private set

    var day by mutableIntStateOf(0)
        private set

    init {
        viewModelScope.launch {

            projectState = itemsRepository.getProject(itemId)
                .filterNotNull()
                .first()
                .toIncubatorProjectState()

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


//   fun dateNoww() :Int {
//        var diff: Long = 0
//        val calendar: Calendar = Calendar.getInstance()
//        val dateBefore22: String = projectState.data
//        val dateBefore222: String =
//            (calendar.get(Calendar.DAY_OF_MONTH) + 1).toString() + "." + (calendar.get(
//                Calendar.MONTH
//            ) + 1) + "." + calendar.get(Calendar.YEAR)
//        val myFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
//
//        try {
//            val date1: Date = myFormat.parse(dateBefore22)
//            val date2: Date = myFormat.parse(dateBefore222)
//            diff = date2.time - date1.time
//        } catch (e: ParseException) {
//            throw RuntimeException(e)
//        }
//        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
//    }


    //    val tempState: StateFlow<IncubatorTempState> =
//        itemsRepository.getIncubatorTemp(itemId).map { IncubatorTempState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorTempState()
//            )
//
//    val dampState: StateFlow<IncubatorDampUiState> =
//        itemsRepository.getIncubatorDamp(itemId).map { IncubatorDampUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorDampUiState()
//            )
////
//    val overState: StateFlow<IncubatorOverUiState> =
//        itemsRepository.getIncubatorOver(itemId).map { IncubatorOverUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorOverUiState()
//            )
//
//    val airingState: StateFlow<IncubatorAiringUiState> =
//        itemsRepository.getIncubatorAiring(itemId).map { IncubatorAiringUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorAiringUiState()
//            )
//
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}

fun ProjectTable.toIncubatorProjectState(): IncubatorProjectState = IncubatorProjectState(
    id,
    titleProject,
    type,
    data,
    eggAll,
    eggAllEND,
    airing,
    over,
    arhive,
    dateEnd,
    time1,
    time2,
    time3,
    mode
)


data class IncubatorProjectState(
    val id: Int = 0,
    val titleProject: String = "",
    val type: String = "",
    val data: String = "",
    val eggAll: String = "",
    val eggAllEND: String = "",
    val airing: String = "",
    val over: String = "",
    val arhive: String = "0",
    val dateEnd: String = "",
    val time1: String = "",
    val time2: String = "",
    val time3: String = "",
    val mode: Int = 0
)

fun IncubatorUIList.toIncubatorState(): IncubatorState = IncubatorState(
    id,
    day1,
    day2,
    day3,
    day4,
    day5,
    day6,
    day7,
    day8,
    day9,
    day10,
    day11,
    day12,
    day13,
    day14,
    day15,
    day16,
    day17,
    day18,
    day19,
    day20,
    day21,
    day22,
    day23,
    day24,
    day25,
    day26,
    day27,
    day28,
    day29,
    day30,
    idPT
)


data class IncubatorState(
    val id: Int = 0,
    val day1: String = "",
    val day2: String = "",
    val day3: String = "",
    val day4: String = "",
    val day5: String = "",
    val day6: String = "",
    val day7: String = "",
    val day8: String = "",
    val day9: String = "",
    val day10: String = "",
    val day11: String = "",
    val day12: String = "",
    val day13: String = "",
    val day14: String = "",
    val day15: String = "",
    val day16: String = "",
    val day17: String = "",
    val day18: String = "",
    val day19: String = "",
    val day20: String = "",
    val day21: String = "",
    val day22: String = "",
    val day23: String = "",
    val day24: String = "",
    val day25: String = "",
    val day26: String = "",
    val day27: String = "",
    val day28: String = "",
    val day29: String = "",
    val day30: String = "",
    val idPT: Int = 0
)

data class IncubatorUIList(
    val id: Int,
    val day1: String,
    val day2: String,
    val day3: String,
    val day4: String,
    val day5: String,
    val day6: String,
    val day7: String,
    val day8: String,
    val day9: String,
    val day10: String,
    val day11: String,
    val day12: String,
    val day13: String,
    val day14: String,
    val day15: String,
    val day16: String,
    val day17: String,
    val day18: String,
    val day19: String,
    val day20: String,
    val day21: String,
    val day22: String,
    val day23: String,
    val day24: String,
    val day25: String,
    val day26: String,
    val day27: String,
    val day28: String,
    val day29: String,
    val day30: String,
    val idPT: Int
)


//data class IncubatorState(
//    val titleList: IncubatorUIList = IncubatorUIList(
//        0,
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        "",
//        0
//    )
//)


//1
//data class IncubatorUIList(
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
//
//fun IncubatorTemp.toIncubatorTemp(): IncubatorUIList = IncubatorUIList(
//    day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30
//)
//
//fun IncubatorDamp.toIncubatorDamp(): IncubatorUIList = IncubatorUIList(
//    day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30
//)
//
//fun IncubatorOver.toIncubatorOver(): IncubatorUIList = IncubatorUIList(
//    day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30
//)
//
//fun IncubatorAiring.toIncubatorAiring(): IncubatorUIList = IncubatorUIList(
//    day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14, day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28, day29, day30
//)
//
//конец 1


//data class IncubatorTempState(val titleList:IncubatorTemp = IncubatorTemp(0, "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",0))
//
//data class IncubatorDampUiState(val titleList:IncubatorDamp = IncubatorDamp(0, "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",0))
//
//data class IncubatorOverUiState(val titleList:IncubatorOver = IncubatorOver(0, "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",0))
//
//data class IncubatorAiringUiState(val titleList:IncubatorAiring = IncubatorAiring(0, "","","","","","","","","","","","","","","","","","","","","","","","","","","","","","",0))

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





