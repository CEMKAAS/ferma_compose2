package com.zaroslikov.fermacompose2.ui.start.add.incubator

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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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

    fun updateUiState(itemDetails: IncubatorProjectEditState) {
        incubatorUiState =
            itemDetails
    }

    suspend fun saveProject(list: MutableList<Incubator>) {

        val idPT = itemsRepository.insertProjectLong(incubatorUiState.toProjectTable())

//        val idPT = itemsRepository.getLastProject()
//            .filterNotNull()
//            .first()
//            .toInt()
//
        setIdPT(list, idPT).forEach {
            itemsRepository.insertIncubator(it)
        }

    }


    suspend fun saveIncubator(list: MutableList<Incubator>) {
        list.forEach {
            itemsRepository.insertIncubator(it)
        }
    }


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


}