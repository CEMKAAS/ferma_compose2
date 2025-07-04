package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainSaleTable
import com.zaroslikov.fermacompose2.data.ferma.SaleTable
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber
import kotlin.Long

fun DomainSaleTable.toRoomMap(): SaleTable = SaleTable(
    id = id,
    title = title.trim(),
    count = count.toConvertDbDouble(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.toConvertDbDouble(),
    suffix = suffix,
    category = category.trim(),
    buyer = buyer.trim(),
    note = note.trim(),
    idPT = idPT,
    animalCountId = animalCountId
)

fun SaleTable.toDomainMap(): DomainSaleTable = DomainSaleTable(
    id = id,
    title = title,
    count = count.formatNumber(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.formatNumber(),
    suffix = suffix,
    category = category,
    buyer = buyer,
    note = note,
    idPT = idPT,
    animalCountId = animalCountId
)

