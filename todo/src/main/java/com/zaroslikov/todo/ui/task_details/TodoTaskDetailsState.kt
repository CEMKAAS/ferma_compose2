package com.zaroslikov.todo.ui.task_details

import androidx.annotation.StringRes
import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.DomainTodoLink
import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.isDone
import com.zaroslikov.todo.ui.lists.TodoTitleColorForm

/** Сколько найденных задач показывать в шторке связи. */
const val TODO_LINK_SEARCH_LIMIT = 50

/** Всё, что шторка «Подробно» получает из БД одним снимком. */
data class TodoTaskDetailsData(
    val item: DomainTodoTaskItem,
    /** Родитель подзадачи; `null` у задачи верхнего уровня. */
    val parent: DomainTodoTask?,
    val listTitle: String,
    val listColorIndex: Int,
    val idPT: Long,
    /** Статусы проекта в порядке показа. */
    val statuses: List<DomainTodoStatus>,
    /** Все теги проекта — для чипов выбора. */
    val tags: List<DomainTodoTag>,
    val subtasks: List<DomainTodoTaskItem>,
    val links: List<DomainTodoLinkedTask>,
    val comments: List<DomainTodoComment>,
    /** Подсписки списка задачи по позиции — для строки «Подсписок». */
    val sections: List<DomainTodoSection> = emptyList(),
    /** Сохранённые имена только проекта открытой задачи. */
    val projectAssignees: List<String> = emptyList(),
    /** Списки этого проекта для переноса задачи. */
    val projectLists: List<DomainTodoList> = emptyList()
) {
    val task: DomainTodoTask get() = item.task

    val isSubtask: Boolean get() = task.parentId != null

    val subtaskDoneCount: Int get() = subtasks.count { it.isDone }

    val tagIds: Set<Long> get() = item.tags.map { it.id }.toSet()
}

/** Шторка «Связать»: тип, направление относительно открытой задачи и строка поиска. */
data class TodoLinkDraft(
    val type: TodoLinkType = TodoLinkType.RELATES,
    val isOutgoing: Boolean = true,
    val query: String = "",
    val showCompleted: Boolean = false
) {
    /** В БД входящая блокировка — тот же BLOCKS с переставленными сторонами. */
    fun toLink(taskId: Long, selectedTaskId: Long): DomainTodoLink =
        if (type == TodoLinkType.BLOCKS && !isOutgoing)
            DomainTodoLink(taskId = selectedTaskId, linkedTaskId = taskId, type = type)
        else DomainTodoLink(taskId = taskId, linkedTaskId = selectedTaskId, type = type)
}

/** Правка комментария во вложенной шторке. */
data class TodoCommentDraft(
    val id: Long,
    val text: String,
    @StringRes val textError: Int? = null
)

/** Идентичность одного открытия выбора: поздний результат создания не меняет новый черновик. */
class TodoTagSelectionSession

data class TodoRepeatDraft(
    val period: com.zaroslikov.todo.domain.model.TodoRepeatPeriod,
    val endDate: String?,
    val hasError: Boolean = false
)

data class TodoTagSelectionDraft(
    val selectedIds: Set<Long>,
    val session: TodoTagSelectionSession = TodoTagSelectionSession()
)

/**
 * Шторка «Подробно». [taskId] = null — шторка закрыта. [title] и [note] — черновики полей:
 * в БД уходят при потере фокуса, «Готово», закрытии и переключении на другую задачу.
 */
data class TodoTaskDetailsState(
    val taskId: Long? = null,
    /** Задачи, из которых перешли внутри шторки; последняя — цель возврата. */
    val backStack: List<Long> = emptyList(),
    val isLoading: Boolean = false,
    val data: TodoTaskDetailsData? = null,
    val title: String = "",
    val note: String = "",
    /** Имя нового исполнителя; очищается после успешной записи. */
    val assigneeName: String = "",
    val showAssigneeChoices: Boolean = false,
    val tagSelection: TodoTagSelectionDraft? = null,
    val repeatDraft: TodoRepeatDraft? = null,
    /** `null` — шторка создания тега закрыта. */
    val newTag: TodoTitleColorForm? = null,
    /** Поле быстрого добавления подзадачи. */
    val quickSubtask: String = "",
    /** Поле нового комментария. */
    val commentText: String = "",
    /** `null` — шторка правки комментария закрыта. */
    val editingComment: TodoCommentDraft? = null,
    val commentToDelete: DomainTodoComment? = null,
    /** `null` — шторка связи закрыта. */
    val linkSheet: TodoLinkDraft? = null,
    /** Результат поиска по проекту, ещё без исключений — их считает [linkCandidates]. */
    val linkSearchResults: List<DomainTodoTask> = emptyList(),
    /** Списки проекта для подписей и значков найденных задач. */
    val linkSearchLists: Map<Long, DomainTodoList> = emptyMap(),
    val showDeleteDialog: Boolean = false
) {
    val showTagsSheet: Boolean get() = tagSelection != null
    val isOpen: Boolean get() = taskId != null

    val assigneeCandidates: List<String>
        get() {
            val data = data ?: return emptyList()
            val query = assigneeName.trim()
            return data.projectAssignees.filter { name ->
                name.contains(query, ignoreCase = true) &&
                    data.task.assignees.none { it.equals(name, ignoreCase = true) }
            }
        }

    /** Задачи, с которыми можно связать: без самой задачи и уже связанных в любую сторону. */
    val linkCandidates: List<DomainTodoTask>
        get() {
            val data = data ?: return emptyList()
            val excluded = data.links.map { it.task.id }.toSet() + data.task.id
            val doneStatusIds = data.statuses.filter { it.isDone }.map { it.id }.toSet()
            return linkSearchResults.asSequence()
                .filter { it.id !in excluded }
                .filter { linkSheet?.showCompleted == true || it.statusId !in doneStatusIds }
                .take(TODO_LINK_SEARCH_LIMIT)
                .toList()
        }
}
