package com.zaroslikov.todo.ui.list

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Перенос удержанием: строки списка во время переноса, цель под «призраком», место в столбце. */
class TodoListDragModelTest {

    private val list = DomainTodoList(id = 10, idPT = 1, title = "Огород")
    private val planned = DomainTodoStatus(1, 1, "planned", 1, 0, TodoDefaultStatus.PLANNED, false)

    private val greenhouse = DomainTodoSection(id = 5, listId = 10, title = "Теплица", position = 0)
    private val beds = DomainTodoSection(id = 6, listId = 10, title = "Грядки", position = 1, isCollapsed = true)

    private fun item(id: Long, sectionId: Long? = null, parentId: Long? = null) = DomainTodoTaskItem(
        task = DomainTodoTask(
            id = id, listId = list.id, parentId = parentId, sectionId = sectionId,
            title = "T$id", statusId = planned.id, position = id.toInt()
        ),
        status = planned
    )

    // Без подсписка: 1; «Теплица»: 2 (подзадача 21), 3; «Грядки» (свёрнут): 4
    private val state = todoListReduce(
        TodoListState(),
        TodoListIntent.DataLoaded(
            list,
            listOf(item(1), item(2, 5), item(21, parentId = 2), item(3, 5), item(4, 6)),
            emptyList(),
            listOf(greenhouse, beds)
        )
    )

    private fun List<TodoListEntry>.keys(): List<Any> = map { it.key }

    @Test
    fun `без переноса — заголовки, строки и пунктир в конце развёрнутых`() {
        assertEquals(
            listOf<Any>(1L, "new_null", "section_5", 2L, 21L, 3L, "new_5", "section_6"),
            todoListEntries(state, null, null).keys()
        )
    }

    @Test
    fun `редактор подзадачи заменяет пунктир после детей, а не новую задачу подсписка`() {
        val editing = todoListReduce(state, TodoListIntent.StartNewSubtask(2))
        val entries = todoListEntries(editing, null, null)
        assertEquals(
            listOf<Any>(1L, "new_null", "section_5", 2L, 21L, "new_editor", 3L, "new_5", "section_6"),
            entries.keys()
        )
        assertEquals(2L, entries.filterIsInstance<TodoListEntry.Editor>().single().parentId)
    }

    @Test
    fun `первый редактор подзадачи сразу под родителем, свёрнутые дети без пунктира`() {
        val editing = todoListReduce(state, TodoListIntent.StartNewSubtask(1))
        assertEquals(listOf<Any>(1L, "new_editor", "new_null"), todoListEntries(editing, null, null).keys().take(3))
        val collapsed = todoListReduce(state, TodoListIntent.ToggleCollapse(2))
        assertTrue(todoListEntries(collapsed, null, null).none { it is TodoListEntry.NewSubtask })
    }

    @Test
    fun `пустое поле подзадачи видно только у редактируемой основной задачи`() {
        for (parentId in listOf(1L, 2L)) {
            val editing = todoListReduce(state, TodoListIntent.StartRename(parentId))
            val entries = todoListEntries(editing, null, null)
            assertEquals(listOf(parentId), entries.filterIsInstance<TodoListEntry.NewSubtask>().map { it.parentId })
            val preceding = entries[entries.indexOfFirst { it is TodoListEntry.NewSubtask } - 1] as TodoListEntry.Task
            assertEquals(parentId, preceding.row.item.task.parentId ?: preceding.row.item.task.id)

            val finished = todoListReduce(editing, TodoListIntent.EditorFinish(parentId))
            assertTrue(todoListEntries(finished, null, null).none { it is TodoListEntry.NewSubtask })
        }

        val subtask = todoListReduce(state, TodoListIntent.StartRename(21L))
        assertTrue(todoListEntries(subtask, null, null).none { it is TodoListEntry.NewSubtask })
    }

    @Test
    fun `переход из основной задачи в поле подзадачи сохраняет родителя и не закрывается старым фокусом`() {
        val focused = todoListReduce(state, TodoListIntent.StartRename(2L))
        val adding = todoListReduce(focused, TodoListIntent.StartNewSubtask(2L))
        val afterFocusLoss = todoListReduce(adding, TodoListIntent.EditorFinish(2L))
        assertEquals(2L, afterFocusLoss.editor?.parentId)
        assertEquals(2L, todoListEntries(afterFocusLoss, null, null).filterIsInstance<TodoListEntry.Editor>().single().parentId)
        assertTrue(todoListEntries(afterFocusLoss, null, null).none { it is TodoListEntry.NewSubtask })

        val finished = todoListReduce(afterFocusLoss, TodoListIntent.EditorFinish(null))
        assertTrue(todoListEntries(finished, null, null).none { it is TodoListEntry.Editor || it is TodoListEntry.NewSubtask })
    }

    @Test
    fun `перенос задачи — без подзадач, на месте цели, в том числе в свёрнутом подсписке`() {
        val drag = TodoDragItem.Task(2)
        assertEquals(TodoDropTarget(5, 0), state.dragOrigin(drag))
        assertEquals(
            listOf<Any>(1L, "new_null", "section_5", 3L, 2L, "new_5", "section_6"),
            todoListEntries(state, drag, TodoDropTarget(5, 1)).keys()
        )
        assertEquals(
            listOf<Any>(1L, "new_null", "section_5", 3L, "new_5", "section_6", 2L),
            todoListEntries(state, drag, TodoDropTarget(6, 0)).keys()
        )
        assertEquals(
            listOf<Any>(2L, 1L, "new_null", "section_5", 3L, "new_5", "section_6"),
            todoListEntries(state, drag, TodoDropTarget(null, 0)).keys()
        )
    }

    @Test
    fun `перенос подсписка — только заголовки, все свёрнуты`() {
        val entries = todoListEntries(state, TodoDragItem.Section(5), TodoDropTarget(null, 1))
        assertEquals(listOf<Any>("section_6", "section_5"), entries.keys())
        assertTrue(entries.all { it is TodoListEntry.Header && it.group.isCollapsed })
    }

    @Test
    fun `цель под «призраком» в списке`() {
        val drag = TodoDragItem.Task(3)
        val entries = todoListEntries(state, drag, TodoDropTarget(5, 1))
        // Строки по 100 px подряд
        val bounds = entries.mapIndexed { i, entry -> TodoItemBounds(entry.key, i * 100f, i * 100f + 100f) }
        val start = TodoDropTarget(5, 1)
        // Верхняя половина задачи 1 — перед ней в «без подсписка»
        assertEquals(TodoDropTarget(null, 0), todoListDropTarget(state, entries, bounds, drag, 20f, start))
        // При переносе поля подзадач скрыты: пунктир «без подсписка» — в его конец;
        // заголовок — в начало подсписка
        assertEquals(TodoDropTarget(null, 1), todoListDropTarget(state, entries, bounds, drag, 150f, start))
        assertEquals(TodoDropTarget(5, 0), todoListDropTarget(state, entries, bounds, drag, 250f, start))
        // Подзадача — после её родителя
        assertEquals(TodoDropTarget(5, 1), todoListDropTarget(state, entries, bounds, drag, 410f, start))
        // Сам промежуток и пустое место цель не меняют
        val gap = entries.indexOfFirst { it.key == 3L }
        assertEquals(start, todoListDropTarget(state, entries, bounds, drag, gap * 100f + 10f, start))
        assertEquals(start, todoListDropTarget(state, entries, bounds, drag, 5000f, start))
    }

    @Test
    fun `бросок на своё место ничего не делает, иначе — интент`() {
        val origin = TodoDropTarget(5, 0)
        assertNull(todoDropIntent(TodoDragItem.Task(2), origin, origin))
        assertEquals(TodoListIntent.DropTask(2, 6, 0), todoDropIntent(TodoDragItem.Task(2), origin, TodoDropTarget(6, 0)))
        assertEquals(TodoListIntent.DropSection(5, 1), todoDropIntent(TodoDragItem.Section(5), TodoDropTarget(null, 0), TodoDropTarget(null, 1)))
        assertEquals(TodoListIntent.DropSubtask(21, 1), todoDropIntent(TodoDragItem.Subtask(21, 2), TodoDropTarget(5, 0), TodoDropTarget(5, 1)))
    }

    @Test
    fun `место карточки в столбце`() {
        val visible = listOf(0 to TodoItemBounds(10L, 0f, 100f), 1 to TodoItemBounds(11L, 110f, 210f))
        assertEquals(0, todoColumnDropIndex(visible, 2, 30f))
        assertEquals(1, todoColumnDropIndex(visible, 2, 60f))
        assertEquals(2, todoColumnDropIndex(visible, 2, 500f))
        assertEquals(3, todoColumnDropIndex(emptyList(), 3, 0f))
    }

    @Test
    fun `место плитки в сетке - ячейка под центром призрака`() {
        // Две колонки по 100 с зазором 10; третья ячейка — половинная последняя; четвёртая не измерена
        val slots = listOf(Rect(0f, 0f, 100f, 50f), Rect(110f, 0f, 210f, 50f), Rect(0f, 60f, 100f, 110f), null)

        assertEquals(0, todoGridDropIndex(slots, Offset(50f, 25f)))
        assertEquals(1, todoGridDropIndex(slots, Offset(150f, 10f)))
        assertEquals(2, todoGridDropIndex(slots, Offset(10f, 100f)))
        // Зазор, пустая половина последнего ряда, вне сетки — цель не меняется
        assertNull(todoGridDropIndex(slots, Offset(105f, 25f)))
        assertNull(todoGridDropIndex(slots, Offset(150f, 80f)))
        assertNull(todoGridDropIndex(slots, Offset(50f, -20f)))
    }
}
