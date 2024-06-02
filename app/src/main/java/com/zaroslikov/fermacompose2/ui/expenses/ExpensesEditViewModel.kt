package com.zaroslikov.fermacompose2.ui.expenses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.home.CategoryUiState
import com.zaroslikov.fermacompose2.ui.home.TitleUiState
import com.zaroslikov.fermacompose2.ui.sale.toSaleTableUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpensesEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(savedStateHandle[ExpensesEditDestination.itemIdArg])
    private val itemIdPT: Int = checkNotNull(savedStateHandle[ExpensesEditDestination.itemIdArgTwo])

    var itemUiState by mutableStateOf(ExpensesTableUiState())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemsRepository.getItemExpenses(itemId)
                .filterNotNull()
                .first()
                .toExpensesTableUiState()
        }
    }

    fun updateUiState(itemDetails: ExpensesTableUiState) {
        itemUiState =
            itemDetails
    }

    val titleUiState: StateFlow<TitleUiState> =
        itemsRepository.getItemsTitleExpensesList(itemIdPT).map { TitleUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TitleUiState()
            )


    val categoryUiState: StateFlow<CategoryUiState> =
        itemsRepository.getItemsCategoryExpensesList(itemIdPT).map { CategoryUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CategoryUiState()
            )

    suspend fun saveItem() {
        itemsRepository.updateExpenses(itemUiState.toExpensesTable())
    }

    suspend fun deleteItem() {
        itemsRepository.deleteExpenses(itemUiState.toExpensesTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class ExpensesTableUiState(
    val id: Int = 0,
    val title: String = "", // название
    val count: String = "", // Кол-во
    val day: Int = 0,  // день
    val mount: Int = 0, // месяц
    val year: Int = 0, // время
    val priceAll: String = "",
    val idPT: Int = 0,
    var suffix: String = "",
    var category: String = "",
)

fun ExpensesTable.toExpensesTableUiState(): ExpensesTableUiState = ExpensesTableUiState(
    id, title, count.toString(), day, mount, year, priceAll, idPT, suffix, category
)

fun ExpensesTableUiState.toExpensesTable(): ExpensesTable = ExpensesTable(
    id = id,
    title = title,
    count = count.toDouble(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll,
    suffix = suffix,
    category = category,
    idPT = idPT
)

