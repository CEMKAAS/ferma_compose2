package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.incubator.Incubator
import com.zaroslikov.domain.models.table.DomainIncubator

fun Incubator.toDomainIncubator(): DomainIncubator {
    return DomainIncubator(
        id = this.id,
        day = this.day,
        temp = this.temp,
        damp = this.damp,
        over = this.over,
        airing = this.airing,
        tempFact = this.tempFact,
        dampFact = this.dampFact,
        overFact = this.overFact,
        airingFact = this.airingFact,
        note = this.note,
        idPT = this.idPT,
    )
}

fun DomainIncubator.toIncubator(): Incubator {
    return Incubator(
        id = this.id,
        day = this.day,
        temp = this.temp,
        damp = this.damp,
        over = this.over,
        airing = this.airing,
        tempFact = this.tempFact,
        dampFact = this.dampFact,
        overFact = this.overFact,
        airingFact = this.airingFact,
        note = this.note,
        idPT = this.idPT
    )
}