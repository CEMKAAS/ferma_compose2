package com.zaroslikov.data.room.mapper.dto.trace

import com.zaroslikov.data.room.dto.trace.TraceProductionDto
import com.zaroslikov.domain.models.dto.trace.DomainTraceProduction

fun TraceProductionDto.toDomainMap(): DomainTraceProduction = DomainTraceProduction(
    id = id,
    count = count,
    countSuffix = countSuffix,
    day = day,
    month = month,
    year = year,
    price = price,
    priceSuffix = priceSuffix,
    animalId = animalId,
    animalName = animalName,
    animalType = animalType,
    animalBreed = animalBreed,
    animalIsGroup = animalIsGroup,
    animalSex = animalSex,
    animalHerdId = animalHerdId,
    animalIcon = animalIcon,
    animalImagePath = animalImagePath,
    note = note
)
