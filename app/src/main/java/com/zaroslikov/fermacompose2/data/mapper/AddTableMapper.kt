package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable

fun DomainAddTable.toRoomMap(): AddTable = AddTable(
    id = id,
    title = title.trim(),
    count = count,
    day = day,
    mount = month,
    year = year,
    price = price,
    countSuffix = countSuffix,
    category = category,
    animalId = animalId,
    note = note,
    idPT = idPT,
)

fun AddTable.toDomainMap(): DomainAddTable = DomainAddTable(
    id = id,
    title = title,
    count = count,
    day = day,
    month = mount,
    year = year,
    price = price,
    countSuffix = countSuffix,
    category = category,
    animalId = animalId,
    note = note,
    idPT = idPT
)