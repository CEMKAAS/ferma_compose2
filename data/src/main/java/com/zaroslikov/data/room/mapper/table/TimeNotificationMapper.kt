package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.incubator.TimeNotificationTable
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotification

fun DomainTimeNotification.toTimeNotificationTable(): TimeNotificationTable {
    return TimeNotificationTable(
        id = this.id,
        time = this.time,
        note = this.note,
        bookmarkId = this.bookmarkId,
    )
}

fun TimeNotificationTable.toDomainTimeNotification(): DomainTimeNotification {
    return DomainTimeNotification(
        id = this.id,
        time = this.time,
        note = this.note,
        bookmarkId = this.bookmarkId,
    )
}