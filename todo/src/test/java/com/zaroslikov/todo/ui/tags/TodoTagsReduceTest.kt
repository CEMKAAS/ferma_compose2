package com.zaroslikov.todo.ui.tags

import com.zaroslikov.todo.domain.model.DomainTodoTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class TodoTagsReduceTest {

    private val urgent = DomainTodoTag(id = 7, idPT = 1, title = "Срочно", colorIndex = 4)
    private val buy = DomainTodoTag(id = 8, idPT = 1, title = "Закупка", colorIndex = 1)

    private fun loaded() = todoTagsReduce(TodoTagsState(), TodoTagsIntent.DataLoaded(listOf(buy, urgent)))

    @Test
    fun `загрузка снимает индикатор`() {
        val state = loaded()

        assertFalse(state.isLoading)
        assertEquals(listOf(8L, 7L), state.tags.map { it.id })
    }

    @Test
    fun `новый тег — пустая форма со следующим цветом`() {
        val state = todoTagsReduce(loaded(), TodoTagsIntent.OpenCreate)

        assertEquals(true, state.form?.isNew)
        assertEquals("", state.form?.title)
        assertEquals(2, state.form?.colorIndex)
    }

    @Test
    fun `пустое название — ошибка поля, шторка остаётся`() {
        val state = todoTagsReduce(todoTagsReduce(loaded(), TodoTagsIntent.OpenCreate), TodoTagsIntent.SaveForm)

        assertEquals(true, state.form?.titleError)
    }

    @Test
    fun `правка тега подставляет его данные, сохранение закрывает шторку`() {
        val opened = todoTagsReduce(loaded(), TodoTagsIntent.OpenEdit(urgent))
        assertEquals(7L, opened.form?.id)
        assertEquals("Срочно", opened.form?.title)
        assertEquals(4, opened.form?.colorIndex)

        val edited = todoTagsReduce(
            todoTagsReduce(opened, TodoTagsIntent.FormTitleChanged("Очень срочно")),
            TodoTagsIntent.FormColorChanged(0)
        )
        assertEquals("Очень срочно", edited.form?.title)
        assertEquals(0, edited.form?.colorIndex)

        assertNull(todoTagsReduce(edited, TodoTagsIntent.SaveForm).form)
    }

    @Test
    fun `закрытие шторки без сохранения`() {
        val state = todoTagsReduce(todoTagsReduce(loaded(), TodoTagsIntent.OpenCreate), TodoTagsIntent.CloseForm)

        assertNull(state.form)
    }

    @Test
    fun `удаление тега с подтверждением`() {
        val requested = todoTagsReduce(loaded(), TodoTagsIntent.RequestDelete(urgent))
        assertEquals(urgent, requested.tagToDelete)

        val state = todoTagsReduce(requested, TodoTagsIntent.ConfirmDelete)
        assertNull(state.tagToDelete)
        assertEquals(listOf(8L), state.tags.map { it.id })
    }
}
