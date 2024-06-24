package com.zaroslikov.fermacompose2.ui.animal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalVaccinationTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AnimalIndicatorsViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalIndicatorsDestination.itemIdArg])
    val indicators: String =
        checkNotNull(savedStateHandle[AnimalIndicatorsDestination.itemIdArgTwo])


    val indicatorsUiState: StateFlow<AnimalIndicatorsUiState> = when (indicators) {
        "count" -> {
            itemsRepository.getCountAnimal(itemId).map { AnimalIndicatorsUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnimalIndicatorsUiState()
                )
        }

        "size" -> {
            itemsRepository.getSizeAnimal(itemId).map { AnimalIndicatorsUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnimalIndicatorsUiState()
                )
        }

        "weight" -> {
            itemsRepository.getWeightAnimal(itemId).map { AnimalIndicatorsUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnimalIndicatorsUiState()
                )
        }

        else -> {
            itemsRepository.getCountAnimal(itemId).map { AnimalIndicatorsUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = AnimalIndicatorsUiState()
                )
        }
    }


    val vaccinationtUiState: StateFlow<AnimalVaccinationtUiState> =
        itemsRepository.getVaccinationtAnimal(itemId).map { AnimalVaccinationtUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalVaccinationtUiState()
            )


    suspend fun saveItem(animalIndicatorsVM: AnimalIndicatorsVM) {
        when (indicators) {
            "count" -> {
                itemsRepository.insertAnimalCountTable(animalIndicatorsVM.toCount())
            }

            "size" -> {
                itemsRepository.insertAnimalSizeTable(animalIndicatorsVM.toSize())
            }

            "weight" -> {
                itemsRepository.insertAnimalWeightTable(animalIndicatorsVM.toWeight())
            }
            else -> {}
        }
    }

    suspend fun updateItem(animalIndicatorsVM: AnimalIndicatorsVM) {
        when (indicators) {
            "count" -> {
                itemsRepository.updateAnimalCountTable(animalIndicatorsVM.toCount())
            }
            "size" -> {
                itemsRepository.updateAnimalSizeTable(animalIndicatorsVM.toSize())
            }
            "weight" -> {
                itemsRepository.updateAnimalWeightTable(animalIndicatorsVM.toWeight())
            }
            else -> {}
        }
    }


    suspend fun saveVaccinationt(animalVaccinationTable: AnimalVaccinationTable) {
        itemsRepository.insertAnimalVaccinationTable(animalVaccinationTable)
    }

    suspend fun updateVaccinationt(animalVaccinationTable: AnimalVaccinationTable) {
        itemsRepository.updateAnimalVaccinationTable(animalVaccinationTable)
    }


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class AnimalIndicatorsUiState(val itemList: List<AnimalIndicatorsVM> = listOf())

data class AnimalVaccinationtUiState(val itemList: List<AnimalVaccinationTable> = listOf())


data class AnimalIndicatorsVM(
    val id: Int,
    val weight: String,
    val date: String,
    val idAnimal: Int
)

fun AnimalIndicatorsVM.toWeight(): AnimalWeightTable = AnimalWeightTable(
    id = id, weight = weight, date = date, idAnimal = idAnimal
)

fun AnimalIndicatorsVM.toCount(): AnimalCountTable = AnimalCountTable(
    id = id, count = weight, date = date, idAnimal = idAnimal
)

fun AnimalIndicatorsVM.toSize(): AnimalSizeTable = AnimalSizeTable(
    id = id, size = weight, date = date, idAnimal = idAnimal
)