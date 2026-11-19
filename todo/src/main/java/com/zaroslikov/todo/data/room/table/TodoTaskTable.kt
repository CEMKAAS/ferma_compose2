package com.zaroslikov.todo.data.room.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod

/**
 * Удаление списка или родительской задачи каскадом удаляет задачи и подзадачи.
 * `status_id` без внешнего ключа: при удалении статуса задачи явно переводятся в «В планах».
 * Удаление подсписка обнуляет `section_id` его задач — они остаются в списке без подсписка.
 */
@Entity(
    tableName = "todo_task_table",
    foreignKeys = [
        ForeignKey(
            entity = TodoListTable::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TodoTaskTable::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TodoSectionTable::class,
            parentColumns = ["id"],
            childColumns = ["section_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["list_id"]),
        Index(value = ["parent_id"]),
        Index(value = ["status_id"]),
        Index(value = ["section_id"])
    ]
)
data class TodoTaskTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "list_id")
    val listId: Long,
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "note", defaultValue = "''")
    val note: String = "",
    @ColumnInfo(name = "priority", defaultValue = "'none'")
    val priority: TodoPriority = TodoPriority.NONE,
    @ColumnInfo(name = "status_id")
    val statusId: Long,
    @ColumnInfo(name = "due_date")
    val dueDate: String? = null,
    @ColumnInfo(name = "due_time")
    val dueTime: String? = null,
    @ColumnInfo(name = "position", defaultValue = "0")
    val position: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,
    // Добавлена в версии 2 через ALTER TABLE — колонка в конце таблицы
    @ColumnInfo(name = "section_id")
    val sectionId: Long? = null,
    @ColumnInfo(name = "repeat_period", defaultValue = "'none'")
    val repeatPeriod: TodoRepeatPeriod = TodoRepeatPeriod.NONE,
    @ColumnInfo(name = "repeat_end_date")
    val repeatEndDate: String? = null,
    @ColumnInfo(name = "recurrence_generated", defaultValue = "0")
    val recurrenceGenerated: Boolean = false,
    @ColumnInfo(name = "assignees", defaultValue = "'[]'")
    val assignees: List<String> = emptyList()
)
