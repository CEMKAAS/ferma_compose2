package com.zaroslikov.fermacompose2.ui.animal.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class AnimalViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    val itemId: Long = checkNotNull(savedStateHandle[AnimalDestination.itemIdArg])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    @OptIn(ExperimentalCoroutinesApi::class)
    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getAllAnimal(itemId)
            .flatMapLatest { animalList ->
                // Преобразуем каждый animal с добавлением count (note)
                if (animalList.isEmpty()) {
                    flowOf(AnimalUiState(emptyList()))
                } else {
                    val animalFlows = animalList.map { animal ->
                        itemsRepository.getCountAnimalLimit(animal.id)
                            .map { countData ->
                                animal.copy(note = countData.count)
                            }
                    }
                    combine(animalFlows) { animalsWithCount ->
                        AnimalUiState(animalsWithCount.toList())
                    }
                }
            }
            .onStart {
                _isLoading.value = true
            }
            .onEach {
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