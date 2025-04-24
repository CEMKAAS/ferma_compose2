package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainWritOffTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.supportFun.toConvertDb

fun WriteOffTable.toDomainMap(): DomainWritOffTable = DomainWritOffTable(
    id = id,
    title = title,
    count = count.toString(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.toString(),
    idPT = idPT,
    suffix = suffix,
    status = status,
    note = note
)


fun DomainWritOffTable.toRoomMap(): WriteOffTable = WriteOffTable(
    id = id,
    title = title,
    count = count.toConvertDb().toDouble(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.toConvertDb().toDouble(),
    idPT = idPT,
    suffix = suffix,
    status = status,
    note = note
)