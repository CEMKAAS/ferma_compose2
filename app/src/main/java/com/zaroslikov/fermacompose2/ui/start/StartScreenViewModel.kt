package com.zaroslikov.fermacompose2.ui.start

import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.water.ProjectTableStartScreen
import com.zaroslikov.fermacompose2.data.water.WaterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class StartScreenViewModel(
    private val fermaRepository: ItemsRepository,
    private val waterRepository: WaterRepository
) : ViewModel() {

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    val getAllProject: StateFlow<List<ProjectTableStartScreen>> =
        fermaRepository.getAllProject().map { projectList ->
            projectList.map { project ->
                project.toProjectWithImage()
            }
        }.onStart {
            // Устанавливаем состояние загрузки перед началом загрузки данных
            _isLoading.value = true
        }.onEach {
            // Отключаем состояние загрузки после завершения загрузки данных
            _isLoading.value = false
        }
            .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private suspend fun ProjectTable.toProjectWithImage(): ProjectTableStartScreen =
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

            ProjectTableStartScreen(
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