package com.zaroslikov.todo.ui.statuses

import com.zaroslikov.todo.domain.model.DomainTodoStatus

sealed interface TodoStatusesIntent {
    data class DataLoaded(val statuses: List<DomainTodoStatus>) : TodoStatusesIntent

    data object OpenCreate : TodoStatusesIntent
    data class OpenEdit(val status: DomainTodoStatus) : TodoStatusesIntent
    data class FormTitleChanged(val title: String) : TodoStatusesIntent
    data class FormColorChanged(val colorIndex: Int) : TodoStatusesIntent
    data object CloseForm : TodoStatusesIntent
    data object SaveForm : TodoStatusesIntent

    data class RequestDelete(val status: DomainTodoStatus) : TodoStatusesIntent
    data object DismissDelete : TodoStatusesIntent
    data object ConfirmDelete : TodoStatusesIntent
}
