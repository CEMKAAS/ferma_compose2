package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoListViewMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Редьюсер экрана списка: перенос удержанием, откат, переименование заголовка на месте. */
class TodoListReorderReduceTest {

    private val list = DomainTodoList(id = 10, idPT = 1, title = "Огород")
    private val planned = DomainTodoStatus(1, 1, "planned", 1, 0, TodoDefaultStatus.PLANNED, false)
    private val done = DomainTodoStatus(3, 1, "done", 2, 2, TodoDefaultStatus.DONE, true)

    private val greenhouse = DomainTodoSection(id = 5, listId = 10, title = "Теплица", position = 0)
    private val beds = DomainTodoSection(id = 6, listId = 10, title = "Грядки", position = 1)

    private fun item(
        id: Long,
        sectionId: Long? = null,
        parentId: Long? = null,
        status: DomainTodoStatus = planned,
        position: Int = id.toInt()
    ) = DomainTodoTaskItem(
        task = DomainTodoTask(
            id = id, listId = list.id, parentId = parentId, sectionId = sectionId,
            title = "T$id", statusId = status.id, position = position
        ),
        status = status
    )

    // Без подсписка: 1; «Теплица»: 2 (подзадачи 21, 22), 3 (завершена, скрыта), 4; «Грядки»: 7
    private val tasks = listOf(
        item(1),
        item(2, sectionId = 5, position = 0),
        item(21, parentId = 2, position = 0),
        item(22, parentId = 2, position = 1),
        item(3, sectionId = 5, status = done, position = 1),
        item(4, sectionId = 5, position = 2),
        item(7, sectionId = 6, position = 0)
    )

    private fun loaded(mode: TodoListViewMode = TodoListViewMode.LIST) = todoListReduce(
        TodoListState(),
        TodoListIntent.DataLoaded(list.copy(viewMode = mode), tasks, emptyList(), listOf(beds, greenhouse))
    )

    private fun TodoListState.reduce(vararg intents: TodoListIntent): TodoListState =
        intents.fold(this) { acc, intent -> todoListReduce(acc, intent) }

    private fun TodoListState.groupIds(): List<Pair<Long?, List<Long>>> =
        groups.map { group -> group.section?.id to group.rows.map { it.item.task.id } }

    private fun TodoListState.position(id: Long): Int = tasks.single { it.task.id == id }.task.position

    @Test
    fun `перенос внутри подсписка — сразу виден, скрытая завершённая остаётся на месте`() {
        // 4 — перед 2 (видимые в «Теплице»: 2, 4)
        val moved = loaded().reduce(TodoListIntent.DropTask(4, 5, 0))
        assertEquals(listOf(null to listOf(1L), 5L to listOf(4L, 2L, 21L, 22L), 6L to listOf(7L)), moved.groupIds())
        // Порядок всех задач подсписка: 4, 2, 3 — позиции подряд
        assertEquals(listOf(0, 1, 2), listOf(4L, 2L, 3L).map { moved.position(it) })

        // Вниз за последнюю видимую — сразу после неё
        val down = loaded().reduce(TodoListIntent.DropTask(2, 5, 1))
        assertEquals(listOf(4L, 2L, 21L, 22L), down.groupIds()[1].second)
        assertEquals(listOf(0, 1, 2), listOf(3L, 4L, 2L).map { down.position(it) })
    }

    @Test
    fun `перенос между подсписками, в «без подсписка» и из него`() {
        val between = loaded().reduce(TodoListIntent.DropTask(2, 6, 0))
        assertEquals(listOf(null to listOf(1L), 5L to listOf(4L), 6L to listOf(2L, 21L, 22L, 7L)), between.groupIds())
        assertEquals(6L, between.tasks.single { it.task.id == 2L }.task.sectionId)

        val out = loaded().reduce(TodoListIntent.DropTask(7, null, 1))
        assertEquals(listOf(null to listOf(1L, 7L), 5L to listOf(2L, 21L, 22L, 4L), 6L to emptyList()), out.groupIds())

        val into = loaded().reduce(TodoListIntent.DropTask(1, 5, 99))
        assertEquals(listOf(null to emptyList(), 5L to listOf(2L, 21L, 22L, 4L, 1L), 6L to listOf(7L)), into.groupIds())
    }

    @Test
    fun `перенос подзадачи — только среди своих, задача верхнего уровня не принимается`() {
        val moved = loaded().reduce(TodoListIntent.DropSubtask(22, 0))
        assertEquals(listOf(2L, 22L, 21L, 4L), moved.groupIds()[1].second)
        assertEquals(loaded(), loaded().reduce(TodoListIntent.DropSubtask(2, 0)))
    }

    @Test
    fun `перестановка подсписков — сразу в обоих видах`() {
        val moved = loaded().reduce(TodoListIntent.DropSection(6, 0))
        assertEquals(listOf(null, 6L, 5L), moved.groups.map { it.section?.id })
        val columns = loaded(TodoListViewMode.COLUMNS).reduce(TodoListIntent.DropSection(5, 5))
        assertEquals(listOf(null, 6L, 5L), columns.columns.map { it.section?.id })
    }

    @Test
    fun `ошибка записи откатывает порядок к снимку, остальное не трогает`() {
        val before = loaded()
        val moved = before.reduce(
            TodoListIntent.DropTask(2, 6, 0),
            TodoListIntent.DropSection(6, 0),
            TodoListIntent.ToggleDone(4)
        )
        val restored = moved.reduce(TodoListIntent.ReorderFailed(before.tasks, before.sections))
        assertEquals(before.groupIds().map { it.first }, restored.groupIds().map { it.first })
        assertEquals(before.tasks.map { it.task }, restored.tasks.map { it.task })
        // Оптимистичная отметка осталась
        assertEquals(true, restored.doneOverrides[4L])
    }

    @Test
    fun `тап по заголовку в виде «список» — правка на месте, пустое возвращает старое`() {
        val editing = loaded().reduce(TodoListIntent.StartNew, TodoListIntent.StartRenameSection(5))
        assertNull(editing.editor)
        assertEquals(TodoSectionEditor(5, "Теплица"), editing.sectionEditor)
        assertNull(editing.sectionForm)

        val renamed = editing.reduce(TodoListIntent.SectionTitleChanged("Парник "), TodoListIntent.SectionTitleFinish())
        assertNull(renamed.sectionEditor)
        assertEquals("Парник", renamed.groups.single { it.section?.id == 5L }.section?.title)

        val empty = loaded().reduce(
            TodoListIntent.StartRenameSection(5),
            TodoListIntent.SectionTitleChanged(" "),
            TodoListIntent.SectionTitleFinish()
        )
        assertEquals("Теплица", empty.sections.single { it.id == 5L }.title)
    }

    @Test
    fun `начало переноса закрывает ввод и применяет начатое название`() {
        val typing = loaded().reduce(TodoListIntent.StartRenameSection(6), TodoListIntent.SectionTitleChanged("Клумбы"))
        val dragging = typing.reduce(TodoListIntent.DragStarted)
        assertNull(dragging.sectionEditor)
        assertEquals("Клумбы", dragging.sections.single { it.id == 6L }.title)

        val adding = loaded().reduce(TodoListIntent.StartNew, TodoListIntent.EditorTextChanged("Полить"), TodoListIntent.DragStarted)
        assertNull(adding.editor)
    }

    @Test
    fun `перемещение через меню — в конец подсписка, в тот же подсписок — ничего`() {
        val moved = loaded().reduce(TodoListIntent.MoveTask(1, 5))
        assertEquals(listOf(2L, 21L, 22L, 4L, 1L), moved.groupIds()[1].second)
        assertEquals(loaded(), loaded().reduce(TodoListIntent.MoveTask(2, 5)))
    }
}
