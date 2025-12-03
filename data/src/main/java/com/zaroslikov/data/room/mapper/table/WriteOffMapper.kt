package com.zaroslikov.data.room.mapper.table


import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.domain.models.table.DomainWriteOffTable

fun DomainWriteOffTable.toRoomMap(): WriteOffTable = WriteOffTable(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    price = price,
    priceAll = priceAll,
    category = category,
    day = day,
    mount = month,
    year = year,
    status = status,
    note = note,
    idPT = idPT,
    animalCountId = animalCountId
)

fun WriteOffTable.toDomainMap(): DomainWriteOffTable = DomainWriteOffTable(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    price = price,
    priceAll = priceAll,
    category = category,
    day = day,
    month = mount,
    year = year,
    status = status,
    note = note,
    idPT = idPT,
    animalCountId = animalCountId
)
