package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.data.water.WaterRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IncubatorProjectEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val waterRepository: WaterRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[IncubatorScreenDestination.itemIdArg])

    var projectState by mutableStateOf(IncubatorProjectEditState())
        private set

    val name = projectState.titleProject

    init {
        viewModelScope.launch {
            projectState = itemsRepository.getProject(itemId)
                .filterNotNull()
                .first()
                .toIncubatorProjectState()
        }
    }

    fun updateUiState(itemDetails: IncubatorProjectEditState) {
        projectState =
            itemDetails
    }

    suspend fun saveItem(count:Int) {
        itemsRepository.updateProject(projectState.toProjectTable())

        projectState =  when (count) {
            0 -> projectState.copy(time1 = "", time2 = "", time3 = "")
            1 -> projectState.copy(time2 = "", time3 = "")
            2 -> projectState.copy(time3 = "")
            else -> {projectState}
        }

        waterRepository.cancelAllNotifications(name)
        waterRepository.scheduleReminder(listOf(projectState.time1, projectState.time2,projectState.time3), projectState.titleProject)
    }

    suspend fun deleteItem() {
        waterRepository.cancelAllNotifications(projectState.titleProject)
        itemsRepository.deleteProject(projectState.toProjectTable())
    }
}

fun IncubatorProjectEditState.toProjectTable(): ProjectTable = ProjectTable(
    id,
    titleProject,
    type,
    data,
    eggAll,
    eggAllEND,
    airing.toString(),
    over.toString(),
    arhive,
    dateEnd,
    time1,
    time2,
    time3,
    mode,
    imageData
)

fun ProjectTable.toIncubatorProjectState(): IncubatorProjectEditState = IncubatorProjectEditState(
    id,
    titleProject,
    type,
    data,
    eggAll,
    eggAllEND,
    airing.toBoolean(),
    over.toBoolean(),
    arhive,
    dateEnd,
    time1,
    time2,
    time3,
    mode,
    imageData
)


data class IncubatorProjectEditState(
    val id: Int = 0,
    val titleProject: String = "",
    val type: String = "",
    val data: String = "",
    val eggAll: String = "",
    val eggAllEND: String = "",
    val airing: Boolean = false,
    val over: Boolean = false,
    val arhive: String = "0",
    val dateEnd: String = "",
    val time1: String = "",
    val time2: String = "",
    val time3: String = "",
    val mode: Int = 0,
    val imageData: ByteArray? = byteArrayOf()
)