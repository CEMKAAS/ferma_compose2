package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.Alarm.AlarmRepository
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.water.Reminder
import com.zaroslikov.fermacompose2.data.water.WaterRepository
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.incubator.toProjectTable
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class AddIncubatorViewModel(
    private val itemsRepository: ItemsRepository,
    private val waterRepository: WaterRepository,
    private val alarmRepository: AlarmRepository
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


    fun updateUiState(itemDetails: IncubatorProjectEditState) {
        incubatorUiState =
            itemDetails
    }

    fun saveProject(list: MutableList<Incubator>) {
        viewModelScope.launch {
            val idPT = itemsRepository.insertProjectLong(incubatorUiState.toProjectTable())
            setIdPT(list, idPT).forEach {
                itemsRepository.insertIncubator(it)
            }
        }
    }

    private val _items = mutableStateOf<List<Incubator>>(emptyList())
    val items: State<List<Incubator>> = _items

    fun incubatorFromArchive4(idPT: Int) {
        viewModelScope.launch {
            _items.value = itemsRepository.getIncubatorListArh4(idPT)
        }
    }

    private val _items2 = mutableStateOf<List<ProjectTable>>(emptyList())
    val items2: State<List<ProjectTable>> = _items2

    fun incubatorFromArchive5(type: String) {
        viewModelScope.launch {
            _items2.value = itemsRepository.getIncubatorListArh6(type)
        }
    }

    fun scheduleReminder(string: String) {
        waterRepository.scheduleReminder(string)
    }

    fun scheduleReminder2(string: String) {
        alarmRepository.setDailyAlarm(string)
    }


    fun scheduleReminder3(string: String) {
        alarmRepository.setDailyAlarm(string)
    }
}
