package com.zaroslikov.todo.ui.statuses

import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.ui.lists.TodoTitleColorForm

data class TodoStatusesState(
    val isLoading: Boolean = true,
    /** Статусы в порядке отображения: завершающие в конце. */
    val statuses: List<DomainTodoStatus> = emptyList(),
    /** Открытая шторка; у дефолтного статуса название в ней не редактируется. */
    val form: TodoTitleColorForm? = null,
    val statusToDelete: DomainTodoStatus? = null
) {
    /** Статус, который сейчас правится в шторке; null — создание или шторка закрыта. */
    val editingStatus: DomainTodoStatus?
        get() = form?.takeUnless { it.isNew }?.let { f -> statuses.firstOrNull { it.id == f.id } }
}
