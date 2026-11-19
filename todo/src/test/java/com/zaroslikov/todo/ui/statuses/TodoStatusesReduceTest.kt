package com.zaroslikov.todo.ui.statuses

import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoStatusesReduceTest {

    private val planned = DomainTodoStatus(1, 1, "planned", 1, 0, TodoDefaultStatus.PLANNED, false)
    private val done = DomainTodoStatus(3, 1, "done", 2, 2, TodoDefaultStatus.DONE, true)
    private val review = DomainTodoStatus(4, 1, "На проверке", 5, 3, null, false)

    private fun loaded() =
        todoStatusesReduce(TodoStatusesState(), TodoStatusesIntent.DataLoaded(listOf(done, review, planned)))

    @Test
    fun `статусы сортируются для показа, завершающие в конце`() {
        val state = loaded()

        assertFalse(state.isLoading)
        assertEquals(listOf(1L, 4L, 3L), state.statuses.map { it.id })
    }

    @Test
    fun `у дефолтного статуса название не редактируется, цвет — да`() {
        val opened = todoStatusesReduce(loaded(), TodoStatusesIntent.OpenEdit(planned))
        assertEquals(false, opened.form?.titleEditable)
        assertEquals(planned, opened.editingStatus)

        val typed = todoStatusesReduce(opened, TodoStatusesIntent.FormTitleChanged("Другое"))
        assertEquals("planned", typed.form?.title)

        val recolored = todoStatusesReduce(typed, TodoStatusesIntent.FormColorChanged(6))
        assertEquals(6, recolored.form?.colorIndex)

        // Название не проверяется — сохраняется только цвет
        assertNull(todoStatusesReduce(recolored, TodoStatusesIntent.SaveForm).form)
    }

    @Test
    fun `пользовательский статус переименовывается, пустое название — ошибка`() {
        val opened = todoStatusesReduce(loaded(), TodoStatusesIntent.OpenEdit(review))
        assertEquals(true, opened.form?.titleEditable)

        val invalid = todoStatusesReduce(
            todoStatusesReduce(opened, TodoStatusesIntent.FormTitleChanged(" ")),
            TodoStatusesIntent.SaveForm
        )
        assertEquals(true, invalid.form?.titleError)

        val saved = todoStatusesReduce(
            todoStatusesReduce(invalid, TodoStatusesIntent.FormTitleChanged("Ждёт")),
            TodoStatusesIntent.SaveForm
        )
        assertNull(saved.form)
    }

    @Test
    fun `создание открывает пустую форму`() {
        val state = todoStatusesReduce(loaded(), TodoStatusesIntent.OpenCreate)

        assertEquals(true, state.form?.isNew)
        assertEquals(3, state.form?.colorIndex)
        assertNull(state.editingStatus)
    }

    @Test
    fun `дефолтный статус удалить нельзя`() {
        val state = todoStatusesReduce(loaded(), TodoStatusesIntent.RequestDelete(done))

        assertNull(state.statusToDelete)
    }

    @Test
    fun `удаление пользовательского статуса с подтверждением`() {
        val requested = todoStatusesReduce(loaded(), TodoStatusesIntent.RequestDelete(review))
        assertEquals(review, requested.statusToDelete)

        val state = todoStatusesReduce(requested, TodoStatusesIntent.ConfirmDelete)
        assertNull(state.statusToDelete)
        assertTrue(state.statuses.none { it.id == review.id })

        val dismissed = todoStatusesReduce(requested, TodoStatusesIntent.DismissDelete)
        assertNull(dismissed.statusToDelete)
        assertEquals(3, dismissed.statuses.size)
    }
}
