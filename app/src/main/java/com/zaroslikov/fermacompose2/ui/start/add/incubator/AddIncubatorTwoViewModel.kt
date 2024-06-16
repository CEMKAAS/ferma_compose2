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
import com.zaroslikov.fermacompose2.data.incubator.IncubatorAiring
import com.zaroslikov.fermacompose2.data.incubator.IncubatorDamp
import com.zaroslikov.fermacompose2.data.incubator.IncubatorOver
import com.zaroslikov.fermacompose2.data.incubator.IncubatorTemp
import com.zaroslikov.fermacompose2.ui.sale.SaleTableUiState
import com.zaroslikov.fermacompose2.ui.sale.toSaleTableUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddIncubatorTwoViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

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


}

