package com.zaroslikov.fermacompose2.ui.start

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.water.WaterRepository
import com.zaroslikov.fermacompose2.ui.home.AddTableUiState
import com.zaroslikov.fermacompose2.ui.home.toAddTableUiState
import com.zaroslikov.fermacompose2.ui.incubator.toProjectTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    val getAllProjectAct: StateFlow<StartUiState> =
        fermaRepository.getAllProjectAct().map { StartUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = StartUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class StartUiState(val projectList: List<ProjectTable> = listOf())