package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.home.AddTableUiState
import com.zaroslikov.fermacompose2.ui.home.toAddTableUiState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.incubator.toProjectTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AddIncubatorViewModel(
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val format = SimpleDateFormat("dd.MM.yyyy")
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val formattedDate: String = format.format(calendar.timeInMillis)

    var incubatorUiState by mutableStateOf(
        IncubatorProjectEditState(
            id = 0,
            titleProject = "Мое Хозяйство",
            type = "Курицы",
            data = formattedDate,
            eggAll = "0",
            eggAllEND = "0",
            airing = false,
            over = false,
            arhive = "0",
            dateEnd = "",
            time1 = "08:00",
            time2 = "12:00",
            time3 = "18:00",
            mode = 0
        )
    )
        private set

    var list = mutableStateOf(emptyList<Incubator>())


    fun updateUiState(itemDetails: IncubatorProjectEditState) {
        incubatorUiState =
            itemDetails
    }

    suspend fun saveProject(
//        list: MutableList<Incubator>
    ) {

        val idPT = itemsRepository.insertProjectLong(incubatorUiState.toProjectTable())

//        val idPT = itemsRepository.getLastProject()
//            .filterNotNull()
//            .first()
//            .toInt()
//
        setIdPT(items.value.toMutableList(), idPT).forEach {
            itemsRepository.insertIncubator(it)
        }

    }

//    suspend fun incubatorFromArchive2(type: String): Int {
//        return itemsRepository.getIncubatorListArh2(type)
//            .filterNotNull()
//            .first()
//            .toInt()
//    }


    fun incubatorFromArchive(type: String): StateFlow<IncubatorProjectListUiState2> {
        return itemsRepository.getIncubatorListArh(type).map { IncubatorProjectListUiState2(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorProjectListUiState2()
            )
    }

    fun incubatorFromArchive3(idPT: Int): StateFlow<IncubatorArhListUiState> {
        return itemsRepository.getIncubatorListArh3(idPT).map { IncubatorArhListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorArhListUiState()
            )
    }

    private val _items = mutableStateOf<List<Incubator>>(
        setAutoIncubator(
            setIncubator(incubatorUiState.type),
            incubatorUiState.airing,
            incubatorUiState.over
        )
    )
    val items: State<List<Incubator>> = _items


    fun incubatorFromArchive4(idPT: Int) {
        viewModelScope.launch {
            _items.value = itemsRepository.getIncubatorListArh4(idPT)
        }

    }
//
//
//    fun updateiArchive4(item: List<Incubator>) {
//        _items.value = item
//    }

//    private var countIncubator by mutableIntStateOf(0)
//
//    fun countProject(): Int {
//        viewModelScope.launch {
//            countIncubator = itemsRepository.getCountRowIncubator()
//                .filterNotNull()
//                .first()
//                .toInt()
//        }
//        return countIncubator
//    }


    companion object {
        private const val TIMEOUT_MILLIS = 1_000L
    }

}