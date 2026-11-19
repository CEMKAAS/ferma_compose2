package com.zaroslikov.todo.ui.tags

import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.ui.lists.TodoTitleColorForm

data class TodoTagsState(
    val isLoading: Boolean = true,
    val tags: List<DomainTodoTag> = emptyList(),
    val form: TodoTitleColorForm? = null,
    val tagToDelete: DomainTodoTag? = null
)
