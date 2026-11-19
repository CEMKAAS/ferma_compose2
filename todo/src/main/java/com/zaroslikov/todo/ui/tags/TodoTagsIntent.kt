package com.zaroslikov.todo.ui.tags

import com.zaroslikov.todo.domain.model.DomainTodoTag

sealed interface TodoTagsIntent {
    data class DataLoaded(val tags: List<DomainTodoTag>) : TodoTagsIntent

    data object OpenCreate : TodoTagsIntent
    data class OpenEdit(val tag: DomainTodoTag) : TodoTagsIntent
    data class FormTitleChanged(val title: String) : TodoTagsIntent
    data class FormColorChanged(val colorIndex: Int) : TodoTagsIntent
    data object CloseForm : TodoTagsIntent
    data object SaveForm : TodoTagsIntent

    data class RequestDelete(val tag: DomainTodoTag) : TodoTagsIntent
    data object DismissDelete : TodoTagsIntent
    data object ConfirmDelete : TodoTagsIntent
}
