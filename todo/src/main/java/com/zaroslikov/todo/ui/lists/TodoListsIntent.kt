package com.zaroslikov.todo.ui.lists

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.TodoListIcon
import java.time.LocalDateTime

sealed interface TodoListsIntent {
    /** [tasks] — все задачи и подзадачи проекта, [now] — текущая минута для «Сегодня» и просрочки. */
    data class DataLoaded(
        val lists: List<DomainTodoList>,
        val tasks: List<DomainTodoProjectTask>,
        val now: LocalDateTime
    ) : TodoListsIntent

    // Шторка «название + цвет» списка
    data object OpenCreate : TodoListsIntent
    data class OpenEdit(val list: DomainTodoList) : TodoListsIntent
    data class FormTitleChanged(val title: String) : TodoListsIntent
    data class FormColorChanged(val colorIndex: Int) : TodoListsIntent
    data class FormIconChanged(val icon: TodoListIcon) : TodoListsIntent
    data object CloseForm : TodoListsIntent
    data object SaveForm : TodoListsIntent

    /** Закрепить плиткой над «Моими списками» ([pinned]) или открепить. */
    data class SetPinned(val listId: Long, val pinned: Boolean) : TodoListsIntent

    /**
     * Перенос удержанием: список [listId] на место [index] среди списков своей группы без него
     * самого — закреплённых (плитки) или незакреплённых («Мои списки»).
     */
    data class DropList(val listId: Long, val index: Int) : TodoListsIntent

    /** Запись порядка или закрепления не удалась — порядок и закрепление как в [lists]. */
    data class ListsChangeFailed(val lists: List<DomainTodoList>) : TodoListsIntent

    data class RequestDelete(val list: DomainTodoList) : TodoListsIntent
    data object DismissDelete : TodoListsIntent
    data object ConfirmDelete : TodoListsIntent
}
