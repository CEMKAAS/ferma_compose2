package com.zaroslikov.data.room.mapper.AnimaMapper.dto

import com.zaroslikov.data.room.dto.AnimalWithCountDto
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalWithCount


fun DomainAnimalWithCount.toRoom(): AnimalWithCountDto = AnimalWithCountDto(
    id = id,
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    isGroup = group,
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
    group = isGroup,
    sex = sex,
    count = count,
    suffix = suffix
)