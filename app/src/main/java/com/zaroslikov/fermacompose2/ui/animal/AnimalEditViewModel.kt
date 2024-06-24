package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.ui.expenses.ExpensesTableUiState
import com.zaroslikov.fermacompose2.ui.expenses.toExpensesTableUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AnimalEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalEditDestination.itemIdArg])
    var animaEditUiState by mutableStateOf(AnimalEditUiState())
        private set


    init {
        viewModelScope.launch {
            animaEditUiState = itemsRepository.getAnimal(itemId)
                .filterNotNull()
                .first()
                .toAnimaEditUiState()
        }
    }

}

data class AnimalEditUiState(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val data: String = "",
    val groop: Boolean = true,
    val count: String = "",
    val sex: String = "",
    val note: String = "",
    val image: String = "",
    val arhiv: Boolean = false,
    val idPT: Int = 0,
)

fun AnimalTable.toAnimaEditUiState(
): AnimalEditUiState = AnimalEditUiState(
    id, name, type, data, groop, count, sex, note, image, arhiv, idPT
)


