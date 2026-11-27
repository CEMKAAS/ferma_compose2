package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class WarehouseIntent() : BaseIntent {
    internal data class ChangeCards(val layout: WarehouseCardLayout) : WarehouseIntent()
    data class ShowAllProductClicked(val value: Boolean) : WarehouseIntent()
    data class ShowAllExpensesClicked(val value: Boolean) : WarehouseIntent()
    data class OpenWarningWriteOffAlterDialogClicked(val id: Long?) : WarehouseIntent()
    data object FoodOnWriteOffChange : WarehouseIntent()
}
