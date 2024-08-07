package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    fun updateUiState(item: AnimalEditUiState) {
        animaEditUiState =
            item
    }

    val typeUiState: StateFlow<TitleUiState> =
        itemsRepository.getTypeAnimal(itemId).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )
    suspend fun saveItem() {
        itemsRepository.updateAnimalTable(animaEditUiState.toAnimalTable())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteAnimalTable(animaEditUiState.toAnimalTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class AnimalEditUiState(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val data: String = "",
    val groop: Boolean = true,
    val sex: String = "",
    val note: String = "",
    val image: String = "",
    val arhiv: Boolean = false,
    val idPT: Int = 0,
)

fun AnimalTable.toAnimaEditUiState(
): AnimalEditUiState = AnimalEditUiState(
    id, name, type, data, groop, sex, note, image, arhiv, idPT
)

fun AnimalEditUiState.toAnimalTable(): AnimalTable = AnimalTable(
    id, name, type, data, groop, sex, note, image, arhiv, idPT
)

