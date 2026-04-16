package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.fermacompose2.base.intent.BaseIntent

sealed class WarehouseEditIntent() : BaseIntent {
    data class NameProjectChanged(val value: String) : WarehouseEditIntent()
    data class IconClicked(val value: Int) : WarehouseEditIntent()
    data class ImagePathClicked(val value: String?) : WarehouseEditIntent()
    data class DateClicked(val value: String) : WarehouseEditIntent()
    data class CurrencyClicked(val value: Suffix) : WarehouseEditIntent()
    data class WeightClicked(val value: Suffix) : WarehouseEditIntent()
    data class VolumeClicked(val value: Suffix) : WarehouseEditIntent()
    data class LinearClicked(val value: Suffix) : WarehouseEditIntent()

    //Notification
    data class ShowNotificationClicked(val value: Boolean) : WarehouseEditIntent()
    data class TimeNotificationChanged(val value: String) : WarehouseEditIntent()
    data class NoteNotificationChanged(val value: String) : WarehouseEditIntent()
    data class ChoiceNotificationClicked(val index: Long) : WarehouseEditIntent()
    data class RemoveNotificationClicked(val index: Long) : WarehouseEditIntent()
    data object CancelNotificationClicked : WarehouseEditIntent()
    data object AddNotificationClicked : WarehouseEditIntent()
    data object EditNotificationClicked : WarehouseEditIntent()

    data object InsertClicked : WarehouseEditIntent()
    data object EditClicked : WarehouseEditIntent()
}