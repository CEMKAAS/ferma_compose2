package com.zaroslikov.fermacompose2.ui.start

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.water.ProjectTable2
import com.zaroslikov.fermacompose2.data.water.WaterRepository
import com.zaroslikov.fermacompose2.ui.home.AddTableUiState
import com.zaroslikov.fermacompose2.ui.home.toAddTableUiState
import com.zaroslikov.fermacompose2.ui.incubator.toProjectTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class StartScreenViewModel(
    private val fermaRepository: ItemsRepository,
    private val waterRepository: WaterRepository
) : ViewModel() {

    var time by mutableStateOf("")

    init {
        viewModelScope.launch {
            time = waterRepository.getTimeReminder()
        }
    }

    fun onUpdate(time1: String) {
        time = time1
    }

    fun saveItem() {
        viewModelScope.launch {
            waterRepository.cancelAllNotifications("7bc20e66-fc56-4002-ac33-4cc15dd28213")
            waterRepository.setTimeReminder(time)
            if (time != "") {
                waterRepository.setupDailyReminder()
            }
        }
    }

    val getAllProjectArh: StateFlow<StartUiState> =
        fermaRepository.getAllProjectArh().map { StartUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = StartUiState()
            )

    val getAllProjectAct: StateFlow<List<ProjectTable2>> =
        fermaRepository.getAllProjectAct().map { projectList ->
            projectList.map { project ->
                project.toProjectWithImage()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private suspend fun ProjectTable.toProjectWithImage(): ProjectTable2 =
        withContext(Dispatchers.IO) {
            
            val imageBitmap = imageData.let {
                BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
            }

            val data = if (mode == 0) {
                val calendar: Calendar = Calendar.getInstance()
                val dateBefore22: String = data
                val dateBefore222: String =
                    (calendar.get(Calendar.DAY_OF_MONTH)).toString() + "." + (calendar.get(
                        Calendar.MONTH
                    ) + 1) + "." + calendar.get(Calendar.YEAR)
                val myFormat = SimpleDateFormat("dd.MM.yyyy")
                val date1: Date = myFormat.parse(dateBefore22)
                val date2: Date = myFormat.parse(dateBefore222)
                val diff = date2.time - date1.time
                "Идет ${TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1} день"
            } else data

            ProjectTable2(
                id,
                titleProject,
                data,
                arhive,
                dateEnd,
                mode,
                imageBitmap
            )
        }
}

data class StartUiState(val projectList: List<ProjectTable> = listOf())
