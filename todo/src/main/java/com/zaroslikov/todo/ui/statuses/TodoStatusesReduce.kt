package com.zaroslikov.todo.ui.statuses

import com.zaroslikov.todo.domain.model.sortedForDisplay
import com.zaroslikov.todo.ui.lists.TodoTitleColorForm
import com.zaroslikov.todo.ui.theme.todoColorPalette

fun todoStatusesReduce(state: TodoStatusesState, intent: TodoStatusesIntent): TodoStatusesState = when (intent) {
    is TodoStatusesIntent.DataLoaded -> state.copy(isLoading = false, statuses = intent.statuses.sortedForDisplay())

    TodoStatusesIntent.OpenCreate -> state.copy(
        form = TodoTitleColorForm(colorIndex = state.statuses.size % todoColorPalette.size)
    )

    // Дефолтный статус нельзя переименовать — только перекрасить
    is TodoStatusesIntent.OpenEdit -> state.copy(
        form = TodoTitleColorForm(
            id = intent.status.id,
            title = intent.status.title,
            colorIndex = intent.status.colorIndex,
            titleEditable = !intent.status.isDefault
        )
    )

    is TodoStatusesIntent.FormTitleChanged -> state.copy(
        form = state.form?.let { if (it.titleEditable) it.withTitle(intent.title) else it }
    )

    is TodoStatusesIntent.FormColorChanged -> state.copy(form = state.form?.copy(colorIndex = intent.colorIndex))
    TodoStatusesIntent.CloseForm -> state.copy(form = null)
    TodoStatusesIntent.SaveForm -> state.copy(form = state.form?.submitted())

    // Дефолтный статус удалить нельзя — диалог для него не открываем
    is TodoStatusesIntent.RequestDelete -> state.copy(
        statusToDelete = intent.status.takeUnless { it.isDefault }
    )

    TodoStatusesIntent.DismissDelete -> state.copy(statusToDelete = null)
    TodoStatusesIntent.ConfirmDelete -> {
        val id = state.statusToDelete?.id
        state.copy(statusToDelete = null, statuses = state.statuses.filterNot { it.id == id })
    }
}
