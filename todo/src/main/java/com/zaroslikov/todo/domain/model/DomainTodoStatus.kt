package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/**
 * Статус задачи проекта. У дефолтного статуса [defaultCode] задан: его нельзя удалить и
 * переименовать, а показывается он строкой из ресурсов ([title] — запасной вариант).
 */
@Serializable
data class DomainTodoStatus(
    val id: Long = 0,
    val idPT: Long,
    val title: String,
    val colorIndex: Int = 0,
    val position: Int = 0,
    @Serializable(with = TodoDefaultStatusSerializer::class)
    val defaultCode: TodoDefaultStatus? = null,
    val isDone: Boolean = false
) {
    val isDefault: Boolean get() = defaultCode != null
}
