package com.zaroslikov.fermacompose2.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ExpensesAnimalTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.home.CategoryUiState
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExpensesEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    val itemId: Int = checkNotNull(savedStateHandle[ExpensesEntryDestination.itemIdArg])

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleExpensesList(itemId).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategoryExpensesList(itemId).map { CategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    val animalUiState: StateFlow<AnimalExpensesUiState> =
        itemsRepository.getItemsAnimalExpensesList(itemId).map { AnimalExpensesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalExpensesUiState()
            )


    suspend fun saveItem(expensesTable: ExpensesTable, set:MutableMap<Long,Double>) {
        val id = itemsRepository.insertExpenses(expensesTable)


        setExpensesAnimal(set, id, itemId).forEach {
            itemsRepository.insertExpensesAnimal(it)
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

fun setExpensesAnimal(
    set:MutableMap<Long,Double>,
    idExpenses: Long,
    idPT: Int
): MutableList<ExpensesAnimalTable> {

    val list = mutableListOf<ExpensesAnimalTable>()

    if (set.isNotEmpty()) {
        set.forEach {
            list.add(
                ExpensesAnimalTable(
                    id = 0,
                    idExpenses = idExpenses,
                    idAnimal = it.key,
                    percentExpenses = it.value,
                    idPT = idPT.toLong()
                )
            )
        }
    }
    return list
}

data class AnimalExpensesUiState(val animalList: List<AnimalExpensesList> = listOf())

data class AnimalExpensesList(
    val id: Int,
    val name: String,
    val foodDay: Double,
    val countAnimal: Int
)
data class AnimalExpensesList2(
    val id: Int,
    val name: String,
    val foodDay: Double,
    val countAnimal: Int,
    var ps:Boolean
)