package com.zaroslikov.data.room.table.incubator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "time_notification_table",
    foreignKeys = [
        ForeignKey(
            entity = BookmarkTable::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("bookmark_id"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("bookmark_id")]
)
data class TimeNotificationTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    val time: String,
    val note: String?,
    @ColumnInfo(name = "bookmark_id")
    val bookmarkId: Long = 0
)