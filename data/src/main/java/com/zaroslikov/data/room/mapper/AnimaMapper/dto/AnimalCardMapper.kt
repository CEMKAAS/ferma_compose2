package com.zaroslikov.data.room.mapper.AnimaMapper.dto

import com.zaroslikov.data.room.dto.AnimalCardDto
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalCard

fun AnimalCardDto.toDomainMap(): DomainAnimalCard = DomainAnimalCard(
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    group = group,
    sex = sex,
    note = note,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    price = price,
    countAnimal = countAnimal,
    countAnimalSuffix = countAnimalSuffix,
    size = size,
    sizeSuffix = sizeSuffix,
    weight = weight,
    weightSuffix = weightSuffix,
    vaccination = vaccination,
    vaccinationDate = vaccinationDate
)

fun DomainAnimalCard.toDomainMap(): AnimalCardDto = AnimalCardDto(
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    group = group,
    sex = sex,
    note = note,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    price = price,
    countAnimal = countAnimal,
    countAnimalSuffix = countAnimalSuffix,
    size = size,
    sizeSuffix = sizeSuffix,
    weight = weight,
    weightSuffix = weightSuffix,
    vaccination = vaccination,
    vaccinationDate = vaccinationDate
)