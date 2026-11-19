package com.zaroslikov.todo.data.room.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoTagTable
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.domain.model.TodoListIcon

// Проекции запросов. Статус приходит LEFT JOIN с префиксом `st_`: у задачи своя колонка
// `status_id`, поэтому префикс `status_` дал бы конфликт имён

data class TodoListSummaryDto(
    @Embedded
    val list: TodoListTable,
    @ColumnInfo(name = "total_count")
    val totalCount: Int,
    @ColumnInfo(name = "done_count")
    val doneCount: Int,
    @ColumnInfo(name = "overdue_count")
    val overdueCount: Int
)

/** Задача со статусом и счётчиками; теги — отдельным запросом [TodoTaskTagDto]. */
data class TodoTaskItemDto(
    @Embedded
    val task: TodoTaskTable,
    @Embedded(prefix = "st_")
    val status: TodoStatusTable?,
    @ColumnInfo(name = "subtask_count")
    val subtaskCount: Int,
    @ColumnInfo(name = "subtask_done_count")
    val subtaskDoneCount: Int,
    @ColumnInfo(name = "comment_count")
    val commentCount: Int,
    @ColumnInfo(name = "link_count")
    val linkCount: Int,
    @ColumnInfo(name = "blocking_count")
    val blockingCount: Int,
    @ColumnInfo(name = "blocked_by_count")
    val blockedByCount: Int
)

/** Задача проекта со своим списком и родителем (у задачи верхнего уровня [parentTitle] = null). */
data class TodoProjectTaskItemDto(
    @Embedded
    val item: TodoTaskItemDto,
    @ColumnInfo(name = "list_title")
    val listTitle: String,
    @ColumnInfo(name = "list_color_index")
    val listColorIndex: Int,
    @ColumnInfo(name = "list_icon")
    val listIcon: TodoListIcon,
    @ColumnInfo(name = "parent_title")
    val parentTitle: String?
)

data class TodoTaskTagDto(
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    @Embedded
    val tag: TodoTagTable
)

/** Связь глазами задачи `:taskId`: [task] — другая сторона связи. */
data class TodoLinkedTaskDto(
    @Embedded(prefix = "link_")
    val link: TodoTaskLinkTable,
    @Embedded
    val task: TodoTaskTable,
    @Embedded(prefix = "st_")
    val status: TodoStatusTable?,
    @ColumnInfo(name = "list_title")
    val listTitle: String,
    @ColumnInfo(name = "list_icon")
    val listIcon: TodoListIcon,
    @ColumnInfo(name = "is_outgoing")
    val isOutgoing: Boolean
)
