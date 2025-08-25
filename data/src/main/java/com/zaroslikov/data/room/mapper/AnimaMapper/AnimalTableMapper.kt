package com.zaroslikov.data.room.mapper.AnimaMapper

import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.data.room.table.animal.AnimalTable

fun AnimalTable.toDomainMap(): DomainAnimalTable = DomainAnimalTable(
    id = id,
    name = name,
    type = type,
    date = date,
    dateFactory = dateFactory,
    group = group,
    sex = sex,
    note = note,
    image = image,
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
    group = group,
    sex = sex,
    note = note,
    image = image,
    archive = archive,
    foodDay = foodDay,
    foodDaySuffix = foodDaySuffix,
    idPT = idPT
)