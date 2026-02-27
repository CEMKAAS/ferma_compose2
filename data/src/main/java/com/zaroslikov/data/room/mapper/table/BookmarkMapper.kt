package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.incubator.BookmarkTable
import com.zaroslikov.domain.models.table.DomainBookmark

fun BookmarkTable.toDomainBookmark(): DomainBookmark {
    return DomainBookmark(
        id = id,
        title = title,
        type = type,
        breed = breed,
        count = count,
        rejectedCount = rejectedCount,
        startDate = startDate,
        endDate = endDate,
        isEarlyCompletionStatus = isEarlyCompletionStatus,
        time = time,
        price = price,
        priceAll = priceAll,
        chickPrice = chickPrice,
        note = note,
        isAutoRotation = isAutoRotation,
        isAutoVentilation = isAutoVentilation,
        isActivityBookmark = isActivityBookmark,
        idPT = idPT
    )
}

fun DomainBookmark.toBookmarkTable(): BookmarkTable {
    return BookmarkTable(
        id = id,
        title = title,
        type = type,
        breed = breed,
        count = count,
        rejectedCount = rejectedCount,
        startDate = startDate,
        endDate = endDate,
        isEarlyCompletionStatus = isEarlyCompletionStatus,
        time = time,
        price = price,
        priceAll = priceAll,
        chickPrice = chickPrice,
        note = note,
        isAutoRotation = isAutoRotation,
        isAutoVentilation = isAutoVentilation,
        isActivityBookmark = isActivityBookmark,
        idPT = idPT
    )
}