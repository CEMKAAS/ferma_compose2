package com.zaroslikov.data.room.mapper.AnimaMapper.dto

import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalWithCount
import com.zaroslikov.fermacompose2.data.dto.animal.AnimalWithCountDto

fun DomainAnimalWithCount.toRoom(): AnimalWithCountDto = AnimalWithCountDto(
    id = id,
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    group = group,
    sex = sex,
    count = count,
    suffix = suffix
)

fun AnimalWithCountDto.toDomain(): DomainAnimalWithCount = DomainAnimalWithCount(
    id = id,
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    group = group,
    sex = sex,
    count = count,
    suffix = suffix
)