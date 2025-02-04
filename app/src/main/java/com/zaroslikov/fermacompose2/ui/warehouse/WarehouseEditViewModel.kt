package com.zaroslikov.fermacompose2.ui.warehouse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorScreenDestination
import com.zaroslikov.fermacompose2.ui.incubator.toIncubatorProjectState
import com.zaroslikov.fermacompose2.ui.incubator.toProjectTable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WarehouseEditViewModel(
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