package com.zaroslikov.todo.ui.lists

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.TodoSmartList
import java.time.LocalDateTime

/** Плитки умных списков 2×2 в порядке «Напоминаний». */
val TODO_SMART_TILES = listOf(TodoSmartList.TODAY, TodoSmartList.SCHEDULED, TodoSmartList.ALL, TodoSmartList.COMPLETED)

/** [lists] — все списки проекта по позиции (затем по id), как их отдаёт БД. */
data class TodoListsState(
    val isLoading: Boolean = true,
    val lists: List<DomainTodoList> = emptyList(),
    /** Все задачи и подзадачи проекта, без фильтров. */
    val tasks: List<DomainTodoProjectTask> = emptyList(),
    val now: LocalDateTime = LocalDateTime.now(),
    /** Открытая шторка создания/правки списка; null — закрыта. */
    val form: TodoTitleColorForm? = null,
    val listToDelete: DomainTodoList? = null,
    /** Числа на плитках; у «Завершено» тоже считается, но не показывается — как в iOS. */
    val smartCounts: Map<TodoSmartList, Int> = emptyMap(),
    /** Незавершённые задачи и подзадачи каждого списка — число справа в «Мои списки». */
    val listCounts: Map<Long, Int> = emptyMap()
) {
    /** Закреплённые — плитками после умных списков, по позиции. */
    val pinnedLists: List<DomainTodoList> get() = lists.filter { it.isPinned }

    /** Карточка «Мои списки» — только незакреплённые. */
    val unpinnedLists: List<DomainTodoList> get() = lists.filterNot { it.isPinned }
}
