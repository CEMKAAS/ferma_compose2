package com.zaroslikov.todo.ui.tags

import com.zaroslikov.todo.ui.lists.TodoTitleColorForm
import com.zaroslikov.todo.ui.theme.todoColorPalette

fun todoTagsReduce(state: TodoTagsState, intent: TodoTagsIntent): TodoTagsState = when (intent) {
    is TodoTagsIntent.DataLoaded -> state.copy(isLoading = false, tags = intent.tags)

    TodoTagsIntent.OpenCreate -> state.copy(
        form = TodoTitleColorForm(colorIndex = state.tags.size % todoColorPalette.size)
    )

    is TodoTagsIntent.OpenEdit -> state.copy(
        form = TodoTitleColorForm(id = intent.tag.id, title = intent.tag.title, colorIndex = intent.tag.colorIndex)
    )

    is TodoTagsIntent.FormTitleChanged -> state.copy(form = state.form?.withTitle(intent.title))
    is TodoTagsIntent.FormColorChanged -> state.copy(form = state.form?.copy(colorIndex = intent.colorIndex))
    TodoTagsIntent.CloseForm -> state.copy(form = null)
    TodoTagsIntent.SaveForm -> state.copy(form = state.form?.submitted())

    is TodoTagsIntent.RequestDelete -> state.copy(tagToDelete = intent.tag)
    TodoTagsIntent.DismissDelete -> state.copy(tagToDelete = null)
    TodoTagsIntent.ConfirmDelete -> {
        val id = state.tagToDelete?.id
        state.copy(tagToDelete = null, tags = state.tags.filterNot { it.id == id })
    }
}
