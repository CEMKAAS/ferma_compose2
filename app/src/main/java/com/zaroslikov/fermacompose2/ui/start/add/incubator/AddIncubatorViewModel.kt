package com.zaroslikov.fermacompose2.ui.start.add.incubator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.ui.home.AddTableUiState
import com.zaroslikov.fermacompose2.ui.home.toAddTableUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddIncubatorViewModel(
    private val itemsRepository: ItemsRepository
) : ViewModel() {
    var countIncubator by mutableIntStateOf(1)
        private set
    init {
        viewModelScope.launch {
            countIncubator = itemsRepository.getCountRowProject()
                .filterNotNull()
                .first()
                .toInt()
        }
    }


}