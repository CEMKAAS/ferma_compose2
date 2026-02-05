package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.incubator.BookmarkTable
import com.zaroslikov.domain.models.table.DomainBookmark

fun BookmarkTable.toDomainBookmark(): DomainBookmark {
    return DomainBookmark(
        id = this.id,
        title = this.title,
        type = this.type,
        breed = this.breed,
        count = this.count,
        date = this.date,
        time = this.time,
        price = this.price,
        autoPrice = this.autoPrice,
        note = this.note,
        isAutoRotation = this.isAutoRotation,
        isAutoVentilation = this.isAutoVentilation,
        isActivityBookmark = this.isActivityBookmark,
        idPT = this.idPT,
    )
}

fun DomainBookmark.toBookmarkTable(): BookmarkTable {
    return BookmarkTable(
        id = this.id,
        title = this.title,
        type = this.type,
        breed = this.breed,
        count = this.count,
        date = this.date,
        time = this.time,
        price = this.price,
        autoPrice = this.autoPrice,
        note = this.note,
        isAutoRotation = this.isAutoRotation,
        isAutoVentilation = this.isAutoVentilation,
        isActivityBookmark = this.isActivityBookmark,
        idPT = this.idPT,
    )
}