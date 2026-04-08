package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.project.SettingsTable
import com.zaroslikov.domain.models.table.DomainSettings

fun SettingsTable.toDomainSettings(): DomainSettings {
    return DomainSettings(
        id = this.id,
        currencySuffix = this.currencySuffix,
        weightSuffix = this.weightSuffix,
        volumeSuffix = this.volumeSuffix,
        linearSuffix = this.linearSuffix,
        idPT = this.idPT,
    )
}

fun DomainSettings.toSettingsTable(): SettingsTable {
    return SettingsTable(
        id = this.id,
        currencySuffix = this.currencySuffix,
        linearSuffix = this.linearSuffix,
        weightSuffix = this.weightSuffix,
        idPT = this.idPT,
        volumeSuffix = this.volumeSuffix,
    )
}