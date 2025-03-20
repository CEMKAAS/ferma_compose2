package com.zaroslikov.fermacompose2.data.mapper

import com.zaroslikov.fermacompose2.Domain.models.DomainAddTable
import com.zaroslikov.fermacompose2.data.ferma.AddTable


fun AddTable.toDomainMap(): DomainAddTable = DomainAddTable(
    id,
    title,
    count,
    day,
    mount,
    year,
    priceAll,
    suffix,
    category,
    idAnimal,
    animal,
    note,
    idPT
)

fun DomainAddTable.toRoomMap(): AddTable = AddTable(
    id,
    title,
    count,
    day,
    mount,
    year,
    priceAll,
    suffix,
    category,
    idAnimal,
    animal,
    note,
    idPT
)