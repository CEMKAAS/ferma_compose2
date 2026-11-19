package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/**
 * Подсписок (раздел) внутри списка. В подсписке лежат только задачи верхнего уровня
 * (`DomainTodoTask.sectionId`), подзадачи показываются под своим родителем.
 * [isCollapsed] — свёрнут ли подсписок в виде «список», хранится в БД.
 */
@Serializable
data class DomainTodoSection(
    val id: Long = 0,
    val listId: Long,
    val title: String,
    val position: Int = 0,
    val isCollapsed: Boolean = false
)
