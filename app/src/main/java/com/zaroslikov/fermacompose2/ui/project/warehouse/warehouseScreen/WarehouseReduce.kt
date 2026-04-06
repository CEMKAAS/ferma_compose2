package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.fermacompose2.base.reduce.BaseReducer

class WarehouseReduce : BaseReducer<WarehouseState, WarehouseIntent>() {
    override fun reducer(
        state: WarehouseState,
        intent: WarehouseIntent
    ): WarehouseState {
        return when (intent) {
            is WarehouseIntent.OpenWarningWriteOffAlterDialogClicked ->
                state.updateOpenWarningWriteOffAlterDialog(intent.id)

            is WarehouseIntent.ShowFastAddClicked ->
                state.updateShowFastAdd(intent.value)

            else -> state
        }
    }

    private fun WarehouseState.updateShowFastAdd(isShowFastAddProduct: Boolean): WarehouseState {
        return copy(
            isShowFastAddProduct = isShowFastAddProduct
        )
    }

    private fun WarehouseState.updateOpenWarningWriteOffAlterDialog(
        id: Long?
    ): WarehouseState {
        if (id == null)
            return copy(
                isOpenWarningWriteOffAlterDialog = false,
                currentFoodOnWriteOff = null
            )
        else {
            val currentFoodOnWriteOff = foodList.find { ui -> ui.id == id }
            return copy(
                currentFoodOnWriteOff = currentFoodOnWriteOff,
                isOpenWarningWriteOffAlterDialog = currentFoodOnWriteOff?.let { true } ?: false
            )
        }
    }
}