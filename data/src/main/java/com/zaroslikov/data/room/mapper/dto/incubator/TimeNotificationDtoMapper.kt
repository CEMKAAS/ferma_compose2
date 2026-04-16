package com.zaroslikov.data.room.mapper.dto.incubator

import com.zaroslikov.data.room.dto.incubator.TimeNotificationDto
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotificationIncubatorDto

fun TimeNotificationDto.toDomainTimeNotificationDto(): DomainTimeNotificationIncubatorDto {
    return DomainTimeNotificationIncubatorDto(
        nameBookmark = this.nameBookmark,
        time = this.time,
        note = this.note,
        bookmarkId = this.bookmarkId,
        projectId = this.projectId,
    )
}