package com.zaroslikov.fermacompose2.ui.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.sale.SaleTableUiState
import com.zaroslikov.fermacompose2.ui.sale.toSaleTable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IncubatorProjectEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[IncubatorScreenDestination.itemIdArg])

    var projectState by mutableStateOf(IncubatorProjectState())
        private set

    init {
        viewModelScope.launch {
            projectState = itemsRepository.getProject(itemId)
                .filterNotNull()
                .first()
                .toIncubatorProjectState()
        }
    }

    fun updateUiState(itemDetails: IncubatorProjectState) {
        projectState =
            itemDetails
    }

    suspend fun saveItem() {
        itemsRepository.updateProject(projectState.toProjectTable())
    }
}

fun IncubatorProjectState.toProjectTable(): ProjectTable = ProjectTable(
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