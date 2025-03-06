package com.zaroslikov.fermacompose2.ui.animal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class AnimalViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    val itemId: Int = checkNotNull(savedStateHandle[AnimalDestination.itemIdArg])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getAllAnimal(itemId).map { AnimalUiState(it) }.onStart {
            // Устанавливаем состояние загрузки перед началом загрузки данных
            _isLoading.value = true
        }.onEach {
            // Отключаем состояние загрузки после завершения загрузки данных
            _isLoading.value = false
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class AnimalUiState(val itemList: List<AnimalTable> = listOf())