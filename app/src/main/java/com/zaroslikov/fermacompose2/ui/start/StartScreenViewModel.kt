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
            if (time != ""){
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
                    project.toProjectWithImage() }
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private suspend fun ProjectTable.toProjectWithImage(): ProjectTable2 = withContext(Dispatchers.IO) {
        val imageBitmap = imageData.let {
            BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap()
        }
        ProjectTable2( id,titleProject,type,data,eggAll,eggAllEND,airing,over,arhive,dateEnd,time1,time2,time3,mode,imageBitmap)
    }

    fun convertByteArrayToBitmap(imageData: ByteArray): ImageBitmap {
        val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
        return bitmap.asImageBitmap()
    }
}
data class StartUiState(val projectList: List<ProjectTable> = listOf())
data class StartUiState2(val projectList: List<ProjectTable2> = listOf())