package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.incubator.IncubatorTable
import com.zaroslikov.domain.models.table.DomainIncubatorTable

fun IncubatorTable.toDomainIncubatorTable(): DomainIncubatorTable {
    return DomainIncubatorTable(
        isAutoRotation = this.isAutoRotation,
        price = this.price,
        id = this.id,
        isAutoVentilation = this.isAutoVentilation,
        capacity = this.capacity,
        note = this.note,
        currencySuffix = this.currencySuffix,
        idPT = this.idPT,
        brand = this.brand,
        model = this.model
    )
}

fun DomainIncubatorTable.toIncubatorTable(): IncubatorTable {
    return IncubatorTable(
        isAutoRotation = this.isAutoRotation,
        price = this.price,
        id = this.id,
        isAutoVentilation = this.isAutoVentilation,
        capacity = this.capacity,
        note = this.note,
        currencySuffix = this.currencySuffix,
        idPT = this.idPT,
        brand = this.brand,
        model = this.model
    )
}