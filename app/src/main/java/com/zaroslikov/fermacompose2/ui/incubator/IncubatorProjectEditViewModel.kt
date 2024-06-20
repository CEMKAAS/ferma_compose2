package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IncubatorProjectEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[IncubatorScreenDestination.itemIdArg])

    var projectState by mutableStateOf(IncubatorProjectEditState())
        private set

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

    suspend fun saveItem() {
        itemsRepository.updateProject(projectState.toProjectTable())
    }

    suspend fun deleteItem() {
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
    airing,
    over,
    arhive,
    dateEnd,
    time1,
    time2,
    time3,
    mode
)

fun ProjectTable.toIncubatorProjectState(): IncubatorProjectEditState = IncubatorProjectEditState(
    id,
    titleProject,
    type,
    data,
    eggAll,
    eggAllEND,
    airing,
    over,
    arhive,
    dateEnd,
    time1,
    time2,
    time3,
    mode
)


data class IncubatorProjectEditState(
    val id: Int = 0,
    val titleProject: String = "",
    val type: String = "",
    val data: String = "",
    val eggAll: String = "",
    val eggAllEND: String = "",
    val airing: String = "",
    val over: String = "",
    val arhive: String = "0",
    val dateEnd: String = "",
    val time1: String = "",
    val time2: String = "",
    val time3: String = "",
    val mode: Int = 0
)