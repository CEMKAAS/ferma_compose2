package com.zaroslikov.fermacompose2.ui.start.add

import androidx.lifecycle.ViewModel
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable

class ProjectAddViewModel(val itemsRepository: ItemsRepository) : ViewModel() {

    suspend fun insertTable(item: ProjectTable) {
        itemsRepository.insertProject(item)
    }


}