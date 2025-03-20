package com.zaroslikov.fermacompose2.ui.animal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AnimalEntryViewModel (
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[AnimalEntryDestination.itemIdArg])

    val typeUiState: StateFlow< DataStringListState> =
        itemsRepository.getTypeAnimal(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue =  DataStringListState()
            )



    suspend fun saveItem(animalTable: AnimalTable, animalCountTable: AnimalCountTable, animalWeightTable: AnimalWeightTable, animalSizeTable: AnimalSizeTable) {
        val id = itemsRepository.insertAnimalTable(animalTable)

        itemsRepository.insertAnimalCountTable(animalCountTable.copy(idAnimal = id.toInt()))
        itemsRepository.insertAnimalWeightTable(animalWeightTable.copy(idAnimal = id.toInt()))
        itemsRepository.insertAnimalSizeTable(animalSizeTable.copy(idAnimal = id.toInt()))

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

