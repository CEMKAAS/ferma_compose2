package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainWriteOffTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable

fun DomainWriteOffTable.toRoomMap(): WriteOffTable = WriteOffTable(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    price = price,
    priceAll = priceAll,
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
    day = day,
    month = mount,
    year = year,
    status = status,
    note = note,
    idPT = idPT,
    animalCountId = animalCountId
)
