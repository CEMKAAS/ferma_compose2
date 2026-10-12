package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.ferma.RecyclingTable
import com.zaroslikov.domain.models.table.DomainRecycling

fun DomainRecycling.toRoomMap(): RecyclingTable = RecyclingTable(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    category = category,
    day = day,
    month = month,
    year = year,
    readyDay = readyDay,
    readyMonth = readyMonth,
    readyYear = readyYear,
    isCompleted = isCompleted,
    addId = addId,
    note = note,
    idPT = idPT
)

fun RecyclingTable.toDomainMap(): DomainRecycling = DomainRecycling(
    id = id,
    title = title,
    count = count,
    countSuffix = countSuffix,
    category = category,
    day = day,
    month = month,
    year = year,
    readyDay = readyDay,
    readyMonth = readyMonth,
    readyYear = readyYear,
    isCompleted = isCompleted,
    addId = addId,
    note = note,
    idPT = idPT
)
