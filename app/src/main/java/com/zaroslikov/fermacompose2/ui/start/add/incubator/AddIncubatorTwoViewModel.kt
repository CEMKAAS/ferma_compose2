package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.Incubator
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectListUiState
import com.zaroslikov.fermacompose2.ui.sale.toSaleTableUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddIncubatorTwoViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    private var incubatorArhivList by mutableIntStateOf(0)

    init {
        viewModelScope.launch {

        }
    }

    fun incubatorFromArchive2(type: String): Int {
        viewModelScope.launch {
            incubatorArhivList = itemsRepository.getIncubatorListArh2(type)
                .filterNotNull()
                .first()
                .toInt()
        }
        return incubatorArhivList
    }

//
//    fun incubatorFromArchive(type: String): Int {
//        viewModelScope.launch {
//            incubatorArhivList = itemsRepository.getIncubatorListArh(type).
//        }
//        return incubatorArhivList
//    }


//    fun incubatorFromArchive(type: String): Boolean {
//        val projectBoolean = false
//
//        viewModelScope.launch {
//            val projectList = itemsRepository.getIncubatorListArh(type)
//                .filterNotNull().
//
//        }
//    }
//
//    suspend fun IncubatorFromArchive(type: String): List<ProjectTable> {
//        return sexState = itemsRepository.getIncubatorListArh(type)
//            .filterNotNull()
//    }


    fun incubatorFromArchive(type: String): StateFlow<IncubatorProjectListUiState2> {

        return itemsRepository.getIncubatorListArh(type).map { IncubatorProjectListUiState2(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = IncubatorProjectListUiState2()
            )

    }
//
//    fun incubatorFromArchive3(id: Int): List<Incubator> {
//        return itemsRepository.getIncubatorList(id).map { IncubatorArhListUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//                initialValue = IncubatorArhListUiState()
//            ).value.itemList
//    }
//
//    suspend fun incubatorlist(id: Int):List<Incubator>{
//        return itemsRepository.getIncubatorList2(id)
//    }
//
//    suspend fun saveIncubator2(id: Int, idPT:Int) {
//        val list = itemsRepository.getIncubatorList2(id)
//
//        list.forEach {
//            itemsRepository.insertIncubator(it)
//        }
//    }
//
//    suspend fun saveIncubator3(id: Int) : List<Incubator> {
//        return itemsRepository.getIncubatorList2(id)
//    }
//


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


    suspend fun saveIncubator(list: MutableList<Incubator>) {
        list.forEach {
            itemsRepository.insertIncubator(it)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 1_000L
    }

}

data class IncubatorArhListUiState(val itemList: List<Incubator> = listOf())