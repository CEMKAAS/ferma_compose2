package com.zaroslikov.data.room.mapper.table


import com.zaroslikov.data.room.table.ferma.SaleTable
import com.zaroslikov.domain.models.DomainSaleTable

fun DomainSaleTable.toRoomMap(): SaleTable = SaleTable(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    price = price,
    priceAll = priceAll,
    priceSuffix = priceSuffix,
    day = day,
    month = month,
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
    priceSuffix = priceSuffix,
    day = day,
    month = month,
    year = year,
    category = category,
    buyer = buyer,
    note = note,
    idPT = idPT,
    animalId = animalId,
    animalCountId = animalCountId
)

