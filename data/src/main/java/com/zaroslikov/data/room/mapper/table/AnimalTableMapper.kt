package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalTable
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable

fun AnimalTable.toDomainMap(): DomainAnimalTable = DomainAnimalTable(
    id = id,
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    group = isGroup,
    sex = sex,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    idPT = idPT
)

fun DomainAnimalTable.toRoomMap(): AnimalTable = AnimalTable(
    id = id,
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    isGroup = group,
    sex = sex,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    idPT = idPT
)