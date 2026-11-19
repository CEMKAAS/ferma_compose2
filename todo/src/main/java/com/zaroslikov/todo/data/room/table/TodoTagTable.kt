package com.zaroslikov.todo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "todo_tag_table",
    indices = [Index(value = ["idPT", "title"], unique = true)]
)
data class TodoTagTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "color_index", defaultValue = "0")
    val colorIndex: Int = 0
)
