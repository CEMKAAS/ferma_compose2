package com.zaroslikov.fermacompose2.ui.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StartScreenViewModel(private val fermaRepository: ItemsRepository) : ViewModel() {

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