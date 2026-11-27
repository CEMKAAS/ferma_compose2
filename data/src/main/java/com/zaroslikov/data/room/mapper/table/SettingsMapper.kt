package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.project.SettingsTable
import com.zaroslikov.domain.models.table.DomainSettings
import org.json.JSONArray

fun SettingsTable.toDomainSettings(): DomainSettings {
    return DomainSettings(
        id = this.id,
        currencySuffix = this.currencySuffix,
        weightSuffix = this.weightSuffix,
        volumeSuffix = this.volumeSuffix,
        linearSuffix = this.linearSuffix,
        idPT = this.idPT,
        warehouseCardCodes = warehouseCardCodesJson?.let { json ->
            val codes = JSONArray(json)
            List(codes.length(), codes::getString)
        },
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
        warehouseCardCodesJson = warehouseCardCodes?.toWarehouseCardCodesJson(),
    )
}

internal fun List<String>.toWarehouseCardCodesJson(): String = JSONArray(this).toString()
