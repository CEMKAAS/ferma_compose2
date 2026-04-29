package com.zaroslikov.data.room.mapper

import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.domain.models.DomainAddTable

fun DomainAddTable.toAddRoomMap(): AddTable = AddTable(
    id = id,
    title = title.trim(),
    count = count,
    day = day,
    mount = month,
    year = year,
    price = price,
    countSuffix = countSuffix,
    priceSuffix = priceSuffix,
    category = category,
    animalId = animalId,
    note = note,
    idPT = idPT,
    animalCountId = animalCountId

)

fun AddTable.toAddDomainMap(): DomainAddTable = DomainAddTable(
    id = id,
    title = title,
    count = count,
    day = day,
    month = mount,
    year = year,
    price = price,
    countSuffix = countSuffix,
    priceSuffix = priceSuffix,
    category = category,
    animalId = animalId,
    note = note,
    idPT = idPT,
    animalCountId = animalCountId
)