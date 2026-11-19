package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/** Комментарий к задаче; даты — `yyyy-MM-dd'T'HH:mm`, [editedAt] = null, если не изменялся. */
@Serializable
data class DomainTodoComment(
    val id: Long = 0,
    val taskId: Long,
    val text: String,
    val createdAt: String,
    val editedAt: String? = null
)
