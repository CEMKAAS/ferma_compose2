package com.zaroslikov.fermacompose2.ui.project.finance.category

import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.supportFun.firstDayOfMonth
import com.zaroslikov.fermacompose2.supportFun.todayOfMonth
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceCategoryState(
    val isOpenBottomSheetGroup: Boolean = false,
    val isOpenCalendarDialog: Boolean = false,
    val filterDate: FilterDate = FilterDate.ALL_TIME,
    val dateBegin: Pair<String, Long> = firstDayOfMonth(),
    val dateEnd: Pair<String, Long> = todayOfMonth(),
    val currentPeriod: String = "",
    val financeCategory: FinanceCategory = FinanceCategory.SALE,
    val currentBalance: BalanceStructure = BalanceStructure(),
    val currentProduct: Pair<String, Double> = "" to 0.0,
    val suffixPrice: Suffix = Suffix.RUBLE,
    val financeCategoryList: List<CategoryUi> = emptyList(),
    val financeProductList: List<ProductUi> = emptyList(),
    val financeGroupList: List<ProductUi2> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState()

data class ProductUi2(
    val value: Double,
    val suffix: Suffix,
    val price: Double,
    val priceAll: Double?,
    val category: String,
    val status: Boolean? = null,
    val buyer: String? = null,
    val data: String,
    val categoryFinance: FinanceCategory
)

data class BalanceStructure(
    val currentBalance: Double = 0.0,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val ownNeed: Double = 0.0,
    val scrap: Double = 0.0,
    val profit: Double = 0.0
)

data class CategoryUi(
    val category: String,
    val price: Double,
    val percentFloat: Float,
    val percentDouble: Double
)

data class ProductUi(
    val title: String,
    val suffix: Suffix,
    val price: Double,
    val category: FinanceCategory,
    val positive: Boolean,
)
