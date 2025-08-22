package com.zaroslikov.fermacompose2.data.mapper.AnimaMapper.dto

import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalCard
import com.zaroslikov.fermacompose2.data.dto.animal.AnimalCardDto

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