package com.zaroslikov.data.room.mapper.table

import com.zaroslikov.data.room.table.incubator.TimeNotificationIncubatorTable
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotificationIncubator

fun DomainTimeNotificationIncubator.toTimeNotificationTable(): TimeNotificationIncubatorTable {
    return TimeNotificationIncubatorTable(
        id = this.id,
        time = this.time,
        note = this.note,
        bookmarkId = this.bookmarkId,
    )
}

fun TimeNotificationIncubatorTable.toDomainTimeNotification(): DomainTimeNotificationIncubator {
    return DomainTimeNotificationIncubator(
        id = this.id,
        time = this.time,
        note = this.note,
        bookmarkId = this.bookmarkId,
    )
}
