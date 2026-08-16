package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseEditScreen

import androidx.compose.ui.graphics.ImageBitmap
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.fermacompose2.base.state.ListState
import com.zaroslikov.fermacompose2.supportFun.dateToday
import com.zaroslikov.fermacompose2.ui.elements.icon.PROJECT_ICONS
import com.zaroslikov.fermacompose2.ui.incubator_project.bookmark.entry.NotificationParameters
import com.zaroslikov.fermacompose2.ui.navigation.UiEvent

data class WarehouseEditState(
    val currentIcon: AppIcon = AppIcon.LIVESTOCK,
    val imagePath: String? = null,
    val nameProject: String = "",
    val dateProject: String = dateToday(),
    val isInsertProject: Boolean = true,
    val currentProject: DomainProjectTable = DomainProjectTable(date = dateToday()),
    val currentSettings: DomainSettings = DomainSettings(),
    val isShowNotification: Boolean = false,

    val notificationList: List<NotificationParameters> = emptyList(),
    val currentNotification: NotificationParameters = NotificationParameters(),
    val indexNotification: Long = 0,

    override val navigate: UiEvent? = null,

    override val idPT: Long = 0,
    override val isLoading: Boolean = false,
    val hasAnyError: Boolean = false,
    val iconList: List<AppIcon> = PROJECT_ICONS
) : ListState()