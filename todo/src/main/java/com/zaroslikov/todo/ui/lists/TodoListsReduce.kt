package com.zaroslikov.todo.ui.lists

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.isDone
import com.zaroslikov.todo.domain.model.todoIncompleteCountByList
import com.zaroslikov.todo.domain.model.todoInsertIndex
import com.zaroslikov.todo.domain.model.todoMoved
import com.zaroslikov.todo.domain.model.todoSmartCount
import com.zaroslikov.todo.ui.theme.todoColorPalette

fun todoListsReduce(state: TodoListsState, intent: TodoListsIntent): TodoListsState = when (intent) {
    is TodoListsIntent.DataLoaded -> state.copy(
        isLoading = false,
        lists = intent.lists,
        tasks = intent.tasks,
        now = intent.now
    ).withDerived()

    // Новый список получает следующий цвет палитры, чтобы соседние списки различались
    TodoListsIntent.OpenCreate -> state.copy(
        form = TodoTitleColorForm(colorIndex = state.lists.size % todoColorPalette.size)
    )

    is TodoListsIntent.OpenEdit -> state.copy(
        form = TodoTitleColorForm(
            id = intent.list.id,
            title = intent.list.title,
            colorIndex = intent.list.colorIndex,
            icon = intent.list.icon
        )
    )

    is TodoListsIntent.FormTitleChanged -> state.copy(form = state.form?.withTitle(intent.title))
    is TodoListsIntent.FormColorChanged -> state.copy(form = state.form?.copy(colorIndex = intent.colorIndex))
    is TodoListsIntent.FormIconChanged -> state.copy(form = state.form?.copy(icon = intent.icon))
    TodoListsIntent.CloseForm -> state.copy(form = null)
    TodoListsIntent.SaveForm -> state.copy(form = state.form?.submitted())

    // Позиция не меняется: закреплённые плитки идут в порядке списков
    is TodoListsIntent.SetPinned -> state.copy(
        lists = state.lists.map { if (it.id == intent.listId) it.copy(isPinned = intent.pinned) else it }
    )

    is TodoListsIntent.DropList -> state.copy(lists = state.lists.withListMoved(intent.listId, intent.index))

    is TodoListsIntent.ListsChangeFailed -> {
        val old = intent.lists.associateBy { it.id }
        state.copy(
            lists = state.lists
                .map { list -> old[list.id]?.let { list.copy(position = it.position, isPinned = it.isPinned) } ?: list }
                .sortedWith(LIST_ORDER)
        )
    }

    is TodoListsIntent.RequestDelete -> state.copy(listToDelete = intent.list)
    TodoListsIntent.DismissDelete -> state.copy(listToDelete = null)
    // Список и его задачи пропадают сразу, не дожидаясь Flow из БД
    TodoListsIntent.ConfirmDelete -> {
        val id = state.listToDelete?.id
        state.copy(
            listToDelete = null,
            lists = state.lists.filterNot { it.id == id },
            tasks = state.tasks.filterNot { it.listId == id }
        ).withDerived()
    }
}

private val LIST_ORDER = compareBy<DomainTodoList>({ it.position }, { it.id })

/**
 * Список [listId] на место [index] среди списков своей группы без него самого: закреплённый —
 * среди закреплённых (плитки), незакреплённый — среди незакреплённых («Мои списки»). Списки
 * другой группы остаются на своих местах относительно остальных. Позиции всех списков —
 * подряд с нуля, как пишет `TodoListRepository.reorderLists`. Неизвестный список и место, где он
 * и так стоит в группе, — без изменений.
 */
internal fun List<DomainTodoList>.withListMoved(listId: Long, index: Int): List<DomainTodoList> {
    val moved = firstOrNull { it.id == listId } ?: return this
    val all = map { it.id } - listId
    val groupIds = filter { it.isPinned == moved.isPinned }.map { it.id }
    // Порядок в группе тот же — позиции не трогаем, иначе список переехал бы среди другой группы
    if (groupIds.todoMoved(listId, index) == groupIds) return this
    val group = groupIds - listId
    val order = map { it.id }.todoMoved(listId, todoInsertIndex(all, group, index))
    val byId = associateBy { it.id }
    return order.mapIndexed { position, id ->
        val list = byId.getValue(id)
        if (list.position == position) list else list.copy(position = position)
    }
}

/** Пересчитывает числа на плитках и у списков. */
private fun TodoListsState.withDerived(): TodoListsState {
    val done: (DomainTodoProjectTask) -> Boolean = { it.item.isDone }
    return copy(
        smartCounts = TODO_SMART_TILES.associateWith { todoSmartCount(tasks, it, now, done) },
        listCounts = todoIncompleteCountByList(tasks, done)
    )
}
