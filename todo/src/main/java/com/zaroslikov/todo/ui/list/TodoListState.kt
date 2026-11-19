package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoListViewMode
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.domain.model.isDone
import com.zaroslikov.todo.ui.lists.TodoTitleColorForm
import java.time.LocalDateTime

/**
 * Строка ввода прямо в списке: новая задача ([taskId] = null) или переименование существующей.
 * [sectionId] — подсписок, в конец которого добавляется новая задача (null — «без подсписка»).
 * [parentId] — родитель новой подзадачи; редактор располагается после его подзадач.
 */
data class TodoInlineEditor(
    val taskId: Long?,
    val text: String,
    val sectionId: Long? = null,
    val note: String = "",
    val parentId: Long? = null
)

/** Текст после закрытия редактора, пока запись ещё не подтверждена потоком БД. */
data class TodoTaskText(
    val title: String,
    val note: String,
    val revision: Long = 0,
    val saved: Boolean = false,
    val dataRevision: Long = 0
)

/** Видимая строка списка: задача или подзадача (с отступом) с уже применённой отметкой. */
data class TodoListRow(
    val item: DomainTodoTaskItem,
    val isDone: Boolean,
    val isSubtask: Boolean,
    /** Сколько подзадач показано бы под задачей — для шеврона «свернуть/развернуть». */
    val childCount: Int = 0,
    val isCollapsed: Boolean = false
)

/**
 * Подсписок на экране; [section] = null — задачи «без подсписка» (в виде «список» без заголовка).
 * [rows] — строки вида «список» (пусто у свёрнутого подсписка), [cards] — задачи верхнего уровня
 * для карточек вида «столбцы» (сворачивание там не применяется).
 */
data class TodoListGroup(
    val section: DomainTodoSection?,
    val rows: List<TodoListRow>,
    val cards: List<TodoListRow>,
    val isCollapsed: Boolean = false,
    /** Содержимое карточек столбцов: сворачивание подсписка на него не влияет. */
    val cardRows: List<TodoListRow> = rows
) {
    /** Число задач в заголовке свёрнутого подсписка. */
    val taskCount: Int get() = cards.size
}

/**
 * Название подсписка на месте — в заголовке вида «список» и в столбце: [sectionId] = null —
 * «Новый столбик» (после создания сразу открывается ввод первой задачи), иначе переименование.
 */
data class TodoSectionEditor(
    val sectionId: Long?,
    val title: String
)

/** Шторка «Новый подсписок» (переименование — на месте, [TodoSectionEditor]). */
data class TodoSectionForm(
    val title: String = "",
    val titleError: Boolean = false
) {
    val isValid: Boolean get() = title.isNotBlank()
}

data class TodoListState(
    val isLoading: Boolean = true,
    val list: DomainTodoList? = null,
    /** Все задачи списка вместе с подзадачами, без фильтров. */
    val tasks: List<DomainTodoTaskItem> = emptyList(),
    val tags: List<DomainTodoTag> = emptyList(),
    /** Подсписки списка по позиции; свёрнутость — оптимистичная, пока БД не вернула то же. */
    val sections: List<DomainTodoSection> = emptyList(),
    /** Показывать завершённые; по умолчанию скрыты, как в «Напоминаниях». */
    val showCompleted: Boolean = false,
    /** Оптимистичные отметки: id → завершена ли, пока БД не вернула то же самое. */
    val doneOverrides: Map<Long, Boolean> = emptyMap(),
    val textOverrides: Map<Long, TodoTaskText> = emptyMap(),
    val textRevision: Long = 0,
    val dataRevision: Long = 0,
    /** Только что отмеченные при скрытых завершённых: видны закрашенными ~1,5 с, затем исчезают. */
    val lingering: Set<Long> = emptySet(),
    /** Удалённые свайпом — скрыты сразу, не дожидаясь БД. */
    val hiddenIds: Set<Long> = emptySet(),
    /** Задачи со свёрнутыми подзадачами; по умолчанию все развёрнуты. */
    val collapsed: Set<Long> = emptySet(),
    val query: String = "",
    val priorityFilter: TodoPriority? = null,
    val tagFilter: Long? = null,
    val showFilterSheet: Boolean = false,
    val editor: TodoInlineEditor? = null,
    val sectionEditor: TodoSectionEditor? = null,
    /** Готовые к показу строки вида «список» всех подсписков подряд: дерево, фильтры, скрытие и сворачивание. */
    val rows: List<TodoListRow> = emptyList(),
    /** Первая — «без подсписка», затем подсписки по позиции; при фильтрах пустые подсписки скрыты. */
    val groups: List<TodoListGroup> = emptyList(),
    /** Завершённых задач и подзадач в списке (с учётом оптимистичных отметок). */
    val completedCount: Int = 0,
    /** Задача, удаление которой нужно подтвердить (есть подзадачи, комментарии или связи). */
    val deleteCandidate: DomainTodoTaskItem? = null,
    val showClearCompleted: Boolean = false,
    val form: TodoTitleColorForm? = null,
    val showDeleteList: Boolean = false,
    val sectionForm: TodoSectionForm? = null,
    val sectionToDelete: DomainTodoSection? = null,
    val searchText: Map<Long, List<String>> = emptyMap(),
    val searchLabels: Map<Int, String> = emptyMap(),
    val searchNow: LocalDateTime? = null
) {
    val hasFilters: Boolean get() = query.isNotBlank() || priorityFilter != null || tagFilter != null

    val viewMode: TodoListViewMode get() = list?.viewMode ?: TodoListViewMode.LIST

    /**
     * Столбцы вида «столбцы»: «Без подсписка» — если в нём есть задачи, идёт ввод новой задачи
     * без подсписка или подсписков нет вовсе; затем подсписки.
     */
    val columns: List<TodoListGroup>
        get() = groups.filter { group ->
            group.section != null || group.cards.isNotEmpty() || groups.size == 1 ||
                (editor != null && editor.taskId == null && editor.sectionId == null)
        }

    /** Отметка задачи на экране: оптимистичная, если есть, иначе из БД. */
    fun isDone(item: DomainTodoTaskItem): Boolean = doneOverrides[item.task.id] ?: item.isDone
}

/** Удаление теряет больше, чем саму строку, — нужно подтверждение. */
val DomainTodoTaskItem.needsDeleteConfirm: Boolean
    get() = subtaskCount > 0 || commentCount > 0 || linkCount > 0
