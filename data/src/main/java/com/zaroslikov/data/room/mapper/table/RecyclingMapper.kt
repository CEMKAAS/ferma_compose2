package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.ferma.RecyclingProductTable
import com.zaroslikov.data.room.table.ferma.RecyclingTable
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainRecyclingProduct

fun DomainRecycling.toRoomMap(): RecyclingTable = RecyclingTable(
    id = id,
    day = day,
    month = month,
    year = year,
    startHour = startHour,
    startMinute = startMinute,
    note = note,
    idPT = idPT
)

fun RecyclingTable.toDomainMap(): DomainRecycling = DomainRecycling(
    id = id,
    day = day,
    month = month,
    year = year,
    startHour = startHour,
    startMinute = startMinute,
    note = note,
    idPT = idPT
)

fun DomainRecyclingProduct.toRoomMap(): RecyclingProductTable = RecyclingProductTable(
    id = id,
    recyclingId = recyclingId,
    title = title,
    count = count,
    countSuffix = countSuffix,
    category = category,
    readyDay = readyDay,
    readyMonth = readyMonth,
    readyYear = readyYear,
    readyHour = readyHour,
    readyMinute = readyMinute,
    isCompleted = isCompleted,
    addId = addId,
    idPT = idPT
)

fun RecyclingProductTable.toDomainMap(): DomainRecyclingProduct = DomainRecyclingProduct(
    id = id,
    recyclingId = recyclingId,
    title = title,
    count = count,
    countSuffix = countSuffix,
    category = category,
    readyDay = readyDay,
    readyMonth = readyMonth,
    readyYear = readyYear,
    readyHour = readyHour,
    readyMinute = readyMinute,
    isCompleted = isCompleted,
    addId = addId,
    idPT = idPT
)
