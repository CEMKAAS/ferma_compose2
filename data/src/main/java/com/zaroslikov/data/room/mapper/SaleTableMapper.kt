package com.zaroslikov.data.room.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.data.room.table.ferma.SaleTable

fun DomainSaleTable.toRoomMap(): SaleTable = SaleTable(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    price = price,
    priceAll = priceAll,
    day = day,
    mount = month,
    year = year,
    category = category,
    buyer = buyer,
    note = note,
    idPT = idPT,
    animalId = animalId,
    animalCountId = animalCountId
)

fun SaleTable.toDomainMap(): DomainSaleTable = DomainSaleTable(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    price = price,
    priceAll = priceAll,
    day = day,
    month = mount,
    year = year,
    category = category,
    buyer = buyer,
    note = note,
    idPT = idPT,
    animalId = animalId,
    animalCountId = animalCountId
)

