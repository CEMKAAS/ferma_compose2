package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/** Имя для назначений и необязательная анкета исполнителя проекта. */
@Serializable
data class DomainTodoAssignee(
    val idPT: Long,
    val name: String,
    val profile: TodoAssigneeProfile = TodoAssigneeProfile(firstName = name)
)
