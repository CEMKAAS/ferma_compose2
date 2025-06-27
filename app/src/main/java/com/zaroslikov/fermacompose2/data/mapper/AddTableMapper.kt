package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable
import com.zaroslikov.fermacompose2.supportFun.toConvertDb
import com.zaroslikov.fermacompose2.supportFun.toConvertDbDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroDouble
import com.zaroslikov.fermacompose2.supportFun.toConvertZeroString
import com.zaroslikov.fermacompose2.supportFun.toFormatNumber
import com.zaroslikov.fermacompose2.ui.start.formatNumber

fun DomainAddTable.toRoomMap(): AddTable = AddTable(
    id.toInt(),
    title.trim(),
    count.toConvertDbDouble(),
    day,
    mount,
    year,
    priceAll.toConvertZeroDouble(),
    suffix,
    category.trim(),
    idAnimal,
    animal,
    note.trim(),
    idPT.toInt()
)

fun AddTable.toDomainMap(): DomainAddTable = DomainAddTable(
    id.toLong(),
    title,
    count.formatNumber(),
    day,
    mount,
    year,
    priceAll.toString(),
    suffix,
    category,
    idAnimal,
    animal,
    note,
    idPT.toLong()
)