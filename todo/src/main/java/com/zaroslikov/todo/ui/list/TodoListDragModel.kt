package com.zaroslikov.todo.ui.list

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect

// Перенос удержанием — чистая часть: что переносится, куда упадёт, как выглядит список во время
// переноса. Места ([TodoDropTarget.index]) — среди видимых элементов без переносимого, как
// у интентов Drop*: редьюсер сам переводит их в места среди всех задач

/** Что переносится. */
internal sealed interface TodoDragItem {
    /** Ключ элемента в LazyColumn вида «список» (у задач — id, у заголовков — [sectionKey]). */
    val key: Any

    /** Задача верхнего уровня — вместе с подзадачами. */
    data class Task(val taskId: Long) : TodoDragItem {
        override val key: Any get() = taskId
    }

    /** Подзадача — только среди подзадач своего родителя. */
    data class Subtask(val taskId: Long, val parentId: Long) : TodoDragItem {
        override val key: Any get() = taskId
    }

    data class Section(val sectionId: Long) : TodoDragItem {
        override val key: Any get() = sectionKey(sectionId)
    }
}

/** Куда упадёт: подсписок [sectionId] (у подзадачи — подсписок родителя, у подсписка — не важен) и место. */
internal data class TodoDropTarget(val sectionId: Long?, val index: Int)

internal fun sectionKey(sectionId: Long?): String = "section_$sectionId"

/** Видимые задачи верхнего уровня подсписка без [exclude] — места для [TodoDragItem.Task]. */
internal fun TodoListState.visibleCards(sectionId: Long?, exclude: Long): List<Long> =
    groups.firstOrNull { it.section?.id == sectionId }?.cards.orEmpty().map { it.item.task.id } - exclude

/** Видимые подзадачи [parentId] без [exclude] — места для [TodoDragItem.Subtask]. */
internal fun TodoListState.visibleSubtasks(parentId: Long, exclude: Long): List<Long> =
    rows.filter { it.isSubtask && it.item.task.parentId == parentId }.map { it.item.task.id } - exclude

/** Видимые подсписки без [exclude] — места для [TodoDragItem.Section]. */
internal fun TodoListState.visibleSections(exclude: Long): List<Long> =
    groups.mapNotNull { it.section?.id } - exclude

/** Где элемент стоит сейчас — с этой цели начинается перенос; null — элемента на экране нет. */
internal fun TodoListState.dragOrigin(item: TodoDragItem): TodoDropTarget? = when (item) {
    is TodoDragItem.Task -> groups.firstOrNull { group -> group.cards.any { it.item.task.id == item.taskId } }
        ?.let { group ->
            TodoDropTarget(group.section?.id, group.cards.indexOfFirst { it.item.task.id == item.taskId })
        }

    is TodoDragItem.Subtask -> rows.filter { it.isSubtask && it.item.task.parentId == item.parentId }
        .indexOfFirst { it.item.task.id == item.taskId }.takeIf { it >= 0 }
        ?.let { index ->
            val sectionId = groups.firstOrNull { group -> group.cards.any { it.item.task.id == item.parentId } }?.section?.id
            TodoDropTarget(sectionId, index)
        }

    is TodoDragItem.Section -> groups.mapNotNull { it.section?.id }.indexOf(item.sectionId)
        .takeIf { it >= 0 }?.let { TodoDropTarget(null, it) }
}

/** Интент броска; null — элемент вернулся на своё место. */
internal fun todoDropIntent(item: TodoDragItem, origin: TodoDropTarget?, target: TodoDropTarget): TodoListIntent? =
    if (target == origin) null
    else when (item) {
        is TodoDragItem.Task -> TodoListIntent.DropTask(item.taskId, target.sectionId, target.index)
        is TodoDragItem.Subtask -> TodoListIntent.DropSubtask(item.taskId, target.index)
        is TodoDragItem.Section -> TodoListIntent.DropSection(item.sectionId, target.index)
    }

/** Элемент LazyColumn вида «список». */
internal sealed interface TodoListEntry {
    val key: Any

    /** Заголовок подсписка; [group] у переносимых подсписков уже свёрнут. */
    data class Header(val group: TodoListGroup) : TodoListEntry {
        override val key: Any get() = sectionKey(group.section?.id)
    }

    data class Task(val row: TodoListRow, val sectionId: Long?) : TodoListEntry {
        override val key: Any get() = row.item.task.id
    }

    /** Пунктирная строка в конце подсписка. */
    data class NewTask(val sectionId: Long?) : TodoListEntry {
        override val key: Any get() = "new_$sectionId"
    }

    data class NewSubtask(val parentId: Long) : TodoListEntry {
        override val key: Any get() = "new_subtask_$parentId"
    }

    /** Строка ввода новой задачи. */
    data class Editor(val sectionId: Long?, val parentId: Long? = null) : TodoListEntry {
        override val key: Any get() = KEY_NEW_EDITOR
    }

    companion object {
        const val KEY_NEW_EDITOR = "new_editor"
    }
}

/**
 * Строки вида «список». Во время переноса переносимый элемент стоит на месте [target]
 * (экран рисует его пустым промежутком, а сам элемент — «призраком» под пальцем):
 * задача — без своих подзадач, подзадача — среди подзадач родителя; при переносе подсписка
 * видны только заголовки подсписков, все свёрнутые, как в «Напоминаниях».
 */
internal fun todoListEntries(state: TodoListState, drag: TodoDragItem?, target: TodoDropTarget?): List<TodoListEntry> {
    if (drag is TodoDragItem.Section) {
        val headers = state.groups.filter { it.section != null }
            .map { TodoListEntry.Header(it.copy(rows = emptyList(), isCollapsed = true)) }
        val dragged = headers.firstOrNull { it.group.section?.id == drag.sectionId }
        return if (dragged == null || target == null) headers
        else (headers - dragged).toMutableList().apply { add(target.index.coerceIn(0, size), dragged) }
    }
    val editor = state.editor?.takeIf { it.taskId == null }
    val draggedRow = (drag as? TodoDragItem.Task)?.let { task ->
        state.groups.flatMap { it.cards }.firstOrNull { it.item.task.id == task.taskId }
    }
    val entries = mutableListOf<TodoListEntry>()
    state.groups.forEach { group ->
        val sectionId = group.section?.id
        if (group.section != null) entries += TodoListEntry.Header(group)
        var rows = group.rows.map { TodoListEntry.Task(it, sectionId) }
        when (drag) {
            is TodoDragItem.Task -> {
                rows = rows.filter { it.row.item.task.id != drag.taskId && it.row.item.task.parentId != drag.taskId }
                if (draggedRow != null && target != null && target.sectionId == sectionId) {
                    // Перед задачей верхнего уровня с номером index, иначе — в конец
                    val at = rows.withIndex().filter { !it.value.row.isSubtask }.getOrNull(target.index)?.index ?: rows.size
                    rows = rows.toMutableList().apply { add(at, TodoListEntry.Task(draggedRow, sectionId)) }
                }
            }

            is TodoDragItem.Subtask -> {
                val moved = rows.firstOrNull { it.row.item.task.id == drag.taskId }
                val parent = rows.indexOfFirst { it.row.item.task.id == drag.parentId }
                if (moved != null && parent >= 0 && target != null) {
                    rows = rows - moved
                    val siblings = rows.count { it.row.isSubtask && it.row.item.task.parentId == drag.parentId }
                    rows = rows.toMutableList().apply { add(parent + 1 + target.index.coerceIn(0, siblings), moved) }
                }
            }

            else -> Unit
        }
        rows.forEachIndexed { index, entry ->
            entries += entry
            val parentId = entry.row.item.task.parentId ?: entry.row.item.task.id
            val lastForParent = rows.getOrNull(index + 1)?.row?.item?.task?.parentId != parentId
            if (drag == null && lastForParent) {
                if (editor?.parentId == parentId) entries += TodoListEntry.Editor(sectionId, parentId)
                else if (state.editor?.taskId == parentId) entries += TodoListEntry.NewSubtask(parentId)
            }
        }
        if (editor != null && editor.parentId == null && editor.sectionId == sectionId) entries += TodoListEntry.Editor(sectionId)
        else if (!group.isCollapsed) entries += TodoListEntry.NewTask(sectionId)
    }
    return entries
}

/** Видимый элемент списка: ключ и границы по вертикали. */
internal data class TodoItemBounds(val key: Any, val top: Float, val bottom: Float) {
    val center: Float get() = (top + bottom) / 2
}

/**
 * Цель переноса в виде «список» по центру «призрака» [y] (в координатах списка): элемент под
 * ним решает, куда упадёт переносимый. Задача: верхняя половина строки — перед её задачей
 * верхнего уровня, нижняя или подзадача — после (вместе с подзадачами); заголовок — в начало
 * подсписка, пунктирная строка — в конец. Подзадача — только среди подзадач родителя.
 * Подсписок — перед или после другого заголовка. Пустое место и сам промежуток цель не меняют.
 */
internal fun todoListDropTarget(
    state: TodoListState,
    entries: List<TodoListEntry>,
    bounds: List<TodoItemBounds>,
    drag: TodoDragItem,
    y: Float,
    current: TodoDropTarget
): TodoDropTarget {
    val hit = bounds.firstOrNull { y >= it.top && y < it.bottom && it.key != drag.key } ?: return current
    val entry = entries.firstOrNull { it.key == hit.key } ?: return current
    val after = y >= hit.center
    return when (drag) {
        is TodoDragItem.Task -> when (entry) {
            is TodoListEntry.Header -> TodoDropTarget(entry.group.section?.id, 0)
            is TodoListEntry.NewTask -> TodoDropTarget(entry.sectionId, state.visibleCards(entry.sectionId, drag.taskId).size)
            is TodoListEntry.Task -> {
                val task = entry.row.item.task
                val index = state.visibleCards(entry.sectionId, drag.taskId).indexOf(task.parentId ?: task.id)
                if (index < 0) current
                else TodoDropTarget(entry.sectionId, if (entry.row.isSubtask || after) index + 1 else index)
            }

            is TodoListEntry.Editor, is TodoListEntry.NewSubtask -> current
        }

        is TodoDragItem.Subtask -> {
            val task = (entry as? TodoListEntry.Task)?.row?.item?.task
            when {
                task == null -> current
                task.id == drag.parentId -> current.copy(index = 0)
                task.parentId == drag.parentId -> {
                    val index = state.visibleSubtasks(drag.parentId, drag.taskId).indexOf(task.id)
                    if (index < 0) current else current.copy(index = if (after) index + 1 else index)
                }

                else -> current
            }
        }

        is TodoDragItem.Section -> {
            val sectionId = (entry as? TodoListEntry.Header)?.group?.section?.id
            val index = sectionId?.let { state.visibleSections(drag.sectionId).indexOf(it) } ?: -1
            if (index < 0) current else current.copy(index = if (after) index + 1 else index)
        }
    }
}

/**
 * Место карточки в столбце по центру «призрака» [y] (в координатах списка столбца): перед первой
 * видимой карточкой, чей центр ниже; ниже всех — после последней видимой; видимых нет — в конец.
 * [visible] — пары «место среди карточек столбца — границы» по порядку сверху вниз.
 */
internal fun todoColumnDropIndex(visible: List<Pair<Int, TodoItemBounds>>, cardCount: Int, y: Float): Int =
    visible.firstOrNull { y < it.second.center }?.first
        ?: visible.lastOrNull()?.first?.plus(1)
        ?: cardCount

/**
 * Место плитки в сетке по центру «призрака» [point]: номер ячейки, в которую он попал. [slots] —
 * границы ячеек по порядку плиток (у ячейки — место, а не плитка: при перестановке плитки
 * меняются, ячейки стоят), null — ячейка ещё не измерена. Мимо ячеек (зазор между плитками,
 * пустая половина последнего ряда, вне сетки) — null: цель не меняется.
 */
internal fun todoGridDropIndex(slots: List<Rect?>, point: Offset): Int? =
    slots.indexOfFirst { it != null && it.contains(point) }.takeIf { it >= 0 }
