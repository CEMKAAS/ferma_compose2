package com.zaroslikov.fermacompose2.ui.add


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.data.room.table.ferma.ProjectTable
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectAddViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private var countProject by mutableIntStateOf(0)

    fun countProject(): Int {
//        viewModelScope.launch {
//            countProject = itemsRepository.getCountRowProject()
//                .filterNotNull()
//                .first()
//                .toInt()
//        }
        return countProject
    }

    suspend fun insertTable(item: DomainProjectTable) {
        projectRepository.insertProject(item)
    }

}