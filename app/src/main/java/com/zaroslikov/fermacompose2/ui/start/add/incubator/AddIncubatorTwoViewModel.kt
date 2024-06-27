package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.ferma.IncubatorAiring
import com.zaroslikov.fermacompose2.data.ferma.IncubatorDamp
import com.zaroslikov.fermacompose2.data.ferma.IncubatorOver
import com.zaroslikov.fermacompose2.data.ferma.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectListUiState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddIncubatorTwoViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


//    var incubatorArhivList by mutableStateOf(emptyList())
//        private set
//


    fun incubatorFromArchive(type: String): StateFlow<IncubatorProjectListUiState> {

        return itemsRepository.getIncubatorListArh(type).map { IncubatorProjectListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorProjectListUiState()
            )
    }

//    suspend fun IncubatorFromArchive(type: String): List<ProjectTable> {
//        return sexState = itemsRepository.getIncubatorListArh(type)
//            .filterNotNull()
//    }


    var sexState by mutableIntStateOf(0)
        private set

    suspend fun savaProject(incubatorData: ProjectTable): Int {
        itemsRepository.insertProject(incubatorData)

        sexState = itemsRepository.getLastProject()
            .filterNotNull()
            .first()
            .toInt()

        return sexState
    }

    suspend fun saveIncubator(

        incubatorTemp: IncubatorTemp,
        incubatorDamp: IncubatorDamp,
        incubatorOver: IncubatorOver,
        incubatorAiring: IncubatorAiring
    ) {
        itemsRepository.insertIncubatorTemp(incubatorTemp)
        itemsRepository.insertIncubatorDamp(incubatorDamp)
        itemsRepository.insertIncubatorOver(incubatorOver)
        itemsRepository.insertIncubatorAiring(incubatorAiring)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

