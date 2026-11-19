package com.zaroslikov.todo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.domain.model.TodoListViewMode

@Entity(
    tableName = "todo_list_table",
    indices = [Index(value = ["idPT"])]
)
data class TodoListTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "idPT")
    val idPT: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "color_index", defaultValue = "0")
    val colorIndex: Int = 0,
    @ColumnInfo(name = "position", defaultValue = "0")
    val position: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "view_mode", defaultValue = "'list'")
    val viewMode: TodoListViewMode = TodoListViewMode.LIST,
    /** Стабильный код значка (`TodoListIcon.code`), не id ресурса. */
    @ColumnInfo(name = "icon", defaultValue = "'list'")
    val icon: TodoListIcon = TodoListIcon.DEFAULT,
    @ColumnInfo(name = "is_pinned", defaultValue = "0")
    val isPinned: Boolean = false,
    @ColumnInfo(name = "is_last_task_destination", defaultValue = "0")
    val isLastTaskDestination: Boolean = false
)
