package com.zaroslikov.todo.domain.model

// Перестановка задач и подсписков переносом: одни и те же правила у экрана (оптимистично)
// и у репозитория. Порядок на экране — по позиции, затем по id (`buildTodoSectionTree`)

private val TASK_ORDER = compareBy<DomainTodoTaskItem>({ it.task.position }, { it.task.id })

/**
 * Список с [item] на месте [index] среди остальных элементов: элемент убирается со старого
 * места (если был) и вставляется заново; [index] ограничивается границами списка.
 */
fun <T> List<T>.todoMoved(item: T, index: Int): List<T> {
    val rest = filter { it != item }
    return rest.toMutableList().apply { add(index.coerceIn(0, rest.size), item) }
}

/**
 * Новый порядок [ids] по [orderedIds]: сначала id из [orderedIds] (без повторов и чужих),
 * затем не упомянутые — в прежнем порядке.
 */
fun todoOrdered(ids: List<Long>, orderedIds: List<Long>): List<Long> {
    val known = ids.toSet()
    val first = orderedIds.filter { it in known }.distinct()
    return first + (ids - first.toSet())
}

/**
 * Место вставки среди всех элементов [full] по месту [visibleIndex] среди видимых [visible]
 * (часть [full] без скрытых фильтрами и завершённых): перед видимым элементом с этим номером,
 * дальше последнего видимого — сразу за ним, без видимых — в конец. Переносимый элемент
 * в обоих списках уже не учитывается.
 */
fun todoInsertIndex(full: List<Long>, visible: List<Long>, visibleIndex: Int): Int {
    visible.getOrNull(visibleIndex.coerceAtLeast(0))?.let { anchor ->
        return full.indexOf(anchor).takeIf { it >= 0 } ?: full.size
    }
    val last = visible.lastOrNull() ?: return full.size
    return (full.indexOf(last) + 1).takeIf { it > 0 } ?: full.size
}

/**
 * id задач верхнего уровня подсписка [sectionId] по порядку экрана. Задача с подсписком
 * не из [knownSections] считается «без подсписка», как в `buildTodoSectionTree`.
 */
fun List<DomainTodoTaskItem>.todoTopLevelIds(sectionId: Long?, knownSections: Set<Long>): List<Long> =
    filter { it.task.parentId == null && it.task.sectionId?.takeIf { id -> id in knownSections } == sectionId }
        .sortedWith(TASK_ORDER)
        .map { it.task.id }

/** id подзадач [parentId] по порядку экрана. */
fun List<DomainTodoTaskItem>.todoSubtaskIds(parentId: Long): List<Long> =
    filter { it.task.parentId == parentId }.sortedWith(TASK_ORDER).map { it.task.id }

/**
 * Переносит задачу верхнего уровня [taskId] в подсписок [sectionId] (null — «без подсписка»)
 * на место [index] среди его задач верхнего уровня (без самой задачи; ограничивается).
 * Позиции задач верхнего уровня целевого и исходного подсписков перенумеровываются подряд
 * с нуля — так же, как `TodoTaskRepository.moveTask`. Подзадача, неизвестная задача или
 * подсписок не из [knownSections] — список без изменений.
 */
fun List<DomainTodoTaskItem>.withTodoTaskMoved(
    taskId: Long,
    sectionId: Long?,
    index: Int,
    knownSections: Set<Long>
): List<DomainTodoTaskItem> {
    val moved = firstOrNull { it.task.id == taskId } ?: return this
    if (moved.task.parentId != null || (sectionId != null && sectionId !in knownSections)) return this
    val from = moved.task.sectionId?.takeIf { it in knownSections }
    val target = todoTopLevelIds(sectionId, knownSections).todoMoved(taskId, index)
    val source = if (from == sectionId) emptyList() else todoTopLevelIds(from, knownSections) - taskId
    val positions = source.withIndex().associate { it.value to it.index } +
        target.withIndex().associate { it.value to it.index }
    return map { item ->
        val position = positions[item.task.id] ?: return@map item
        val section = if (item.task.id == taskId) sectionId else item.task.sectionId
        if (item.task.position == position && item.task.sectionId == section) item
        else item.copy(task = item.task.copy(position = position, sectionId = section))
    }
}

/**
 * Переставляет подзадачу [taskId] на место [index] среди подзадач её родителя; позиции
 * подзадач перенумеровываются подряд с нуля. Задача верхнего уровня — без изменений.
 */
fun List<DomainTodoTaskItem>.withTodoSubtaskMoved(taskId: Long, index: Int): List<DomainTodoTaskItem> {
    val parentId = firstOrNull { it.task.id == taskId }?.task?.parentId ?: return this
    val positions = todoSubtaskIds(parentId).todoMoved(taskId, index).withIndex().associate { it.value to it.index }
    return map { item ->
        val position = positions[item.task.id]
        if (position == null || position == item.task.position) item
        else item.copy(task = item.task.copy(position = position))
    }
}

/** id подсписков по порядку экрана. */
fun List<DomainTodoSection>.todoSectionIds(): List<Long> =
    sortedWith(compareBy({ it.position }, { it.id })).map { it.id }

/** Переставляет подсписок [sectionId] на место [index]; позиции — подряд с нуля. */
fun List<DomainTodoSection>.withTodoSectionMoved(sectionId: Long, index: Int): List<DomainTodoSection> {
    if (none { it.id == sectionId }) return this
    val positions = todoSectionIds().todoMoved(sectionId, index).withIndex().associate { it.value to it.index }
    return map { section ->
        val position = positions.getValue(section.id)
        if (position == section.position) section else section.copy(position = position)
    }
}
