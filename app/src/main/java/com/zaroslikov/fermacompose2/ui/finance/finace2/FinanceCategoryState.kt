package com.zaroslikov.fermacompose2.ui.finance.finace2


import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class FinanceCategoryState(
    val financeCategory: FinanceCategory = FinanceCategory.SALE,
    val currentBalance: Double = 0.0,
    val financeCategoryList: List<CategoryUi> = emptyList(),
    val financeProductList: List<ProductUi> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState()

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
    val category: FinanceCategory ,
    val positive: Boolean,
)
