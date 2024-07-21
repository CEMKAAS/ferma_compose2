package com.zaroslikov.fermacompose2.ui.start.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProjectAddViewModel(val itemsRepository: ItemsRepository) : ViewModel() {

   private var countProject by mutableIntStateOf(0)

    fun countProject(): Int {
        viewModelScope.launch {
            countProject = itemsRepository.getCountRowProject()
                .filterNotNull()
                .first()
                .toInt()
        }
        return countProject
    }


    suspend fun insertTable(item: ProjectTable) {
        itemsRepository.insertProject(item)
    }


}