package com.zaroslikov.fermacompose2.ui.finance.finace2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.FinanceRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository
) : ListViewModel<FinanceCategoryState, FinanceCategoryIntent>(FinanceCategoryState()) {

    private val itemId: Long =
        checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArg])
    private val financeCategory: FinanceCategory =
        FinanceCategory.valueOf(checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArgTwo]))

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val currentBalance = when (financeCategory) {
                FinanceCategory.SALE -> saleRepository.getIncome(itemId).first()
                FinanceCategory.EXPENSES -> expensesRepository.getExpenses(itemId).first()
                FinanceCategory.OWN_NEED -> writeOffRepository.getOwnNeed(itemId).first()
                FinanceCategory.SCRAP -> writeOffRepository.getScrap(itemId).first()
            }

            val financeCategoryList =
                when (financeCategory) {
                    FinanceCategory.SALE -> saleRepository.getIncomeCategoryAllList(itemId).first()
                    FinanceCategory.EXPENSES -> expensesRepository.getExpensesCategoryAllList(itemId)
                        .first()

                    FinanceCategory.OWN_NEED -> writeOffRepository.getOwnNeedAllCategoryAllList(
                        itemId
                    ).first()

                    FinanceCategory.SCRAP -> writeOffRepository.getScrapAllCategoryAllList(itemId)
                        .first()
                }

            val financeProductList =
                when (financeCategory) {
                    FinanceCategory.SALE -> saleRepository.getIncomeAllList(itemId).first()
                    FinanceCategory.EXPENSES -> expensesRepository.getExpensesAllList(itemId)
                        .first()

                    FinanceCategory.OWN_NEED -> writeOffRepository.getOwnNeedAllList(itemId).first()
                    FinanceCategory.SCRAP -> writeOffRepository.getScrapAllList(itemId).first()
                }
            val financeCategoryUiList = reduceCategories(financeCategoryList)
            val financeProductUiList = reduceProduct(financeProductList)
            updateState { state ->
                state.copy(
                    financeCategory = financeCategory,
                    isLoading = false,
                    currentBalance = currentBalance,
                    financeCategoryList = financeCategoryUiList,
                    financeProductList = financeProductUiList
                )
            }

        }
    }

    fun reduceCategories(list: List<DomainCategoryPrice>): List<CategoryUi> {
        val total = list.sumOf { it.price }

        return list.map { item ->
            val percent = if (total == 0.0) 0.0 else (item.price / total)

            CategoryUi(
                category = item.category,
                price = item.price,
                percentFloat = percent.toFloat(),
                percentDouble = percent * 100.0

            )
        }
    }

    fun reduceProduct(list: List<DomainTitleSuffixPrice>): List<ProductUi> {

        return list.map { item ->
            val positive = when (item.category) {
                FinanceCategory.SALE, FinanceCategory.OWN_NEED -> true
                FinanceCategory.EXPENSES, FinanceCategory.SCRAP -> false
            }

            ProductUi(
                category = item.category,
                price = item.price,
                title = item.title,
                suffix = item.suffix,
                positive = positive
            )
        }
    }

}


sealed class FinanceCategoryIntent() : BaseIntent