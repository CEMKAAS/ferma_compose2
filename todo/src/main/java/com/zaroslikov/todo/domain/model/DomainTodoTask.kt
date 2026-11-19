package com.zaroslikov.todo.domain.model

import kotlinx.serialization.Serializable

/**
 * Задача списка. [parentId] задан у подзадачи (один уровень вложенности, тот же список).
 * [sectionId] — подсписок задачи верхнего уровня (null — «без подсписка»); у подзадачи всегда null.
 * [dueDate] — `yyyy-MM-dd`, [dueTime] — `HH:mm`, [createdAt]/[completedAt] — `yyyy-MM-dd'T'HH:mm`;
 * пустой [createdAt] при вставке = сейчас, [completedAt] выставляет репозиторий по статусу.
 */
@Serializable
data class DomainTodoTask(
    val id: Long = 0,
    val listId: Long,
    val parentId: Long? = null,
    val sectionId: Long? = null,
    val title: String,
    val note: String = "",
    val priority: TodoPriority = TodoPriority.NONE,
    val statusId: Long,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val position: Int = 0,
    val createdAt: String = "",
    val completedAt: String? = null,
    val repeatPeriod: TodoRepeatPeriod = TodoRepeatPeriod.NONE,
    /** Последний день повтора включительно, null — без окончания. */
    val repeatEndDate: String? = null,
    /** Следующий экземпляр уже создан, в том числе если затем был удалён. */
    val recurrenceGenerated: Boolean = false,
    /** Исполнители этой задачи; у подзадач собственный независимый набор. */
    val assignees: List<String> = emptyList()
)

/** Задача со всем, что нужно карточке в списке. [status] = null, если статус не найден. */
data class DomainTodoTaskItem(
    val task: DomainTodoTask,
    val status: DomainTodoStatus?,
    val tags: List<DomainTodoTag> = emptyList(),
    val subtaskCount: Int = 0,
    val subtaskDoneCount: Int = 0,
    val commentCount: Int = 0,
    val linkCount: Int = 0,
    val blockingCount: Int = 0,
    val blockedByCount: Int = 0
) {
    val relatedLinkCount: Int get() = (linkCount - blockingCount - blockedByCount).coerceAtLeast(0)
}

/**
 * Задача или подзадача на главном экране и в умных списках: со своим списком (название, цвет, значок)
 * и названием родителя ([parentTitle] = null у задачи верхнего уровня).
 */
data class DomainTodoProjectTask(
    val item: DomainTodoTaskItem,
    val listTitle: String,
    val listColorIndex: Int,
    val parentTitle: String? = null,
    val listIcon: TodoListIcon = TodoListIcon.DEFAULT
) {
    val id: Long get() = item.task.id
    val listId: Long get() = item.task.listId
}
