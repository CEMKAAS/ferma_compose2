package com.zaroslikov.fermacompose2.ui.animal.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.animal.AnimalCountTable
import com.zaroslikov.fermacompose2.data.animal.AnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class AnimalEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val itemIdPT: Long = checkNotNull(savedStateHandle[AnimalEntryDestination.itemIdPT])
    private val itemId: Long = checkNotNull(savedStateHandle[AnimalEntryDestination.itemIdArg])
    val isEntry: Boolean = itemId == (-1).toLong()

    var animalUiState by mutableStateOf(
        AnimalEntryState().copy(
            isEntry = isEntry,
            countSuffix = resourceProvider.getString(R.string.suffix_pieces),
            foodDaySuffix = resourceProvider.getString(R.string.suffix_kilogram)
        )
    )
        private set

    init {
        viewModelScope.launch {
            if (!isEntry) {

            }
            val typeList = itemsRepository.getTypeAnimal(itemIdPT).first()

        }
    }

    fun updateUiState(state: AnimalEntryState) {
        animalUiState =
            state
    }

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

}

