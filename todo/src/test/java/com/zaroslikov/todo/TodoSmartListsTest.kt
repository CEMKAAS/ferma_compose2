package com.zaroslikov.todo

import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoSmartHeader
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.domain.model.inSmartList
import com.zaroslikov.todo.domain.model.todoIncompleteCountByList
import com.zaroslikov.todo.domain.model.todoSmartCount
import com.zaroslikov.todo.domain.model.todoSmartSections
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TodoSmartListsTest {

    /** 15.03.2026 14:30 — «сегодня» во всех тестах. */
    private val now = LocalDateTime.of(2026, 3, 15, 14, 30)

    private val planned = DomainTodoStatus(id = 1, idPT = 1, title = "", defaultCode = TodoDefaultStatus.PLANNED)
    private val done = DomainTodoStatus(id = 3, idPT = 1, title = "", defaultCode = TodoDefaultStatus.DONE, isDone = true)

    private fun task(
        id: Long,
        listId: Long = 1,
        due: String? = null,
        time: String? = null,
        isDone: Boolean = false,
        parentId: Long? = null,
        parentTitle: String? = null,
        position: Int = 0,
        completedAt: String? = null,
        title: String = "Задача $id"
    ) = DomainTodoProjectTask(
        item = DomainTodoTaskItem(
            task = DomainTodoTask(
                id = id,
                listId = listId,
                parentId = parentId,
                title = title,
                statusId = if (isDone) done.id else planned.id,
                dueDate = due,
                dueTime = time,
                position = position,
                completedAt = completedAt
            ),
            status = if (isDone) done else planned
        ),
        listTitle = "Список $listId",
        listColorIndex = listId.toInt(),
        parentTitle = parentTitle
    )

    @Test
    fun `код умного списка читается обратно, неизвестный — null`() {
        TodoSmartList.entries.forEach { assertEquals(it, TodoSmartList.fromCode(it.code)) }
        assertEquals(listOf("today", "scheduled", "all", "completed"), TodoSmartList.entries.map { it.code })
        assertNull(TodoSmartList.fromCode("future"))
        assertNull(TodoSmartList.fromCode(null))
    }

    @Test
    fun `сегодня — незавершённые с сегодняшней датой независимо от времени`() {
        assertTrue(task(1, due = "2026-03-15").inSmartList(TodoSmartList.TODAY, now))
        assertFalse(task(2, due = "2026-03-10").inSmartList(TodoSmartList.TODAY, now))
        assertFalse(task(3, due = "2026-03-16").inSmartList(TodoSmartList.TODAY, now))
        assertFalse(task(4).inSmartList(TodoSmartList.TODAY, now))
        assertFalse(task(5, due = "2026-03-15", isDone = true).inSmartList(TodoSmartList.TODAY, now))
        // Неразборчивая дата не роняет экран и не считается сроком
        assertFalse(task(6, due = "мусор").inSmartList(TodoSmartList.TODAY, now))
        assertTrue(task(7, due = "2026-03-15", time = "14:30").inSmartList(TodoSmartList.TODAY, now))
        assertTrue(task(8, due = "2026-03-15", time = "14:29").inSmartList(TodoSmartList.TODAY, now))
    }

    @Test
    fun `запланировано, все и завершено`() {
        val withDue = task(1, due = "2026-04-01")
        val noDue = task(2)
        val finished = task(3, due = "2026-03-01", isDone = true)

        assertTrue(withDue.inSmartList(TodoSmartList.SCHEDULED, now))
        assertFalse(noDue.inSmartList(TodoSmartList.SCHEDULED, now))
        assertFalse(finished.inSmartList(TodoSmartList.SCHEDULED, now))

        assertTrue(withDue.inSmartList(TodoSmartList.ALL, now))
        assertTrue(noDue.inSmartList(TodoSmartList.ALL, now))
        assertFalse(finished.inSmartList(TodoSmartList.ALL, now))

        assertTrue(finished.inSmartList(TodoSmartList.COMPLETED, now))
        assertFalse(noDue.inSmartList(TodoSmartList.COMPLETED, now))
    }

    @Test
    fun `число на плитке считает и подзадачи и совпадает со строками экрана`() {
        val tasks = listOf(
            task(1, due = "2026-03-15"),
            task(2, due = "2026-03-15", parentId = 1, parentTitle = "Задача 1"),
            task(3, due = "2026-03-20"),
            task(4),
            task(5, isDone = true)
        )
        TodoSmartList.entries.forEach { kind ->
            val rows = todoSmartSections(tasks, kind, now).sumOf { it.tasks.size }
            assertEquals(kind.name, rows, todoSmartCount(tasks, kind, now))
        }
        assertEquals(2, todoSmartCount(tasks, TodoSmartList.TODAY, now))
        assertEquals(3, todoSmartCount(tasks, TodoSmartList.SCHEDULED, now))
        assertEquals(4, todoSmartCount(tasks, TodoSmartList.ALL, now))
        assertEquals(1, todoSmartCount(tasks, TodoSmartList.COMPLETED, now))
    }

    @Test
    fun `оптимистичная отметка учитывается в счётчиках`() {
        val tasks = listOf(task(1, due = "2026-03-15"), task(2, due = "2026-03-15"))
        val overrides = mapOf(1L to true)
        val isDone: (DomainTodoProjectTask) -> Boolean = { overrides[it.id] ?: (it.item.task.statusId == done.id) }

        assertEquals(1, todoSmartCount(tasks, TodoSmartList.TODAY, now, isDone))
        assertEquals(1, todoSmartCount(tasks, TodoSmartList.COMPLETED, now, isDone))
    }

    @Test
    fun `сегодня — плоский список по времени без задач прошлых дней`() {
        val tasks = listOf(
            task(1, due = "2026-03-15", time = "18:00"),
            task(2, due = "2026-03-15"),
            task(3, due = "2026-03-15", time = "09:00"),
            task(4, due = "2026-03-12")
        )

        val sections = todoSmartSections(tasks, TodoSmartList.TODAY, now)

        assertEquals(1, sections.size)
        assertNull(sections.single().header)
        assertEquals(listOf(2L, 3L, 1L), sections.single().tasks.map { it.id })
        assertEquals(3, todoSmartCount(tasks, TodoSmartList.TODAY, now))
    }

    @Test
    fun `запланировано — группы просрочено, сегодня, завтра и дальше по датам`() {
        val tasks = listOf(
            task(1, due = "2026-03-20"),
            task(2, due = "2026-03-16", time = "10:00"),
            task(3, due = "2026-03-15", time = "20:00"),
            task(4, due = "2026-03-01"),
            task(5, due = "2026-03-17"),
            task(6, due = "2026-03-16", time = "08:00"),
            task(7, due = "2026-03-15", time = "10:00")
        )

        val sections = todoSmartSections(tasks, TodoSmartList.SCHEDULED, now)

        assertEquals(
            listOf(
                TodoSmartHeader.Overdue,
                TodoSmartHeader.Today,
                TodoSmartHeader.Tomorrow,
                TodoSmartHeader.Day("2026-03-17"),
                TodoSmartHeader.Day("2026-03-20")
            ),
            sections.map { it.header }
        )
        assertEquals(listOf(4L, 7L), sections[0].tasks.map { it.id })
        assertEquals(listOf(3L), sections[1].tasks.map { it.id })
        assertEquals(listOf(6L, 2L), sections[2].tasks.map { it.id })
    }

    @Test
    fun `все — по спискам, задача и сразу её подзадачи`() {
        val tasks = listOf(
            task(1, listId = 1, position = 0),
            task(2, listId = 1, position = 1),
            task(3, listId = 1, position = 0, parentId = 1, parentTitle = "Задача 1"),
            task(4, listId = 2, position = 0),
            task(5, listId = 1, isDone = true, position = 2)
        )

        val sections = todoSmartSections(tasks, TodoSmartList.ALL, now)

        assertEquals(
            listOf(TodoSmartHeader.ListTitle(1, "Список 1", 1), TodoSmartHeader.ListTitle(2, "Список 2", 2)),
            sections.map { it.header }
        )
        assertEquals(listOf(1L, 3L, 2L), sections[0].tasks.map { it.id })
        assertEquals(listOf(4L), sections[1].tasks.map { it.id })
    }

    @Test
    fun `завершено — по спискам, сначала недавно завершённые`() {
        val tasks = listOf(
            task(1, listId = 1, isDone = true, completedAt = "2026-03-10T10:00"),
            task(2, listId = 1, isDone = true, completedAt = "2026-03-14T09:00"),
            task(3, listId = 2, isDone = true, completedAt = "2026-03-01T09:00"),
            task(4, listId = 1)
        )

        val sections = todoSmartSections(tasks, TodoSmartList.COMPLETED, now)

        assertEquals(listOf(1L, 2L), sections.map { (it.header as TodoSmartHeader.ListTitle).listId })
        assertEquals(listOf(2L, 1L), sections[0].tasks.map { it.id })
        assertEquals(listOf(3L), sections[1].tasks.map { it.id })
    }

    @Test
    fun `только что отмеченная задача удерживается на экране`() {
        val tasks = listOf(task(1, due = "2026-03-15", time = "14:30", isDone = true), task(2, due = "2026-03-15"))

        val sections = todoSmartSections(tasks, TodoSmartList.TODAY, now, keep = { it.id == 1L })

        assertEquals(listOf(1L, 2L), sections.single().tasks.map { it.id }.sorted())

        val afterDeadline = todoSmartSections(tasks, TodoSmartList.TODAY, now.plusMinutes(1), keep = { it.id == 1L })

        assertEquals(listOf(2L, 1L), afterDeadline.single().tasks.map { it.id })

        val nextDay = todoSmartSections(tasks, TodoSmartList.TODAY, now.toLocalDate().plusDays(1).atStartOfDay(), keep = { it.id == 1L })

        assertTrue(nextDay.isEmpty())
    }

    @Test
    fun `пустой умный список — без секций`() {
        assertTrue(todoSmartSections(listOf(task(1)), TodoSmartList.TODAY, now).isEmpty())
    }

    @Test
    fun `незавершённые по спискам`() {
        val tasks = listOf(
            task(1, listId = 1),
            task(2, listId = 1, parentId = 1),
            task(3, listId = 1, isDone = true),
            task(4, listId = 2, isDone = true)
        )

        assertEquals(mapOf(1L to 2), todoIncompleteCountByList(tasks))
    }
}
