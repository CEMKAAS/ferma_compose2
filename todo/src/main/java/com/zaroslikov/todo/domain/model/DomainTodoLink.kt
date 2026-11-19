package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/** Связь двух задач; для [TodoLinkType.BLOCKS] [taskId] блокирует [linkedTaskId]. */
@Serializable
data class DomainTodoLink(
    val id: Long = 0,
    val taskId: Long,
    val linkedTaskId: Long,
    val type: TodoLinkType = TodoLinkType.RELATES
)

/**
 * Связь с точки зрения открытой задачи: [task] — другая сторона связи. [isOutgoing] = true,
 * если открытая задача — `task_id` связи (для BLOCKS — «Блокирует»), иначе «Заблокирована задачей».
 */
data class DomainTodoLinkedTask(
    val link: DomainTodoLink,
    val task: DomainTodoTask,
    val status: DomainTodoStatus?,
    val listTitle: String,
    val isOutgoing: Boolean,
    val listIcon: TodoListIcon = TodoListIcon.DEFAULT
)
