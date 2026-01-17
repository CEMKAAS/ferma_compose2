package com.zaroslikov.fermacompose2.ui.project.finance.arhive.category//package com.zaroslikov.fermacompose2.ui.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
) : ListViewModel<FinanceCategoryState, FinanceCategoryIntent>(FinanceCategoryState()) {

    val itemId: Long = checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArg])
    val itemCategory: String =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgTwo])
    val itemBoolean: Boolean =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgThree])
    val dateBegin: String =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgFour])
    val dateEnd: String =
        checkNotNull(savedStateHandle[FinanceCategoryDestination.itemIdArgFive])

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = false) }
            /*val financeCategory = when {
                itemBoolean -> {
                    saleRepository.getProductListCategoryIncomeCurrentMonth(
                        itemId,
                        dateBegin, dateEnd,
                        itemCategory
                    ).first()
                }

                *//*itemCategory == " Мои Животные " -> {
                    itemsRepository.getProductLisCategoryExpensesAnimalCurrentMonth(
                        itemId,
                        dateBegin, dateEnd
                    )
                }*//*

                else -> expensesRepository.getProductLisCategoryExpensesCurrentMonth(
                    itemId,
                    dateBegin, dateEnd,
                    itemCategory
                ).first()
            }
            updateState { it.copy(financeCategory = financeCategory) }*/
        }
    }
}

sealed class FinanceCategoryIntent : BaseIntent

