package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.ferma.Incubator
import com.zaroslikov.domain.models.table.DomainIncubator

fun Incubator.toDomainIncubator(): DomainIncubator {
    return DomainIncubator(
        id = this.id,
        day = this.day,
        temp = this.temp,
        damp = this.damp,
        over = this.over,
        airing = this.airing,
        idPT = this.idPT,
    )
}

fun DomainIncubator.toIncubator(): Incubator {
    return Incubator(
        id = this.id,
        airing = this.airing,
        day = this.day,
        damp = this.damp,
        idPT = this.idPT,
        temp = this.temp,
        over = this.over,
    )
}