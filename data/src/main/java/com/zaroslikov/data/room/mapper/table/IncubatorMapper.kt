package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.incubator.IncubatorParameters
import com.zaroslikov.domain.models.table.DomainIncubatorParameters

fun IncubatorParameters.toDomainIncubatorParameters(): DomainIncubatorParameters {
    return DomainIncubatorParameters(
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

fun DomainIncubatorParameters.toIncubatorParameters(): IncubatorParameters {
    return IncubatorParameters(
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