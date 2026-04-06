package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.domain.models.dto.add.DomainFastAddProduct
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class WarehouseIntent() : BaseIntent {
    data class FastAddClicked(val value: DomainFastAddProduct) : WarehouseIntent()
    data class ShowFastAddClicked(val value: Boolean) : WarehouseIntent()
    data class OpenWarningWriteOffAlterDialogClicked(val id: Long?) : WarehouseIntent()
    data object FoodOnWriteOffChange : WarehouseIntent()
}