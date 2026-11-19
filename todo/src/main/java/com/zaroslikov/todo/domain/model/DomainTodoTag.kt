package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/** Тег проекта; название уникально в пределах проекта. */
@Serializable
data class DomainTodoTag(
    val id: Long = 0,
    val idPT: Long,
    val title: String,
    val colorIndex: Int = 0
)

/** Тег с таким названием в проекте уже есть — переименование отклонено. */
class TodoDuplicateTagException(val existingId: Long) : IllegalStateException("Тег уже существует")
