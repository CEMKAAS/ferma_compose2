package com.zaroslikov.fermacompose2.ui.project.finance.arhive.income_expenses

import androidx.lifecycle.SavedStateHandle
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FinanceIncomeExpensesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val financeRepository: FinanceRepository,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository
) : ListViewModel<FinanceIncomeExpensesState, FinanceIncomeExpensesIntent>(
    FinanceIncomeExpensesState()
) {
    /*private val itemId: Long =
        checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArg])
    private val isIncome: Boolean =
        checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArgTwo])

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val financeCategoryList =
                if (isIncome)
                    saleRepository.getIncomeCategoryAllList(itemId).first()
                else
                    expensesRepository.getExpensesCategoryAllList(itemId).first()

            val financeProductList =
                if (isIncome)
                    saleRepository.getIncomeAllList(itemId).first()
                else
                    expensesRepository.getExpensesAllList(itemId).first()

            updateState { state ->
                state.copy(
                    isIncome = isIncome,
                    isLoading = false,
                    financeCategoryList = financeCategoryList,
                    financeProductList = financeProductList
                )
            }

        }
    }*/
//    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//    val month = calendar[Calendar.MONTH] + 1
//    val year = calendar[Calendar.YEAR]


    /* val aminalExpensesUIState: StateFlow<FinanceCategoryState> =
         expensesRepository.getExpensesAnimalAllList(itemId)
             .map { FinanceCategoryState(it) }
             .stateIn(
                 scope = viewModelScope,
                 started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                 initialValue = FinanceCategoryState()
             )*/


}

sealed class FinanceIncomeExpensesIntent : BaseIntent