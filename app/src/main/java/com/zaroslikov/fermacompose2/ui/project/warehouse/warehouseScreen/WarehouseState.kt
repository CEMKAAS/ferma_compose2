package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class WarehouseState(
    val isOpenWarningWriteOffAlterDialog: Boolean = false,
    val currentFoodOnWriteOff: FoodListUi? = null,
    val isShowFastAddProduct: Boolean = false,
    val productList: List<DomainTitleCountSuffix> = emptyList(),
    val foodList: List<FoodListUi> = emptyList(),
    val expensesList: List<DomainTitleCountSuffix> = emptyList(),
    val fastAddList: List<DomainFastAddProduct> = emptyList(),
    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    override val navigate: UiEvent? = null
) : ListState()

data class FoodListUi(
    val id: Long,
    val title: String,
    val daysEnd: Int,
    val weightAll: Double,
    val weightSuffix: Suffix,
    val price: Double,
    val percentFloat: Float,
)

data class LoadDataWarehouse(
    val productList: List<DomainTitleCountSuffix>,
    val foodList: List<FoodListUi>,
    val expensesList: List<DomainTitleCountSuffix>,
    val fastAddList: List<DomainFastAddProduct>
)