package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.home.AddViewModel
import com.zaroslikov.fermacompose2.ui.home.HomeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class IncubatorViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[IncubatorScreenDestination.itemIdArg])

    var homeUiState: StateFlow<IncubatorProjectState> =
        itemsRepository.getProject(itemId).map { IncubatorProjectState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorProjectState()
            )

    val projectListAct: StateFlow<IncubatorProjectListUiState> =
        itemsRepository.getProjectListAct().map { IncubatorProjectListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorProjectListUiState()
            )

    val incubatorUiState: StateFlow<IncubatorListUiState> =
        itemsRepository.getIncubatorList(itemId).map { IncubatorListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorListUiState()
            )


    var itemUiState by mutableStateOf(IncubatorProjectEditState())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getProject(itemId)
                .filterNotNull()
                .first()
                .toIncubatorProjectState()
        }
    }

    fun updateUiState(itemDetails: IncubatorProjectEditState) {
        itemUiState =
            itemDetails
    }

    suspend fun saveItem() {
        itemsRepository.updateProject(itemUiState.toProjectTable())
    }

    suspend fun saveProject(animalTable: AnimalTable, count: String) {
        val id = itemsRepository.insertAnimalTable(animalTable)
        itemsRepository.insertAnimalCountTable(
            AnimalCountTable(
                count = count,
                date = animalTable.data,
                idAnimal = id.toInt()
            )
        )
    }

    suspend fun saveNewProjectArh(project: ProjectTable) {
        val format = SimpleDateFormat("dd.MM.yyyy")
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val dateEnd: String = format.format(calendar.timeInMillis)
        itemsRepository.updateProject(project.copy(arhive = "1", dateEnd = dateEnd))
    }


    suspend fun saveNewProject(animalTable: AnimalTable, count: String) {

        val idPT = itemsRepository.insertProjectLong(itemUiState.toProjectTable())

        val id = itemsRepository.insertAnimalTable(animalTable.copy(idPT = idPT.toInt()))

        itemsRepository.insertAnimalCountTable(
            AnimalCountTable(
                count = count,
                date = animalTable.data,
                idAnimal = id.toInt()
            )
        )
    }

    suspend fun deleteItem() {
        itemsRepository.deleteProject(itemUiState.toProjectTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}


data class IncubatorProjectListUiState(val itemList: List<ProjectTable> = listOf())

data class IncubatorListUiState(val itemList: List<Incubator> = listOf())

data class IncubatorProjectState(
    val project: ProjectTable = ProjectTable(0, "", "", "", "", "", "", "", "", "", "", "", "", 0)
)





