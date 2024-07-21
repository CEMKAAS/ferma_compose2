package com.zaroslikov.fermacompose2.ui.animal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AnimalCardViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalCardDestination.itemIdArg])


    val animalState: StateFlow<AnimalCardUiState> =
        itemsRepository.getAnimal(itemId).map { AnimalCardUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalCardUiState()
            )

    val countState: StateFlow<AnimalCoutUiStateLimit> =
        itemsRepository.getCountAnimalLimit(itemId).map { AnimalCoutUiStateLimit(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalCoutUiStateLimit()
            )

    val sizeState: StateFlow<AnimalSizeCardUiStateLimit> =
        itemsRepository.getSizeAnimalLimit(itemId).map { AnimalSizeCardUiStateLimit(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalSizeCardUiStateLimit()
            )

    val weightState: StateFlow<AnimalWeightUiStateLimit> =
        itemsRepository.getWeightAnimalLimit(itemId).map { AnimalWeightUiStateLimit(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalWeightUiStateLimit()
            )

    val vaccinationState: StateFlow<AnimalVaccinationCardUiStateLimit> =
        itemsRepository.getVaccinationtAnimalLimit(itemId)
            .map { AnimalVaccinationCardUiStateLimit(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalVaccinationCardUiStateLimit()
            )

    fun productState(name:String): StateFlow<AnimalProductCardUiStateLimit> {
        return itemsRepository.getProductAnimal(name)
            .map { AnimalProductCardUiStateLimit(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalProductCardUiStateLimit()
            )
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}


data class AnimalCardUiState(
    val animalTable: AnimalTable = AnimalTable(
        0,
        "",
        "",
        "",
        true,
        "",
        "",
        "",
        false,
        1
    )
)

data class AnimalCoutUiStateLimit(val itemList: List<AnimalCountTable> = listOf())

data class AnimalSizeCardUiStateLimit(val itemList: List<AnimalSizeTable> = listOf())

data class AnimalWeightUiStateLimit(val itemList: List<AnimalWeightTable> = listOf())

data class AnimalVaccinationCardUiStateLimit(val itemList: List<AnimalVaccinationTable> = listOf())

data class AnimalProductCardUiStateLimit(val itemList: List<AnimalTitSuff> = listOf())

data class AnimalTitSuff(
    val Title: String,
    val priceAll: Double,
    val suffix: String
)