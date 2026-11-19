package com.zaroslikov.todo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.todo.domain.model.TodoDefaultStatus

/**
 * Уникальность `(idPT, default_code)` не даёт продублировать дефолтный статус проекта;
 * у пользовательских статусов код NULL, а NULL-значения уникальность не нарушают.
 */
@Entity(
    tableName = "todo_status_table",
    indices = [Index(value = ["idPT", "default_code"], unique = true)]
)
data class TodoStatusTable(
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
    @ColumnInfo(name = "default_code")
    val defaultCode: TodoDefaultStatus? = null,
    @ColumnInfo(name = "is_done", defaultValue = "0")
    val isDone: Boolean = false
)
