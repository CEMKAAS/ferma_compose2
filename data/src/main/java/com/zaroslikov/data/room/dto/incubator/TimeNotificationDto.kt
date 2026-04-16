package com.zaroslikov.data.room.dto.incubator

import androidx.room.ColumnInfo

data class TimeNotificationDto(
    @ColumnInfo(name = "name_bookmark")
    val nameBookmark: String,
    val time: String,
    val note: String?,
    @ColumnInfo(name = "bookmark_id")
    val bookmarkId: Long,
    @ColumnInfo(name = "project_id")
    val projectId: Long 
)