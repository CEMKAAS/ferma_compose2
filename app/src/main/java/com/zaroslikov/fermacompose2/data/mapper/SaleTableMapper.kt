package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable

fun SaleTable.toDomainMap(): DomainSaleTable = DomainSaleTable(
    id = id,
    title = title,
    count = count.toString(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.toString(),
    idPT = idPT,
    suffix = suffix,
    category = category,
    buyer = buyer,
    note = note
)

fun DomainSaleTable.toRoomMap(): SaleTable = SaleTable(
    id = id,
    title = title,
    count = count.replace(Regex("[^\\d.]"), "").replace(",", ".").toDouble(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.replace(Regex("[^\\d.]"), "").replace(",", ".").toDouble(),
    suffix = suffix,
    category = category,
    buyer = buyer,
    note = note,
    idPT = idPT
)
