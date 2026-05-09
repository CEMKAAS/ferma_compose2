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
    priceSuffix = priceSuffix,
    category = category,
    productOrigin = productOrigin,
    day = day,
    month = month,
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
    priceSuffix = priceSuffix,
    category = category,
    productOrigin = productOrigin,
    day = day,
    month = month,
    year = year,
    status = status,
    note = note,
    idPT = idPT,
    animalCountId = animalCountId,
)
