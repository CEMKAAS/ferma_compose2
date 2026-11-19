package com.zaroslikov.todo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile

/** Отдельный справочник проекта: удаление задачи не удаляет имя исполнителя. */
@Entity(tableName = "todo_assignee_table", primaryKeys = ["idPT", "name_key"])
data class TodoAssigneeTable(
    @ColumnInfo(name = "idPT") val idPT: Long,
    @ColumnInfo(name = "name_key") val nameKey: String,
    @ColumnInfo(name = "name") val name: String,
    // NULL у записей старого справочника и при добавлении имени из задачи.
    @ColumnInfo(name = "profile") val profile: TodoAssigneeProfile? = null
)
