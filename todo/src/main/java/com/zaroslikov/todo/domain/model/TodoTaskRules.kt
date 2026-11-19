package com.zaroslikov.todo.domain.model

import java.time.LocalDateTime

// Чистые правила задач: одни и те же на всех экранах и в тестах

/**
 * Просрочена ли незавершённая задача: срок раньше сегодняшнего дня, либо сегодня и время
 * срока уже прошло. Задача на сегодня без времени не просрочена до конца дня.
 * Так же считает SQL сводки списков (`TodoListDao.getSummaries`).
 */
fun isTodoOverdue(
    dueDate: String?,
    dueTime: String?,
    isDone: Boolean,
    now: LocalDateTime = LocalDateTime.now()
): Boolean {
    if (isDone) return false
    val date = parseTodoDate(dueDate) ?: return false
    val today = now.toLocalDate()
    if (date.isBefore(today)) return true
    if (date.isAfter(today)) return false
    val time = parseTodoTime(dueTime) ?: return false
    return time.isBefore(now.toLocalTime().withSecond(0).withNano(0))
}

val DomainTodoTaskItem.isDone: Boolean get() = status?.isDone == true

fun DomainTodoTaskItem.isOverdue(now: LocalDateTime = LocalDateTime.now()): Boolean =
    isTodoOverdue(task.dueDate, task.dueTime, isDone, now)

/** Порядок статусов на экранах: завершающие всегда в конце, дальше по позиции. */
fun List<DomainTodoStatus>.sortedForDisplay(): List<DomainTodoStatus> =
    sortedWith(compareBy<DomainTodoStatus>({ it.isDone }, { it.position }, { it.id }))

/** Секция задач одного статуса. */
data class TodoStatusGroup(
    val status: DomainTodoStatus,
    val tasks: List<DomainTodoTaskItem>
)

/**
 * Группирует задачи по статусам в порядке [sortedForDisplay]; пустые секции не попадают.
 * Задача с неизвестным статусом показывается в «В планах» (или в первом статусе, если его нет).
 * Внутри секции — по позиции, затем по id.
 */
fun groupTasksByStatus(
    statuses: List<DomainTodoStatus>,
    tasks: List<DomainTodoTaskItem>
): List<TodoStatusGroup> {
    val sorted = statuses.sortedForDisplay()
    if (sorted.isEmpty()) return emptyList()
    val known = sorted.map { it.id }.toSet()
    val fallbackId = (sorted.firstOrNull { it.defaultCode == TodoDefaultStatus.PLANNED } ?: sorted.first()).id
    val byStatus = tasks.groupBy { if (it.task.statusId in known) it.task.statusId else fallbackId }
    return sorted.mapNotNull { status ->
        byStatus[status.id]
            ?.sortedWith(compareBy({ it.task.position }, { it.task.id }))
            ?.let { TodoStatusGroup(status, it) }
    }
}

/** Задача верхнего уровня и её подзадачи — строка списка и строки с отступом под ней. */
data class TodoTaskNode(
    val item: DomainTodoTaskItem,
    val subtasks: List<DomainTodoTaskItem>
)

/**
 * Собирает плоский список задач списка (верхний уровень + подзадачи) в дерево. И задачи,
 * и подзадачи — по позиции, затем по id. Подзадача, чьего родителя нет в [items],
 * показывается как задача верхнего уровня, чтобы не пропасть с экрана.
 */
fun buildTodoTaskTree(items: List<DomainTodoTaskItem>): List<TodoTaskNode> {
    val order = compareBy<DomainTodoTaskItem>({ it.task.position }, { it.task.id })
    val ids = items.map { it.task.id }.toSet()
    val (roots, children) = items.partition { it.task.parentId == null || it.task.parentId !in ids }
    val byParent = children.groupBy { it.task.parentId }
    return roots.sortedWith(order).map { root ->
        TodoTaskNode(root, byParent[root.task.id].orEmpty().sortedWith(order))
    }
}

/** Подсписок с деревом его задач; [section] = null — задачи «без подсписка». */
data class TodoSectionNodes(
    val section: DomainTodoSection?,
    val nodes: List<TodoTaskNode>
)

/**
 * Раскладывает дерево задач ([buildTodoTaskTree]) по подспискам: первой идёт группа «без
 * подсписка» (всегда, даже пустая), затем все [sections] по позиции, затем по id — тоже и пустые.
 * Подзадачи остаются под родителем, их собственный `sectionId` не учитывается. Задача с
 * неизвестным подсписком попадает в «без подсписка», чтобы не пропасть с экрана.
 */
fun buildTodoSectionTree(
    sections: List<DomainTodoSection>,
    items: List<DomainTodoTaskItem>
): List<TodoSectionNodes> {
    val sorted = sections.sortedWith(compareBy({ it.position }, { it.id }))
    val known = sorted.map { it.id }.toSet()
    val bySection = buildTodoTaskTree(items).groupBy { node -> node.item.task.sectionId?.takeIf { it in known } }
    return listOf(TodoSectionNodes(null, bySection[null].orEmpty())) +
        sorted.map { TodoSectionNodes(it, bySection[it.id].orEmpty()) }
}
