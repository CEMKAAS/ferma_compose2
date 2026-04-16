package com.zaroslikov.data.room.dto.project

import androidx.room.ColumnInfo

data class TimeNotificationProjectDto(
    @ColumnInfo(name = "name_project")
    val nameProject: String,
    val time: String,
    val note: String?,
    @ColumnInfo(name = "project_id")
    val projectId: Long
)