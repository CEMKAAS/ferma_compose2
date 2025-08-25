package com.zaroslikov.data.room.mapper.AnimaMapper

import com.zaroslikov.fermacompose2.Domain.models.DomainAnimalTable.DomainAnimalCount
import com.zaroslikov.data.room.table.animal.AnimalCountTable


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