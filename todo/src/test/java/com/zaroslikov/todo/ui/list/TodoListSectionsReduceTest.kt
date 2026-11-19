package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoListViewMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Редьюсер экрана списка: подсписки, сворачивание, перемещение и вид «столбцы». */
class TodoListSectionsReduceTest {

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
        title: String = "T$id"
    ) = DomainTodoTaskItem(
        task = DomainTodoTask(
            id = id, listId = list.id, parentId = parentId, sectionId = sectionId,
            title = title, statusId = status.id, position = id.toInt()
        ),
        status = status
    )

    // 1 — без подсписка; 2 (с подзадачей 21) и 3 — в «Теплице»; 4 (завершена) — в «Грядках»
    private val tasks = listOf(
        item(1),
        item(2, sectionId = 5, title = "Полить"),
        item(21, parentId = 2),
        item(3, sectionId = 5),
        item(4, sectionId = 6, status = done)
    )

    private fun loaded(sections: List<DomainTodoSection> = listOf(beds, greenhouse)) =
        todoListReduce(TodoListState(), TodoListIntent.DataLoaded(list, tasks, emptyList(), sections))

    private fun TodoListState.reduce(vararg intents: TodoListIntent): TodoListState =
        intents.fold(this) { acc, intent -> todoListReduce(acc, intent) }

    private fun TodoListState.groupIds(): List<Pair<Long?, List<Long>>> =
        groups.map { group -> group.section?.id to group.rows.map { it.item.task.id } }

    @Test
    fun `строки идут без подсписка, затем подсписки по позиции, подзадачи под родителем`() {
        val state = loaded()
        assertEquals(listOf(null to listOf(1L), 5L to listOf(2L, 21L, 3L), 6L to emptyList()), state.groupIds())
        assertEquals(listOf(1L, 2L, 21L, 3L), state.rows.map { it.item.task.id })
        // Карточки столбцов — только задачи верхнего уровня
        assertEquals(listOf(2L, 3L), state.groups[1].cards.map { it.item.task.id })
        assertEquals(2, state.groups[1].taskCount)
    }

    @Test
    fun `свёрнутый подсписок прячет строки, но считает задачи и оставляет карточки`() {
        val state = loaded().reduce(TodoListIntent.ToggleSectionCollapse(5))
        val group = state.groups[1]
        assertTrue(group.isCollapsed)
        assertEquals(emptyList<TodoListRow>(), group.rows)
        assertEquals(2, group.taskCount)
        assertEquals(listOf(1L), state.rows.map { it.item.task.id })
        assertTrue(state.sections.single { it.id == 5L }.isCollapsed)

        val expanded = state.reduce(TodoListIntent.ToggleSectionCollapse(5))
        assertFalse(expanded.groups[1].isCollapsed)
        assertEquals(listOf(1L, 2L, 21L, 3L), expanded.rows.map { it.item.task.id })
    }

    @Test
    fun `подзадачи карточки остаются в свёрнутом подсписке и скрываются шевроном родителя`() {
        val state = loaded(listOf(greenhouse.copy(isCollapsed = true), beds))
        assertEquals(listOf(2L, 21L, 3L), state.groups[1].cardRows.map { it.item.task.id })
        assertTrue(state.groups[1].rows.isEmpty())

        val collapsed = state.reduce(TodoListIntent.ToggleCollapse(2))
        assertEquals(listOf(2L, 3L), collapsed.groups[1].cardRows.map { it.item.task.id })
        assertTrue(collapsed.groups[1].cards.first().isCollapsed)

        val expanded = collapsed.reduce(TodoListIntent.ToggleCollapse(2))
        assertEquals(listOf(2L, 21L, 3L), expanded.groups[1].cardRows.map { it.item.task.id })
    }

    @Test
    fun `подзадачи карточки учитывают поиск отметку и удаление оптимистично`() {
        val state = loaded(listOf(greenhouse.copy(isCollapsed = true), beds))
            .reduce(TodoListIntent.Search("T21"))
        assertEquals(listOf(2L, 21L), state.groups.single { it.section?.id == 5L }.cardRows.map { it.item.task.id })

        val marked = state.reduce(TodoListIntent.ToggleDone(21))
        assertTrue(marked.groups.single { it.section?.id == 5L }.cardRows.last().isDone)
        val completed = marked.reduce(TodoListIntent.HideLingering(21))
        assertEquals(listOf(2L, 21L), completed.groups.single { it.section?.id == 5L }.cardRows.map { it.item.task.id })
        val cleared = completed.reduce(TodoListIntent.Search(""))
        assertEquals(listOf(2L, 3L), cleared.groups.single { it.section?.id == 5L }.cardRows.map { it.item.task.id })

        val deleted = state.reduce(TodoListIntent.RequestDeleteTask(21))
        assertTrue(deleted.groups.none { it.section?.id == 5L })
    }

    @Test
    fun `ввод подзадачи в столбце раскрывает родителя даже в свёрнутом подсписке`() {
        val state = loaded(listOf(greenhouse.copy(isCollapsed = true), beds))
            .reduce(TodoListIntent.ToggleCollapse(2), TodoListIntent.StartNewSubtask(2))
        assertEquals(TodoInlineEditor(null, "", sectionId = 5, parentId = 2), state.editor)
        assertEquals(listOf(2L, 21L, 3L), state.groups[1].cardRows.map { it.item.task.id })
        assertTrue(state.groups[1].isCollapsed)
    }

    @Test
    fun `свёрнутость приходит из БД`() {
        val state = loaded(listOf(greenhouse.copy(isCollapsed = true), beds))
        assertTrue(state.groups[1].isCollapsed)
    }

    @Test
    fun `новая задача в подсписке разворачивает его, Enter оставляет поле в том же подсписке`() {
        val state = loaded(listOf(greenhouse.copy(isCollapsed = true), beds))
            .reduce(TodoListIntent.StartNewInSection(5), TodoListIntent.EditorTextChanged("Рассада"))
        assertEquals(TodoInlineEditor(null, "Рассада", 5), state.editor)
        assertFalse(state.groups[1].isCollapsed)

        val submitted = state.reduce(TodoListIntent.EditorSubmit)
        assertEquals(TodoInlineEditor(null, "", 5), submitted.editor)

        // Обычный «+» — без подсписка
        assertEquals(TodoInlineEditor(null, ""), submitted.reduce(TodoListIntent.StartNew).editor)
        // Неизвестный подсписок — ничего
        assertEquals(submitted, submitted.reduce(TodoListIntent.StartNewInSection(99)))
    }

    @Test
    fun `удалённый в другом месте подсписок закрывает строку ввода`() {
        val state = loaded().reduce(TodoListIntent.StartNewInSection(6))
        val reloaded = todoListReduce(state, TodoListIntent.DataLoaded(list, tasks, emptyList(), listOf(greenhouse)))
        assertNull(reloaded.editor)
    }

    @Test
    fun `перемещение задачи в подсписок и обратно`() {
        val moved = loaded().reduce(TodoListIntent.MoveTask(1, 6))
        assertEquals(listOf(null to emptyList(), 5L to listOf(2L, 21L, 3L), 6L to listOf(1L)), moved.groupIds())

        val back = moved.reduce(TodoListIntent.MoveTask(2, null))
        assertEquals(listOf(2L, 21L), back.groups[0].rows.map { it.item.task.id })

        // Подзадачи и неизвестные подсписки не перемещаются
        assertEquals(moved, moved.reduce(TodoListIntent.MoveTask(21, 6)))
        assertEquals(moved, moved.reduce(TodoListIntent.MoveTask(3, 99)))
    }

    @Test
    fun `переключение вида закрывает строку ввода и меняет режим`() {
        val state = loaded().reduce(TodoListIntent.StartNew)
        val columns = state.reduce(TodoListIntent.ToggleViewMode)
        assertEquals(TodoListViewMode.COLUMNS, columns.viewMode)
        assertNull(columns.editor)
        assertEquals(TodoListViewMode.LIST, columns.reduce(TodoListIntent.ToggleViewMode).viewMode)
    }

    @Test
    fun `столбец «Без подсписка» — только если в нём есть задачи или подсписков нет`() {
        assertEquals(listOf(null, 5L, 6L), loaded().columns.map { it.section?.id })
        val emptyNoSection = loaded().reduce(TodoListIntent.MoveTask(1, 5))
        assertEquals(listOf(5L, 6L), emptyNoSection.columns.map { it.section?.id })
        val noSections = todoListReduce(TodoListState(), TodoListIntent.DataLoaded(list, emptyList(), emptyList()))
        assertEquals(listOf<Long?>(null), noSections.columns.map { it.section?.id })
    }

    @Test
    fun `удаление подсписка переводит задачи без подсписка`() {
        val asked = loaded().reduce(TodoListIntent.RequestDeleteSection(5))
        assertEquals(greenhouse, asked.sectionToDelete)
        val deleted = asked.reduce(TodoListIntent.ConfirmDeleteSection)
        assertNull(deleted.sectionToDelete)
        assertEquals(listOf(null to listOf(1L, 2L, 21L, 3L), 6L to emptyList()), deleted.groupIds())
    }

    @Test
    fun `шторка нового подсписка — пустое название даёт ошибку`() {
        val empty = loaded().reduce(TodoListIntent.OpenNewSection, TodoListIntent.SaveSectionForm)
        assertTrue(empty.sectionForm!!.titleError)
        val saved = empty.reduce(TodoListIntent.SectionFormTitleChanged("Сад"), TodoListIntent.SaveSectionForm)
        assertNull(saved.sectionForm)
    }

    @Test
    fun `новый столбец — название, затем сразу ввод первой задачи в нём`() {
        val naming = loaded().reduce(TodoListIntent.StartNew, TodoListIntent.StartNewColumn)
        // Ввод задачи и названия столбца одновременно не открыты
        assertNull(naming.editor)
        assertEquals(TodoSectionEditor(null, ""), naming.sectionEditor)

        val finished = naming.reduce(TodoListIntent.SectionTitleChanged("Сад\n"),TodoListIntent.SectionTitleFinish())
        assertNull(finished.sectionEditor)
        // Подсписок пишет ViewModel и сообщает его id
        val created = finished.reduce(TodoListIntent.ColumnCreated(7))
        assertEquals(TodoInlineEditor(null, "", 7), created.editor)

        // Выгрузка без нового подсписка (была до записи) строку ввода не закрывает
        val stale = todoListReduce(created, TodoListIntent.DataLoaded(list, tasks, emptyList(), listOf(greenhouse, beds)))
        assertEquals(TodoInlineEditor(null, "", 7), stale.editor)
        val garden = DomainTodoSection(id = 7, listId = 10, title = "Сад", position = 2)
        val fresh = todoListReduce(stale, TodoListIntent.DataLoaded(list, tasks, emptyList(), listOf(greenhouse, beds, garden)))
        assertEquals(TodoInlineEditor(null, "", 7), fresh.editor)
        assertEquals(listOf(null, 5L, 6L, 7L), fresh.columns.map { it.section?.id })
    }

    @Test
    fun `новый столбец — пустое название отменяет, начатый ввод не перебивается`() {
        val cancelled = loaded().reduce(TodoListIntent.StartNewColumn, TodoListIntent.SectionTitleChanged("  "), TodoListIntent.SectionTitleFinish())
        assertNull(cancelled.sectionEditor)
        assertEquals(loaded().sections, cancelled.sections)

        // Пока подсписок записывался, пользователь начал новую задачу в другом столбце
        val busy = loaded().reduce(TodoListIntent.StartNewInSection(5), TodoListIntent.ColumnCreated(7))
        assertEquals(TodoInlineEditor(null, "", 5), busy.editor)
    }

    @Test
    fun `переименование столбца на месте — сразу видно, пустое название возвращает старое`() {
        val editing = loaded().reduce(TodoListIntent.StartNewInSection(5), TodoListIntent.StartRenameSection(6))
        assertNull(editing.editor)
        assertEquals(TodoSectionEditor(6, "Грядки"), editing.sectionEditor)
        // Повторный тап по тому же названию поле не сбрасывает
        val typed = editing.reduce(TodoListIntent.SectionTitleChanged("Клумбы "))
        assertEquals(typed, typed.reduce(TodoListIntent.StartRenameSection(6)))

        val renamed = typed.reduce(TodoListIntent.SectionTitleFinish())
        assertNull(renamed.sectionEditor)
        assertEquals("Клумбы", renamed.sections.single { it.id == 6L }.title)
        assertEquals("Клумбы", renamed.groups.single { it.section?.id == 6L }.section?.title)

        val empty = loaded().reduce(TodoListIntent.StartRenameSection(6), TodoListIntent.SectionTitleChanged(""), TodoListIntent.SectionTitleFinish())
        assertEquals("Грядки", empty.sections.single { it.id == 6L }.title)
        // Начать ввод задачи — поле названия закрывается; неизвестный подсписок — ничего
        assertNull(loaded().reduce(TodoListIntent.StartRenameSection(6), TodoListIntent.StartNew).sectionEditor)
        assertEquals(loaded(), loaded().reduce(TodoListIntent.StartRenameSection(99)))
    }

    @Test
    fun `удалённый подсписок закрывает поле его названия`() {
        val state = loaded().reduce(TodoListIntent.StartRenameSection(6))
        val reloaded = todoListReduce(state, TodoListIntent.DataLoaded(list, tasks, emptyList(), listOf(greenhouse)))
        assertNull(reloaded.sectionEditor)
        assertNull(state.reduce(TodoListIntent.ToggleViewMode).sectionEditor)
    }

    @Test
    fun `фильтр скрывает подсписки без подходящих задач и работает в столбцах`() {
        val state = loaded().reduce(TodoListIntent.Search("полить"))
        assertEquals(listOf(null to emptyList(), 5L to listOf(2L, 21L)), state.groupIds())
        assertEquals(listOf(2L), state.columns.single { it.section?.id == 5L }.cards.map { it.item.task.id })
    }

    @Test
    fun `отметка в подсписке задерживается до HideLingering`() {
        val toggled = loaded().reduce(TodoListIntent.ToggleDone(3))
        assertEquals(listOf(2L, 3L), toggled.groups[1].cards.map { it.item.task.id })
        assertTrue(toggled.groups[1].cards.last().isDone)
        val hidden = toggled.reduce(TodoListIntent.HideLingering(3))
        assertEquals(listOf(2L), hidden.groups[1].cards.map { it.item.task.id })
    }
}
