package com.zaroslikov.todo.ui.smart

import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoSmartHeader
import com.zaroslikov.todo.domain.model.TodoSmartList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TodoSmartReduceTest {

    @Test
    fun `подтверждение сразу очищает строку и ошибка возвращает введённое`() {
        var state = todoSmartReduce(loaded(TodoSmartList.TODAY), TodoSmartIntent.OpenNewTask)
        state = todoSmartReduce(state, TodoSmartIntent.NewTaskNoteChanged("Утром\nДо жары"))
        state = todoSmartReduce(state, TodoSmartIntent.NewTaskTitleChanged("Полить"))
        val next = todoSmartReduce(state, submitNewTask)
        assertEquals(TodoSmartTaskDraft(), next.newTask)

        val failed = TodoSmartIntent.NewTaskAddFailed(checkNotNull(state.newTask))
        val retry = todoSmartReduce(next, failed)
        assertEquals("Утром\nДо жары", retry.newTask?.note)
        assertEquals("Полить", retry.newTask?.title)
        val typing = todoSmartReduce(next, TodoSmartIntent.NewTaskTitleChanged("Собрать"))
        assertEquals("Собрать", todoSmartReduce(typing, failed).newTask?.title)
    }

    @Test
    fun `сохранение при завершении ввода или открытии подробностей закрывает строку`() {
        val opened = todoSmartReduce(loaded(TodoSmartList.TODAY), TodoSmartIntent.OpenNewTask)
        val written = todoSmartReduce(opened, TodoSmartIntent.NewTaskTitleChanged("Полить"))

        for (action in listOf(NewTaskAction.FINISH, NewTaskAction.OPEN_DETAILS)) {
            assertNull(todoSmartReduce(written, submitNewTask.copy(action = action)).newTask)
        }
    }

    private val now = LocalDateTime.of(2026, 3, 15, 14, 30)
    private val planned = DomainTodoStatus(id = 1, idPT = 1, title = "", defaultCode = TodoDefaultStatus.PLANNED)
    private val done = DomainTodoStatus(id = 3, idPT = 1, title = "", defaultCode = TodoDefaultStatus.DONE, isDone = true)
    private val submitNewTask = TodoSmartIntent.SubmitNewTask("Новый список")

    private fun task(id: Long, due: String? = null, isDone: Boolean = false, listId: Long = 1) = DomainTodoProjectTask(
        item = DomainTodoTaskItem(
            task = DomainTodoTask(id = id, listId = listId, title = "Задача $id", statusId = 1, dueDate = due),
            status = if (isDone) done else planned
        ),
        listTitle = "Список $listId",
        listColorIndex = 0
    )

    private fun loaded(kind: TodoSmartList, vararg tasks: DomainTodoProjectTask) =
        todoSmartReduce(TodoSmartState(kind = kind), TodoSmartIntent.DataLoaded(tasks.toList(), now))

    private val TodoSmartState.rowIds: List<Long> get() = sections.flatMap { it.tasks }.map { it.id }

    @Test
    fun `загрузка отбирает задачи вида`() {
        val state = loaded(TodoSmartList.TODAY, task(1, due = "2026-03-15"), task(2, due = "2026-03-16"), task(3))

        assertFalse(state.isLoading)
        assertEquals(listOf(1L), state.rowIds)
    }

    @Test
    fun `отмеченная в «Сегодня» задача видна закрашенной, пока не истечёт пауза`() {
        val base = loaded(TodoSmartList.TODAY, task(1, due = "2026-03-15"))

        val toggled = todoSmartReduce(base, TodoSmartIntent.ToggleDone(1))
        assertTrue(1L in toggled.lingering)
        assertEquals(listOf(1L), toggled.rowIds)
        assertTrue(toggled.isDone(toggled.tasks.single()))

        val hidden = todoSmartReduce(toggled, TodoSmartIntent.HideLingering(1))
        assertTrue(hidden.rowIds.isEmpty())
    }

    @Test
    fun `повторный тап в паузе отменяет отметку и оставляет строку`() {
        val toggled = todoSmartReduce(loaded(TodoSmartList.ALL, task(1)), TodoSmartIntent.ToggleDone(1))

        val undone = todoSmartReduce(toggled, TodoSmartIntent.ToggleDone(1))

        assertFalse(1L in undone.lingering)
        assertFalse(undone.isDone(undone.tasks.single()))
        assertEquals(listOf(1L), undone.rowIds)
    }

    @Test
    fun `снятие отметки в «Завершено» убирает задачу после паузы`() {
        val base = loaded(TodoSmartList.COMPLETED, task(1, isDone = true), task(2))
        assertEquals(listOf(1L), base.rowIds)

        val toggled = todoSmartReduce(base, TodoSmartIntent.ToggleDone(1))
        assertEquals(listOf(1L), toggled.rowIds)
        assertFalse(toggled.isDone(toggled.tasks.first()))

        // БД вернула «В планах» — оптимистичная отметка больше не нужна, строка держится паузой
        val synced = todoSmartReduce(toggled, TodoSmartIntent.DataLoaded(listOf(task(1), task(2)), now))
        assertTrue(synced.doneOverrides.isEmpty())
        assertEquals(listOf(1L), synced.rowIds)

        assertTrue(todoSmartReduce(synced, TodoSmartIntent.HideLingering(1)).rowIds.isEmpty())
    }

    @Test
    fun `ошибка записи откатывает отметку`() {
        val toggled = todoSmartReduce(loaded(TodoSmartList.ALL, task(1)), TodoSmartIntent.ToggleDone(1))

        val failed = todoSmartReduce(toggled, TodoSmartIntent.ToggleDoneFailed(1))

        assertTrue(failed.doneOverrides.isEmpty())
        assertTrue(failed.lingering.isEmpty())
        assertEquals(listOf(1L), failed.rowIds)
    }

    @Test
    fun `удалённая задача уходит и из паузы`() {
        val toggled = todoSmartReduce(loaded(TodoSmartList.ALL, task(1)), TodoSmartIntent.ToggleDone(1))

        val state = todoSmartReduce(toggled, TodoSmartIntent.DataLoaded(emptyList(), now))

        assertTrue(state.lingering.isEmpty())
        assertTrue(state.sections.isEmpty())
    }

    @Test
    fun `минута сменилась — задача на сегодня стала просроченной`() {
        val base = todoSmartReduce(
            TodoSmartState(kind = TodoSmartList.SCHEDULED),
            TodoSmartIntent.DataLoaded(
                listOf(task(1, due = "2026-03-16")),
                now
            )
        )
        assertEquals(TodoSmartHeader.Tomorrow, base.sections.single().header)

        val nextDay = todoSmartReduce(base, TodoSmartIntent.DataLoaded(base.tasks, now.plusDays(2)))
        assertEquals(TodoSmartHeader.Overdue, nextDay.sections.single().header)
    }

    @Test
    fun `новая задача доступна в Сегодня без списков`() {
        assertNull(todoSmartReduce(loaded(TodoSmartList.ALL), TodoSmartIntent.OpenNewTask).newTask)

        val emptyToday = todoSmartReduce(loaded(TodoSmartList.TODAY), TodoSmartIntent.OpenNewTask)
        assertNotNull(emptyToday.newTask)
    }

    @Test
    fun `подтверждение пустой строки завершает ввод`() {
        val opened = todoSmartReduce(loaded(TodoSmartList.TODAY), TodoSmartIntent.OpenNewTask)

        assertNull(todoSmartReduce(opened, submitNewTask).newTask)
        assertNull(todoSmartReduce(opened, submitNewTask.copy(action = NewTaskAction.FINISH)).newTask)
        assertNull(todoSmartReduce(opened, submitNewTask.copy(action = NewTaskAction.OPEN_DETAILS)).newTask)
    }
}
