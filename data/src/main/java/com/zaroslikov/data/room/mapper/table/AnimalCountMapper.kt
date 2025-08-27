package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalCountTable
import com.zaroslikov.domain.models.table.DomainAnimalCount


fun AnimalCountTable.toDomainMap(): DomainAnimalCount = DomainAnimalCount(
    id = id,
    count = count,
    suffix = suffix,
    date = date,
    note = note,
    version = version,
    idAnimal = idAnimal
)

fun DomainAnimalCount.toRoomMap(): AnimalCountTable = AnimalCountTable(
    id = id,
    count = count,
    suffix = suffix,
    date = date,
    note = note,
    version = version,
    idAnimal = idAnimal
)
