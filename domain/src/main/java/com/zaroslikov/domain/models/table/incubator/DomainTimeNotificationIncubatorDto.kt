package com.zaroslikov.domain.models.table.incubator

data class DomainTimeNotificationIncubatorDto(
    val nameBookmark: String,
    val time: String,
    val note: String?,
    val bookmarkId: Long,
    val projectId: Long
)
