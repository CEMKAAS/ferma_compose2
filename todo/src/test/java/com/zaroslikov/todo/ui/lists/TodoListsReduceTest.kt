package com.zaroslikov.todo.ui.lists

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.domain.model.TodoSmartList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TodoListsReduceTest {

    private val now = LocalDateTime.of(2026, 3, 15, 14, 30)
    private val planned = DomainTodoStatus(id = 1, idPT = 1, title = "", defaultCode = TodoDefaultStatus.PLANNED)
    private val done = DomainTodoStatus(id = 3, idPT = 1, title = "", defaultCode = TodoDefaultStatus.DONE, isDone = true)

    private fun list(id: Long, title: String = "Список $id", color: Int = 0) =
        DomainTodoList(id = id, idPT = 1, title = title, colorIndex = color)

    private fun task(id: Long, listId: Long = 1, due: String? = null, isDone: Boolean = false) =
        DomainTodoProjectTask(
            item = DomainTodoTaskItem(
                task = DomainTodoTask(id = id, listId = listId, title = "Задача $id", statusId = 1, dueDate = due),
                status = if (isDone) done else planned
            ),
            listTitle = "Список $listId",
            listColorIndex = 0
        )

    private fun loaded(lists: List<DomainTodoList>, tasks: List<DomainTodoProjectTask> = emptyList()) =
        todoListsReduce(TodoListsState(), TodoListsIntent.DataLoaded(lists, tasks, now))

    @Test
    fun `загрузка снимает индикатор и считает плитки и списки`() {
        val state = loaded(
            listOf(list(1), list(2)),
            listOf(task(1, due = "2026-03-15"), task(2, due = "2026-03-20"), task(3, listId = 2), task(4, isDone = true))
        )

        assertFalse(state.isLoading)
        assertEquals(listOf(1L, 2L), state.lists.map { it.id })
        assertEquals(1, state.smartCounts[TodoSmartList.TODAY])
        assertEquals(2, state.smartCounts[TodoSmartList.SCHEDULED])
        assertEquals(3, state.smartCounts[TodoSmartList.ALL])
        assertEquals(1, state.smartCounts[TodoSmartList.COMPLETED])
        assertEquals(mapOf(1L to 2, 2L to 1), state.listCounts)
    }

    @Test
    fun `без списков плитки показывают нули`() {
        val state = loaded(emptyList())

        TODO_SMART_TILES.forEach { assertEquals(0, state.smartCounts[it]) }
        assertTrue(state.listCounts.isEmpty())
    }

    @Test
    fun `новый список получает следующий цвет палитры`() {
        val state = todoListsReduce(loaded(listOf(list(1), list(2))), TodoListsIntent.OpenCreate)

        assertEquals(true, state.form?.isNew)
        assertEquals(2, state.form?.colorIndex)
        assertEquals("", state.form?.title)
    }

    @Test
    fun `пустое название не закрывает шторку и показывает ошибку`() {
        val opened = todoListsReduce(loaded(emptyList()), TodoListsIntent.OpenCreate)
        val typed = todoListsReduce(opened, TodoListsIntent.FormTitleChanged("   "))

        val state = todoListsReduce(typed, TodoListsIntent.SaveForm)

        assertEquals(true, state.form?.titleError)
    }

    @Test
    fun `ввод названия снимает ошибку, сохранение закрывает шторку`() {
        val invalid = todoListsReduce(todoListsReduce(loaded(emptyList()), TodoListsIntent.OpenCreate), TodoListsIntent.SaveForm)
        val typed = todoListsReduce(invalid, TodoListsIntent.FormTitleChanged("Посевная"))
        assertEquals(false, typed.form?.titleError)

        val state = todoListsReduce(typed, TodoListsIntent.SaveForm)
        assertNull(state.form)
    }

    @Test
    fun `правка подставляет название и цвет списка`() {
        val state = todoListsReduce(loaded(emptyList()), TodoListsIntent.OpenEdit(list(5, "Ремонт", color = 4)))

        assertEquals(5L, state.form?.id)
        assertEquals("Ремонт", state.form?.title)
        assertEquals(4, state.form?.colorIndex)

        val recolored = todoListsReduce(state, TodoListsIntent.FormColorChanged(6))
        assertEquals(6, recolored.form?.colorIndex)
    }

    @Test
    fun `подтверждение удаления убирает список с задачами и закрывает диалог`() {
        val base = loaded(listOf(list(1), list(2)), listOf(task(1, listId = 1), task(2, listId = 2)))
        val requested = todoListsReduce(base, TodoListsIntent.RequestDelete(list(1)))
        assertEquals(1L, requested.listToDelete?.id)

        val state = todoListsReduce(requested, TodoListsIntent.ConfirmDelete)
        assertNull(state.listToDelete)
        assertEquals(listOf(2L), state.lists.map { it.id })
        assertEquals(1, state.smartCounts[TodoSmartList.ALL])
    }

    @Test
    fun `отмена удаления оставляет список`() {
        val requested = todoListsReduce(loaded(listOf(list(1))), TodoListsIntent.RequestDelete(list(1)))

        val state = todoListsReduce(requested, TodoListsIntent.DismissDelete)
        assertNull(state.listToDelete)
        assertEquals(1, state.lists.size)
    }

    /** Списки по позиции: [pinned] — id закреплённых. */
    private fun ordered(vararg ids: Long, pinned: Set<Long> = emptySet()) =
        ids.mapIndexed { position, id -> list(id).copy(position = position, isPinned = id in pinned) }

    @Test
    fun `закрепление уводит список в плитки и обратно, позиция не меняется`() {
        val base = loaded(ordered(1, 2, 3))

        val pinned = todoListsReduce(base, TodoListsIntent.SetPinned(2, true))
        assertEquals(listOf(2L), pinned.pinnedLists.map { it.id })
        assertEquals(listOf(1L, 3L), pinned.unpinnedLists.map { it.id })
        assertEquals(1, pinned.lists.first { it.id == 2L }.position)

        val unpinned = todoListsReduce(pinned, TodoListsIntent.SetPinned(2, false))
        assertTrue(unpinned.pinnedLists.isEmpty())
        assertEquals(listOf(1L, 2L, 3L), unpinned.unpinnedLists.map { it.id })
    }

    @Test
    fun `закреплённые плитки идут по позиции списка`() {
        val state = loaded(ordered(1, 2, 3, 4, pinned = setOf(4, 2)))

        assertEquals(listOf(2L, 4L), state.pinnedLists.map { it.id })
        assertEquals(listOf(1L, 3L), state.unpinnedLists.map { it.id })
    }

    @Test
    fun `перенос переставляет списки и нумерует позиции подряд`() {
        val base = loaded(ordered(1, 2, 3, 4))

        val state = todoListsReduce(base, TodoListsIntent.DropList(listId = 4, index = 0))
        assertEquals(listOf(4L, 1L, 2L, 3L), state.lists.map { it.id })
        assertEquals(listOf(0, 1, 2, 3), state.lists.map { it.position })

        val down = todoListsReduce(base, TodoListsIntent.DropList(listId = 1, index = 3))
        assertEquals(listOf(2L, 3L, 4L, 1L), down.lists.map { it.id })
    }

    @Test
    fun `перенос среди незакреплённых обходит закреплённые`() {
        // 2 закреплён: в «Моих списках» 1, 3, 4; 4 ставится перед 3
        val base = loaded(ordered(1, 2, 3, 4, pinned = setOf(2)))

        val state = todoListsReduce(base, TodoListsIntent.DropList(listId = 4, index = 1))
        assertEquals(listOf(1L, 4L, 3L), state.unpinnedLists.map { it.id })
        assertEquals(listOf(2L), state.pinnedLists.map { it.id })
        assertEquals(listOf(0, 1, 2, 3), state.lists.map { it.position })
    }

    @Test
    fun `неизвестный список переносом ничего не меняет`() {
        val base = loaded(ordered(1, 2, 3, pinned = setOf(2)))

        assertEquals(base.lists, todoListsReduce(base, TodoListsIntent.DropList(listId = 99, index = 0)).lists)
    }

    // Закреплённые 2, 4, 6 вперемешку с незакреплёнными 1, 3, 5
    private val mixed = ordered(1, 2, 3, 4, 5, 6, pinned = setOf(2, 4, 6))

    @Test
    fun `закреплённая плитка переносится вперёд среди закреплённых`() {
        val state = todoListsReduce(loaded(mixed), TodoListsIntent.DropList(listId = 6, index = 0))

        assertEquals(listOf(6L, 2L, 4L), state.pinnedLists.map { it.id })
        assertEquals(listOf(0, 1, 2, 3, 4, 5), state.lists.map { it.position })
    }

    @Test
    fun `закреплённая плитка переносится назад среди закреплённых`() {
        val middle = todoListsReduce(loaded(mixed), TodoListsIntent.DropList(listId = 2, index = 1))
        assertEquals(listOf(4L, 2L, 6L), middle.pinnedLists.map { it.id })

        val last = todoListsReduce(loaded(mixed), TodoListsIntent.DropList(listId = 2, index = 2))
        assertEquals(listOf(4L, 6L, 2L), last.pinnedLists.map { it.id })
    }

    @Test
    fun `место плитки за границами прижимается к краю`() {
        val toEnd = todoListsReduce(loaded(mixed), TodoListsIntent.DropList(listId = 4, index = 99))
        assertEquals(listOf(2L, 6L, 4L), toEnd.pinnedLists.map { it.id })

        val toStart = todoListsReduce(loaded(mixed), TodoListsIntent.DropList(listId = 4, index = -1))
        assertEquals(listOf(4L, 2L, 6L), toStart.pinnedLists.map { it.id })

        // На своё же место — порядок прежний
        val same = todoListsReduce(loaded(mixed), TodoListsIntent.DropList(listId = 2, index = 0))
        assertEquals(loaded(mixed).lists, same.lists)
    }

    @Test
    fun `перестановка закреплённых не сдвигает незакреплённые относительно друг друга`() {
        listOf(0, 1, 2).forEach { index ->
            listOf(2L, 4L, 6L).forEach { id ->
                val state = todoListsReduce(loaded(mixed), TodoListsIntent.DropList(listId = id, index = index))
                assertEquals(listOf(1L, 3L, 5L), state.unpinnedLists.map { it.id })
                assertEquals(listOf(0, 1, 2, 3, 4, 5), state.lists.map { it.position })
            }
        }
    }

    @Test
    fun `ошибка записи возвращает порядок закреплённых`() {
        val base = loaded(mixed)
        val moved = todoListsReduce(base, TodoListsIntent.DropList(listId = 6, index = 0))

        val state = todoListsReduce(moved, TodoListsIntent.ListsChangeFailed(base.lists))
        assertEquals(base.lists, state.lists)
        assertEquals(listOf(2L, 4L, 6L), state.pinnedLists.map { it.id })
    }

    @Test
    fun `ошибка записи возвращает порядок и закрепление`() {
        val base = loaded(ordered(1, 2, 3))
        val moved = todoListsReduce(base, TodoListsIntent.DropList(listId = 3, index = 0))
        val pinned = todoListsReduce(moved, TodoListsIntent.SetPinned(1, true))

        val state = todoListsReduce(pinned, TodoListsIntent.ListsChangeFailed(base.lists))
        assertEquals(base.lists, state.lists)
    }

    @Test
    fun `форма списка хранит значок`() {
        val created = todoListsReduce(loaded(emptyList()), TodoListsIntent.OpenCreate)
        assertEquals(TodoListIcon.LIST, created.form?.icon)

        val picked = todoListsReduce(created, TodoListsIntent.FormIconChanged(TodoListIcon.EGGS))
        assertEquals(TodoListIcon.EGGS, picked.form?.icon)

        val edit = todoListsReduce(
            loaded(listOf(list(5))),
            TodoListsIntent.OpenEdit(list(5).copy(icon = TodoListIcon.ANIMALS))
        )
        assertEquals(TodoListIcon.ANIMALS, edit.form?.icon)
    }
}
