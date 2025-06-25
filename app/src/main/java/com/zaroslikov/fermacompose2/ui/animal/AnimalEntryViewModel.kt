package com.zaroslikov.fermacompose2.ui.animal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalSizeTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.animal.AnimalWeightTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.supportFun.DataStringListState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimalEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Long = checkNotNull(savedStateHandle[AnimalEntryDestination.itemIdArg])

    val typeUiState: StateFlow<DataStringListState> =
        itemsRepository.getTypeAnimal(itemId).map { DataStringListState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DataStringListState()
            )


  fun saveItem(
        animalTable: AnimalTable,
        animalCountTable: AnimalCountTable,
        expensesTable: ExpensesTable?
    ) {
        viewModelScope.launch {
            val id = itemsRepository.insertAnimalTable(animalTable)
            itemsRepository.insertAnimalCountTable(animalCountTable.copy(idAnimal = id.toInt()))
            if (expensesTable != null)
                itemsRepository.insertExpenses(expensesTable.copy(animalId = id))
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

