package com.zaroslikov.fermacompose2.ui.finance.finace2

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.domain.models.dto.shared.DomainCategoryPrice
import com.zaroslikov.domain.models.dto.shared.DomainTitleSuffixPrice
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.repository.ExpensesRepository
import com.zaroslikov.domain.repository.SaleRepository
import com.zaroslikov.domain.repository.SettingsRepository
import com.zaroslikov.domain.repository.WriteOffRepository
import com.zaroslikov.fermacompose2.base.intent.BaseIntent
import com.zaroslikov.fermacompose2.base.viewModel.ListViewModel
import com.zaroslikov.fermacompose2.supportFun.dateLongToString
import com.zaroslikov.fermacompose2.supportFun.dateLongToStringSQLPair
import com.zaroslikov.fermacompose2.supportFun.datePeriod
import com.zaroslikov.fermacompose2.ui.dateBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinanceCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saleRepository: SaleRepository,
    private val expensesRepository: ExpensesRepository,
    private val writeOffRepository: WriteOffRepository,
    private val settingsRepository: SettingsRepository
) : ListViewModel<FinanceCategoryState, FinanceCategoryIntent>(FinanceCategoryState()) {

    private val itemId: Long =
        checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArg])
    private val financeCategory: FinanceCategory =
        FinanceCategory.valueOf(checkNotNull(savedStateHandle[FinanceIncomeExpensesDestination.itemIdArgTwo]))

    init {
        loadData()
    }

    fun onIntent(intent: FinanceCategoryIntent) {
        when (intent) {
            is FinanceCategoryIntent.OpenBottomSheetGroup -> openBottomSheetGroup(intent.titleProduct)
            is FinanceCategoryIntent.FilterDateClicked -> updateFilterDate(intent.value)
            is FinanceCategoryIntent.CurrentPeriodClicked -> updateCurrentPeriod(intent.value)
            is FinanceCategoryIntent.OpenCalendarDialogClicked -> updateOpenDialogCalendar(intent.value)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            val (start, end) = datePeriod(
                getState().filterDate,
                getState().dateBegin.first, getState().dateEnd.first
            )
            val currentBalanceFlow: Flow<BalanceStructure> = when (financeCategory) {
                FinanceCategory.SALE -> saleRepository.getIncomeMount(itemId, start, end)
                    .map { income -> BalanceStructure(currentBalance = income) }

                FinanceCategory.EXPENSES -> expensesRepository.getExpensesMount(itemId, start, end)
                    .map { expenses ->
                        BalanceStructure(currentBalance = expenses)
                    }

                FinanceCategory.OWN_NEED -> writeOffRepository.getOwnNeedMonth(itemId, start, end)
                    .map { ownNeed -> BalanceStructure(currentBalance = ownNeed) }

                FinanceCategory.SCRAP -> writeOffRepository.getScrapMonth(itemId, start, end)
                    .map { scrap ->
                        BalanceStructure(currentBalance = scrap)
                    }

                FinanceCategory.PROFIT -> {
                    combine(
                        saleRepository.getIncomeMount(itemId, start, end),
                        writeOffRepository.getOwnNeedMonth(itemId, start, end),
                        expensesRepository.getExpensesMount(itemId, start, end),
                        writeOffRepository.getScrapMonth(itemId, start, end),
                    ) { income, ownNeed, expenses, scrap ->
                        BalanceStructure(
                            currentBalance = income - expenses/*ownNeed - expenses - scrap*/,
                            income = income,
                            ownNeed = ownNeed,
                            expenses = expenses,
                            scrap = scrap,
                            profit = income + ownNeed - expenses - scrap
                        )
                    }
                }
            }

            val financeCategoryFlow = when (financeCategory) {
                FinanceCategory.SALE -> saleRepository.getCategoryIncomeCurrentMonth(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.EXPENSES -> expensesRepository.getCategoryExpensesCurrentMonth(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.OWN_NEED -> writeOffRepository.getOwnNeedCategoryPeriodList(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.SCRAP -> writeOffRepository.getScrapCategoryPeriodList(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.PROFIT -> flowOf(emptyList())
            }

            val financeProductFlow = when (financeCategory) {
                FinanceCategory.SALE -> saleRepository.getProductListCategoryIncomeCurrentMonth(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.EXPENSES -> expensesRepository.getProductLisCategoryExpensesCurrentMonth(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.OWN_NEED -> writeOffRepository.getOwnNeedProductPeriodList(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.SCRAP -> writeOffRepository.getScrapProductPeriodList(
                    itemId,
                    start,
                    end
                )

                FinanceCategory.PROFIT -> flowOf(emptyList())
            }

            combine(
                currentBalanceFlow,
                financeCategoryFlow,
                financeProductFlow,
                settingsRepository.getSettings(itemId)
            ) { currentBalance, categoryList, productList, set ->
                val financeCategoryUiList = reduceCategories(categoryList)
                val financeProductUiList = reduceProduct(productList)
                Triple(
                    currentBalance,
                    financeCategoryUiList,
                    financeProductUiList
                ) to set
            }.collect { (finance, set) ->
                updateState { state ->
                    state.copy(
                        financeCategory = financeCategory,
                        isLoading = false,
                        currentBalance = finance.first,
                        financeCategoryList = finance.second,
                        financeProductList = finance.third,
                        suffixPrice = set.currencySuffix
                    )
                }
            }
        }
    }

    private fun reduceCategories(list: List<DomainCategoryPrice>): List<CategoryUi> {
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

    private fun reduceProduct(list: List<DomainTitleSuffixPrice>): List<ProductUi> {
        return list.map { item ->
            val positive = when (item.category) {
                FinanceCategory.SALE, FinanceCategory.OWN_NEED -> true
                FinanceCategory.EXPENSES, FinanceCategory.SCRAP, FinanceCategory.PROFIT -> false
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

    private fun openBottomSheetGroup(titleProduct: Pair<String, Double>?) {
        viewModelScope.launch {
            if (titleProduct != null) {
                val listBriefly = listProduct(itemIdPT = itemId, titleProduct = titleProduct.first)
                updateState {
                    it.copy(
                        isOpenBottomSheetGroup = true,
                        financeGroupList = listBriefly,
                        currentProduct = titleProduct
                    )
                }
            } else updateState {
                it.copy(isOpenBottomSheetGroup = false)
            }
        }
    }

    private fun updateCurrentPeriod(currentPeriod: Pair<Long?, Long?>) {
        val dateBegin =
            currentPeriod.first?.let { dateLongToStringSQLPair(it) } ?: getState().dateBegin
        val dateEnd =
            currentPeriod.second?.let { dateLongToStringSQLPair(it) } ?: getState().dateEnd
        updateState {
            it.copy(
                dateBegin = dateBegin,
                dateEnd = dateEnd,
                currentPeriod = "${dateLongToString(dateBegin.second)} - ${dateLongToString(dateEnd.second)}"
            )
        }
        start(filterDate = FilterDate.PERIOD)
    }

    private fun updateFilterDate(filterDate: FilterDate) {
        if (filterDate == FilterDate.PERIOD) updateOpenDialogCalendar(true)
        else start(filterDate)
    }

    private fun start(filterDate: FilterDate) {
        updateState { it.copy(filterDate = filterDate) }
        loadData()
    }

    private fun updateOpenDialogCalendar(isOpenDialogCalendar: Boolean) {
        updateState { it.copy(isOpenCalendarDialog = isOpenDialogCalendar) }
    }

    private suspend fun listProduct(
        itemIdPT: Long,
        titleProduct: String
    ): List<ProductUi2> {
        return when (financeCategory) {
            FinanceCategory.SALE -> getDetailsSaleByName(titleProduct)
            FinanceCategory.EXPENSES -> getDetailsFinanceByName(titleProduct)
            FinanceCategory.OWN_NEED -> getDetailsWriteOffOwnNeedByName(titleProduct)
            FinanceCategory.SCRAP -> getDetailsScrapByName(titleProduct)
            FinanceCategory.PROFIT -> emptyList()
        }
    }

    private suspend fun getDetailsSaleByName(name: String): List<ProductUi2> {
        val list = saleRepository.getBrieflyDetailsItemSale(itemId, name).first()
        return list.map { item ->
            val date = dateBuilder(item.day, item.month, item.year)
            ProductUi2(
                value = item.count,
                suffix = item.countSuffix,
                price = item.price,
                priceAll = item.priceAll,
                category = item.category,
                buyer = item.buyer,
                data = date,
                categoryFinance = financeCategory
            )
        }
    }

    private suspend fun getDetailsFinanceByName(name: String): List<ProductUi2> {
        val list = expensesRepository.getBrieflyDetailsItemExpenses(itemId, name).first()
        return list.map { item ->
            val date = dateBuilder(item.day, item.month, item.year)
            ProductUi2(
                value = item.count,
                suffix = item.countSuffix,
                price = item.price,
                priceAll = item.priceAll,
                category = item.category,
                data = date,
                categoryFinance = financeCategory
            )
        }
    }

    private suspend fun getDetailsWriteOffOwnNeedByName(name: String): List<ProductUi2> {
        val list = writeOffRepository.getBrieflyDetailsItemWriteOffOwnNeed(itemId, name).first()
        return list.map { item ->
            val date = dateBuilder(item.day, item.month, item.year)
            ProductUi2(
                value = item.count,
                suffix = item.countSuffix,
                price = item.price ?: 0.0,
                priceAll = item.priceAll,
                category = item.category,
                data = date,
                categoryFinance = financeCategory
            )
        }
    }

    private suspend fun getDetailsScrapByName(name: String): List<ProductUi2> {
        val list = writeOffRepository.getBrieflyDetailsItemWriteOffScrap(itemId, name).first()
        return list.map { item ->
            val date = dateBuilder(item.day, item.month, item.year)
            ProductUi2(
                value = item.count,
                suffix = item.countSuffix,
                price = item.price ?: 0.0,
                priceAll = item.priceAll,
                category = item.category,
                data = date,
                categoryFinance = financeCategory
            )
        }
    }
}

sealed class FinanceCategoryIntent() : BaseIntent {
    data class OpenBottomSheetGroup(val titleProduct: Pair<String, Double>? = null) :
        FinanceCategoryIntent()

    data class FilterDateClicked(val value: FilterDate) : FinanceCategoryIntent()
    data class OpenCalendarDialogClicked(val value: Boolean) : FinanceCategoryIntent()
    data class CurrentPeriodClicked(val value: Pair<Long?, Long?>) : FinanceCategoryIntent()
}