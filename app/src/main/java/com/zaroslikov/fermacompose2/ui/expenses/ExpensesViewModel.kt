package com.zaroslikov.fermacompose2.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.data.ferma.ExpensesTable
import com.zaroslikov.fermacompose2.ui.home.HomeDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExpensesViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {


    val itemId: Int = checkNotNull(savedStateHandle[ExpensesDestination.itemIdArg])


    val homeUiState: StateFlow<ExpensesUiState> =
        itemsRepository.getAllExpensesItems(itemId).map { ExpensesUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ExpensesUiState()
            )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * Ui State for HomeScreen
 */
data class ExpensesUiState(val itemList: List<ExpensesTable> = listOf())