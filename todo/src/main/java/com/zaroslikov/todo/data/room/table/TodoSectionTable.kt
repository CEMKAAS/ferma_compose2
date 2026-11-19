package com.zaroslikov.todo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Подсписок списка; удаление списка каскадом удаляет подсписки. */
@Entity(
    tableName = "todo_section_table",
    foreignKeys = [
        ForeignKey(
            entity = TodoListTable::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["list_id"])]
)
data class TodoSectionTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "list_id")
    val listId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "position", defaultValue = "0")
    val position: Int = 0,
    @ColumnInfo(name = "is_collapsed", defaultValue = "0")
    val isCollapsed: Boolean = false
)
