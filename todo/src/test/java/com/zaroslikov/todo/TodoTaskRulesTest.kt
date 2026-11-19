package com.zaroslikov.todo

import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.buildTodoTaskTree
import com.zaroslikov.todo.domain.model.groupTasksByStatus
import com.zaroslikov.todo.domain.model.isOverdue
import com.zaroslikov.todo.domain.model.isTodoOverdue
import com.zaroslikov.todo.domain.model.sortedForDisplay
import com.zaroslikov.todo.domain.model.todoNowIso
import com.zaroslikov.todo.domain.model.todoNowTime
import com.zaroslikov.todo.domain.model.todoToday
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TodoTaskRulesTest {

    private val now = LocalDateTime.of(2026, 3, 15, 14, 30, 45)

    private val planned = DomainTodoStatus(id = 1, idPT = 1, title = "", position = 0, defaultCode = TodoDefaultStatus.PLANNED)
    private val inWork = DomainTodoStatus(id = 2, idPT = 1, title = "", position = 1, defaultCode = TodoDefaultStatus.IN_PROGRESS)
    private val done = DomainTodoStatus(id = 3, idPT = 1, title = "", position = 2, defaultCode = TodoDefaultStatus.DONE, isDone = true)
    private val custom = DomainTodoStatus(id = 4, idPT = 1, title = "Ждёт", position = 3)

    private fun item(id: Long, status: DomainTodoStatus?, statusId: Long = status?.id ?: 99, position: Int = 0) =
        DomainTodoTaskItem(
            task = DomainTodoTask(id = id, listId = 1, title = "T$id", statusId = statusId, position = position),
            status = status
        )

    @Test
    fun dates_formatAsIso() {
        assertEquals("2026-03-15", todoToday(now))
        assertEquals("14:30", todoNowTime(now))
        assertEquals("2026-03-15T14:30", todoNowIso(now))
    }

    @Test
    fun overdue_byDateAndTime() {
        assertTrue(isTodoOverdue("2026-03-14", null, isDone = false, now = now))
        assertFalse(isTodoOverdue("2026-03-16", "00:00", isDone = false, now = now))
        // Сегодня без времени — не просрочено до конца дня
        assertFalse(isTodoOverdue("2026-03-15", null, isDone = false, now = now))
        assertTrue(isTodoOverdue("2026-03-15", "14:29", isDone = false, now = now))
        // Минута срока ещё идёт
        assertFalse(isTodoOverdue("2026-03-15", "14:30", isDone = false, now = now))
        assertFalse(isTodoOverdue("2026-03-15", "18:00", isDone = false, now = now))
    }

    @Test
    fun overdue_neverForDoneOrWithoutDate() {
        assertFalse(isTodoOverdue("2020-01-01", null, isDone = true, now = now))
        assertFalse(isTodoOverdue(null, "10:00", isDone = false, now = now))
        assertFalse(isTodoOverdue("мусор", null, isDone = false, now = now))
        val doneItem = item(1, done).let { it.copy(task = it.task.copy(dueDate = "2020-01-01")) }
        assertFalse(doneItem.isOverdue(now))
    }

    @Test
    fun statuses_doneAlwaysLast() {
        val sorted = listOf(done, custom, inWork, planned).sortedForDisplay()
        assertEquals(listOf(planned, inWork, custom, done), sorted)
    }

    @Test
    fun grouping_followsStatusOrderAndSkipsEmpty() {
        val tasks = listOf(
            item(1, done),
            item(2, planned, position = 1),
            item(3, planned, position = 0),
            item(4, custom)
        )
        val groups = groupTasksByStatus(listOf(done, custom, inWork, planned), tasks)
        assertEquals(listOf(planned, custom, done), groups.map { it.status })
        assertEquals(listOf(3L, 2L), groups.first().tasks.map { it.task.id })
    }

    @Test
    fun grouping_unknownStatusGoesToPlanned() {
        val groups = groupTasksByStatus(listOf(inWork, planned), listOf(item(1, null, statusId = 42)))
        assertEquals(planned, groups.single().status)
        assertEquals(1L, groups.single().tasks.single().task.id)
    }

    @Test
    fun grouping_withoutStatusesIsEmpty() {
        assertTrue(groupTasksByStatus(emptyList(), listOf(item(1, planned))).isEmpty())
    }

    private fun sub(id: Long, parentId: Long, position: Int) =
        item(id, planned, position = position).let { it.copy(task = it.task.copy(parentId = parentId)) }

    @Test
    fun tree_subtasksUnderParentByPosition() {
        val tree = buildTodoTaskTree(
            listOf(sub(21, parentId = 2, position = 1), item(2, planned, position = 1), sub(20, parentId = 2, position = 0), item(1, planned, position = 0))
        )
        assertEquals(listOf(1L, 2L), tree.map { it.item.task.id })
        assertTrue(tree.first().subtasks.isEmpty())
        assertEquals(listOf(20L, 21L), tree[1].subtasks.map { it.task.id })
    }

    @Test
    fun tree_orphanSubtaskBecomesRoot() {
        val tree = buildTodoTaskTree(listOf(sub(5, parentId = 99, position = 3), item(1, planned, position = 0)))
        assertEquals(listOf(1L, 5L), tree.map { it.item.task.id })
    }
}
