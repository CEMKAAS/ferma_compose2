package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.animal.AnimalHerdTable
import com.zaroslikov.domain.models.table.DomainAnimalHerd

fun AnimalHerdTable.toDomain(): DomainAnimalHerd = DomainAnimalHerd(
    id = id,
    name = name,
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    idPT = idPT
)

fun DomainAnimalHerd.toRoom(): AnimalHerdTable = AnimalHerdTable(
    id = id,
    name = name.trim(),
    note = note,
    imagePath = imagePath,
    currentIcon = currentIcon,
    idPT = idPT
)
