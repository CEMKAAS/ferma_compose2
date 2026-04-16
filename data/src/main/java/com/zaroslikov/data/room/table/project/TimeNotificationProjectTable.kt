package com.zaroslikov.data.room.table.project

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "time_notification_project_table",
    foreignKeys = [
        ForeignKey(
            entity = ProjectTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("project_id"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("project_id")]
)
data class TimeNotificationProjectTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    val time: String,
    val note: String?,
    @ColumnInfo(name = "project_id")
    val projectId: Long = 0
)