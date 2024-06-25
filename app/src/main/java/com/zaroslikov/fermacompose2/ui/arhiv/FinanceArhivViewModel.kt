package com.zaroslikov.fermacompose2.ui.arhiv

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.ui.animal.AnimalUiState
import com.zaroslikov.fermacompose2.ui.animal.AnimalViewModel
import com.zaroslikov.fermacompose2.ui.finance.FinanceDestination
import com.zaroslikov.fermacompose2.ui.finance.IncomeCategoryUiState
import com.zaroslikov.fermacompose2.ui.finance.IncomeExpensesCategoryUiState
import com.zaroslikov.fermacompose2.ui.incubator.IncubatorProjectEditState
import com.zaroslikov.fermacompose2.ui.incubator.toIncubatorProjectState
import com.zaroslikov.fermacompose2.ui.incubator.toProjectTable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class FinanceArhivViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {
    val itemId: Int = checkNotNull(savedStateHandle[FinanceDestination.itemIdArg])


    var currentBalanceUiState by mutableDoubleStateOf(0.00)
        private set

    var incomeUiState by mutableDoubleStateOf(0.00)
        private set

    var expensesUiState by mutableDoubleStateOf(0.00)
        private set

    var projectState by mutableStateOf(IncubatorProjectEditState())
        private set

    val animalUiState: StateFlow<AnimalUiState> =
        itemsRepository.getAllAnimal(itemId).map { AnimalUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = AnimalUiState()
            )

    init {
        viewModelScope.launch {
            projectState = itemsRepository.getProject(itemId)
                .filterNotNull()
                .first()
                .toIncubatorProjectState()

            currentBalanceUiState = itemsRepository.getCurrentBalance(itemId)
                .filterNotNull()
                .first().toDouble()

            incomeUiState = itemsRepository.getIncome(itemId)
                .filterNotNull()
                .first().toDouble()

            expensesUiState = itemsRepository.getExpenses(itemId)
                .filterNotNull()
                .first().toDouble()
        }
    }

    suspend fun deleteItem() {
        itemsRepository.deleteProject(projectState.toProjectTable())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}