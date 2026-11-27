package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class WarehouseState(
    val isOpenWarningWriteOffAlterDialog: Boolean = false,
    val currentFoodOnWriteOff: FoodListUi? = null,
    val productList: List<WarehouseItem> = emptyList(),
    val foodList: List<FoodListUi> = emptyList(),
    val expensesList: List<WarehouseItem> = emptyList(),
    val settings: DomainSettings = DomainSettings(),
    val feedSummary: WarehouseFeedSummary = WarehouseFeedSummary(),
    val pendingCardCodes: List<String>? = null,
    val isShowAllProduct: Boolean = false,
    val isShowAllExpenses: Boolean = false,
    override val idPT: Long = 0,
    override val isLoading: Boolean = true,
    override val navigate: UiEvent? = null,
    val isArchive: Boolean = false
) : ListState() {
    internal val visibleLayout: WarehouseCardLayout
        get() = WarehouseCardLayout.fromCodes(pendingCardCodes ?: settings.warehouseCardCodes)
}

data class FoodListUi(
    val id: Long,
    val title: String,
    val daysEnd: Int?,
    val weightAll: Double,
    val weightSuffix: Suffix,
    val price: Double,
    val percentFloat: Float,
)

internal data class LoadDataWarehouse(
    val productList: List<WarehouseItem>,
    val feed: WarehouseFeedSnapshot,
    val expensesList: List<WarehouseItem>,
    val settings: DomainSettings
)

data class WarehouseItem(
    val title: String,
    val count: Double,
    val suffix: Suffix,
    val countOfWarehouse: CountOfWarehouse
)

enum class CountOfWarehouse {
    Have, Zero, Minus
}
