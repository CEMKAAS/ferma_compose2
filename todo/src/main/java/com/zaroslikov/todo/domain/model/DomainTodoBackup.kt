package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/** Строка связи «задача — тег». */
@Serializable
data class DomainTodoTaskTag(
    val taskId: Long,
    val tagId: Long
)

/** Содержимое `todo_database` для общего бэкапа приложения. */
@Serializable
data class DomainTodoBackup(
    val lists: List<DomainTodoList> = emptyList(),
    /** Подсписки; в бэкапах до версии БД 2 поля нет — пустой список. */
    val sections: List<DomainTodoSection> = emptyList(),
    val statuses: List<DomainTodoStatus> = emptyList(),
    val tasks: List<DomainTodoTask> = emptyList(),
    val tags: List<DomainTodoTag> = emptyList(),
    val taskTags: List<DomainTodoTaskTag> = emptyList(),
    val links: List<DomainTodoLink> = emptyList(),
    val comments: List<DomainTodoComment> = emptyList(),
    val assignees: List<DomainTodoAssignee> = emptyList()
)
