package com.zaroslikov.fermacompose2.ui.sections.expenses.list_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val expensesRepository: ExpensesRepository
) : ListViewModel<ExpensesListState, ExpensesListIntent>(ExpensesListState()) {

    val itemIdPT: Long = checkNotNull(savedStateHandle[ExpensesDestination.itemIdArg])

    init {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val addList = expensesRepository.getAllExpensesItems(itemIdPT).first()
            val briefly = expensesRepository.getBrieflyItemExpenses(itemIdPT).first()
            updateState {
                it.copy(
                    idPT = itemIdPT,
                    list = addList,
                    briefly = briefly,
                    isLoading = false
                )
            }
        }
    }

    fun getDetailsName(name: String): Flow<List<DomainExpensesTable>> {
        return expensesRepository.getBrieflyDetailsItemExpenses(itemIdPT, name)
    }
}

sealed class ExpensesListIntent() : BaseIntent