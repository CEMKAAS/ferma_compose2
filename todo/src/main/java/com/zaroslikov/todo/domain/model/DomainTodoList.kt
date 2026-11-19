package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/**
 * Список задач проекта; [createdAt] — `yyyy-MM-dd'T'HH:mm`, пустая строка при вставке = сейчас.
 * [viewMode] — вид экрана списка (строки или столбцы подсписков), [icon] — значок списка,
 * [isPinned] — закреплён плиткой над «Моими списками».
 */
@Serializable
data class DomainTodoList(
    val id: Long = 0,
    val idPT: Long,
    val title: String,
    val colorIndex: Int = 0,
    val position: Int = 0,
    val createdAt: String = "",
    val viewMode: TodoListViewMode = TodoListViewMode.LIST,
    val icon: TodoListIcon = TodoListIcon.DEFAULT,
    val isPinned: Boolean = false,
    val isLastTaskDestination: Boolean = false
)

/** Список со счётчиками для экрана списков; считаются только задачи верхнего уровня. */
data class DomainTodoListSummary(
    val list: DomainTodoList,
    val totalCount: Int,
    val doneCount: Int,
    val overdueCount: Int
)
