package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.app.AppSettingsTable
import com.zaroslikov.domain.models.table.app.DomainAppSettings


fun DomainAppSettings.toAppSettingsTable(): AppSettingsTable {
    return AppSettingsTable(
        id = this.id,
        lastVersionApp = this.lastVersionApp,
        currentVersionApp = this.currentVersionApp,
        isFirstLaunch = this.isFirstLaunch,
    )
}

fun AppSettingsTable.toDomainAppSettings(): DomainAppSettings {
    return DomainAppSettings(
        id = this.id,
        lastVersionApp = this.lastVersionApp,
        currentVersionApp = this.currentVersionApp,
        isFirstLaunch = this.isFirstLaunch,
    )
}