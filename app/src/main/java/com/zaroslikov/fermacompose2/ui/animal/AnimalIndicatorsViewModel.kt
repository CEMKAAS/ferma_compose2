package com.zaroslikov.fermacompose2.ui.animal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.Domain.models.DomainIndicatorsVM
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.mapper.toCountRoomMap
import com.zaroslikov.fermacompose2.data.mapper.toDomainMap
import com.zaroslikov.fermacompose2.data.mapper.toSizeRoomMap
import com.zaroslikov.fermacompose2.data.mapper.toVaccinationRoomMap
import com.zaroslikov.fermacompose2.data.mapper.toWeightRoomMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class AnimalIndicatorsViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalIndicatorsDestination.itemIdArg])
    val indicators: Int =
        checkNotNull(savedStateHandle[AnimalIndicatorsDestination.itemIdArgTwo])

    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    var animalUiState by mutableStateOf<DomainIndicatorsVM?>(null)
        private set

    fun updateUiState(animalIndicatorsVM: DomainIndicatorsVM) {
        animalUiState =
            animalIndicatorsVM
    }

    val indicatorsUiState: StateFlow<AnimalIndicatorsUiState> = when (indicators) {

        3 -> itemsRepository.getVaccinationAnimal(itemId).map { list ->
            val mappedList = list.map { item -> item.toDomainMap() }
            AnimalIndicatorsUiState(mappedList)
        }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AnimalIndicatorsUiState()
        )

        2 -> itemsRepository.getCountAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalIndicatorsUiState()
            )

        1 -> itemsRepository.getSizeAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalIndicatorsUiState()
            )

        0 -> itemsRepository.getWeightAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalIndicatorsUiState()
            )

        else -> itemsRepository.getCountAnimal(itemId).map { AnimalIndicatorsUiState(it) }.onStart {
            _isLoading.value = true
        }.onEach {
            _isLoading.value = false
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalIndicatorsUiState()
            )

    }

    suspend fun saveItem(animalIndicatorsVM: DomainIndicatorsVM) {

        when (indicators) {
            3 -> itemsRepository.insertAnimalVaccinationTable(animalIndicatorsVM.toVaccinationRoomMap())
            2 -> itemsRepository.insertAnimalCountTable(animalIndicatorsVM.toCountRoomMap())
            1 -> itemsRepository.insertAnimalSizeTable(animalIndicatorsVM.toSizeRoomMap())
            0 -> itemsRepository.insertAnimalWeightTable(animalIndicatorsVM.toWeightRoomMap())
        }
    }

    suspend fun updateItem(animalIndicatorsVM: DomainIndicatorsVM) {
        when (indicators) {

            3 -> itemsRepository.updateAnimalVaccinationTable(animalIndicatorsVM.toVaccinationRoomMap())
            2 -> itemsRepository.updateAnimalCountTable(animalIndicatorsVM.toCountRoomMap())
            1 -> itemsRepository.updateAnimalSizeTable(animalIndicatorsVM.toSizeRoomMap())
            0 -> itemsRepository.updateAnimalWeightTable(animalIndicatorsVM.toWeightRoomMap())

            else -> {}
        }
    }

    suspend fun deleteItem(animalIndicatorsVM: DomainIndicatorsVM) {
        when (indicators) {

            3 -> itemsRepository.deleteAnimalVaccinationTable(animalIndicatorsVM.toVaccinationRoomMap())
            2 -> itemsRepository.deleteAnimalCountTable(animalIndicatorsVM.toCountRoomMap())
            1 -> itemsRepository.deleteAnimalSizeTable(animalIndicatorsVM.toSizeRoomMap())
            0 -> itemsRepository.deleteAnimalWeightTable(animalIndicatorsVM.toWeightRoomMap())

            else -> {}
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class AnimalIndicatorsUiState(val itemList: List<DomainIndicatorsVM> = listOf())



