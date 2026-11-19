package com.zaroslikov.todo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.todo.domain.model.TodoLinkType

/** Обратная пара `(linked_task_id, task_id)` отсекается в репозитории — индексом её не выразить. */
@Entity(
    tableName = "todo_task_link_table",
    foreignKeys = [
        ForeignKey(
            entity = TodoTaskTable::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TodoTaskTable::class,
            parentColumns = ["id"],
            childColumns = ["linked_task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["task_id", "linked_task_id"], unique = true),
        Index(value = ["linked_task_id"])
    ]
)
data class TodoTaskLinkTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    @ColumnInfo(name = "linked_task_id")
    val linkedTaskId: Long,
    @ColumnInfo(name = "type", defaultValue = "'relates'")
    val type: TodoLinkType = TodoLinkType.RELATES
)
