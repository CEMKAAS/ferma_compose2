package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainWriteOffTable
import com.zaroslikov.fermacompose2.data.ferma.WriteOffTable
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.ui.start.formatNumber

fun DomainWriteOffTable.toRoomMap(): WriteOffTable = WriteOffTable(
    id = id,
    title = title.trim(),
    count = count.toConvertDbDouble(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.toConvertDbDouble(),
    suffix = suffix,
    status = status,
    note = note.trim(),
    idPT = idPT.toInt(),
    animalCountId = animalCountId
)

fun WriteOffTable.toDomainMap(): DomainWriteOffTable = DomainWriteOffTable(
    id = id,
    title = title,
    count = count.formatNumber(),
    day = day,
    mount = mount,
    year = year,
    priceAll = priceAll.formatNumber(),
    suffix = suffix,
    status = status,
    note = note,
    idPT = idPT.toLong(),
    animalCountId = animalCountId
)
