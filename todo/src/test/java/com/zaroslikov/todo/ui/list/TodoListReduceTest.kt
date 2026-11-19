package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoListReduceTest {

    @Test
    fun `новая подзадача сохраняет родителя после Enter и не допускает второй уровень`() {
        val editing = loaded().reduce(
            TodoListIntent.StartNewSubtask(1),
            TodoListIntent.EditorTextChanged("Полить"),
            TodoListIntent.EditorNoteChanged("Утром")
        )
        assertEquals(1L, editing.editor?.parentId)
        val next = editing.reduce(TodoListIntent.EditorSubmit)
        assertEquals(TodoInlineEditor(null, "", parentId = 1), next.editor)
        assertNull(next.reduce(TodoListIntent.EditorSubmit).editor)
        assertNull(loaded().reduce(TodoListIntent.StartNewSubtask(11)).editor)
    }

    @Test
    fun `добавление подзадачи сохраняет правку родителя и раскрывает детей`() {
        val editing = loaded().reduce(
            TodoListIntent.ToggleCollapse(1), TodoListIntent.StartRename(1),
            TodoListIntent.EditorTextChanged("Новое название"), TodoListIntent.StartNewSubtask(1)
        )
        assertEquals("Новое название", editing.textOverrides[1]?.title)
        assertFalse(1L in editing.collapsed)
        assertEquals(1L, editing.editor?.parentId)
    }

    @Test
    fun `закрытие редактора не возвращает удалённую заметку до подтверждения БД`() {
        val original = item(1, "Рассада").let { it.copy(task = it.task.copy(note = "Первая строка\nВторая строка")) }
        val saved = loaded(tasks = listOf(original)).reduce(
            TodoListIntent.StartRename(1),
            TodoListIntent.EditorTextChanged("Томаты"),
            TodoListIntent.EditorNoteChanged(""),
            TodoListIntent.EditorFinish(1)
        )
        assertNull(saved.editor)
        assertEquals("Томаты", saved.rows.single().item.task.title)
        assertEquals("", saved.rows.single().item.task.note)
        // Даже повторный старый снимок и мгновенное повторное редактирование не возвращают текст.
        val delayed = loaded(state = saved, tasks = listOf(original))
        assertEquals("", delayed.rows.single().item.task.note)
        assertEquals("Томаты", delayed.reduce(TodoListIntent.StartRename(1)).editor?.text)
        val written = delayed.reduce(TodoListIntent.EditorSaveSucceeded(1, delayed.textOverrides.getValue(1)))
        val confirmed = loaded(state = written, tasks = listOf(original.copy(task = original.task.copy(title = "Томаты", note = ""))))
        assertTrue(confirmed.textOverrides.isEmpty())
        assertEquals("", confirmed.rows.single().item.task.note)
        assertEquals(original, saved.tasks.single())
    }

    @Test
    fun `ошибка старой записи и её поток не отменяют более новую правку`() {
        val original = item(1, "Рассада")
        val first = loaded(tasks = listOf(original)).reduce(
            TodoListIntent.StartRename(1), TodoListIntent.EditorNoteChanged("Старая правка"),
            TodoListIntent.EditorSubmit
        )
        val firstText = first.textOverrides.getValue(1)
        val second = first.reduce(
            TodoListIntent.StartRename(1), TodoListIntent.EditorTextChanged("Томаты"),
            TodoListIntent.EditorNoteChanged("Новая правка"), TodoListIntent.EditorFinish(1)
        )
        val stale = loaded(state = second, tasks = listOf(original.copy(task = original.task.copy(note = "Старая правка"))))
            .reduce(TodoListIntent.EditorSaveFailed(1, firstText))
        assertEquals("Томаты", stale.rows.single().item.task.title)
        assertEquals("Новая правка", stale.rows.single().item.task.note)
        val failed = stale.reduce(TodoListIntent.EditorSaveFailed(1, stale.textOverrides.getValue(1)))
        assertTrue(failed.textOverrides.isEmpty())
        assertEquals("Старая правка", failed.rows.single().item.task.note)
    }

    @Test
    fun `пустое название сохраняет прежнее название но позволяет очистить заметку`() {
        val original = item(1, "Рассада").let { it.copy(task = it.task.copy(note = "Заметка")) }
        val saved = loaded(tasks = listOf(original)).reduce(
            TodoListIntent.StartRename(1), TodoListIntent.EditorTextChanged(""),
            TodoListIntent.EditorNoteChanged(""), TodoListIntent.EditorFinish(1)
        )
        assertEquals(TodoTaskText("Рассада", "", revision = 1, dataRevision = 1), saved.textOverrides[1])
        assertEquals("Рассада", saved.rows.single().item.task.title)
        assertEquals("", saved.rows.single().item.task.note)
    }

    @Test
    fun `возврат к исходному тексту не подтверждается старым снимком или ошибкой первого поколения`() {
        val original = item(1, "Рассада")
        val first = loaded(tasks = listOf(original)).reduce(
            TodoListIntent.StartRename(1), TodoListIntent.EditorFinish(1)
        )
        val firstText = first.textOverrides.getValue(1)
        val second = first.reduce(
            TodoListIntent.StartRename(1), TodoListIntent.EditorTextChanged("Томаты"), TodoListIntent.EditorFinish(1),
            TodoListIntent.StartRename(1), TodoListIntent.EditorTextChanged("Рассада"), TodoListIntent.EditorFinish(1)
        )
        val unchangedSnapshot = second.reduce(TodoListIntent.EditorSaveFailed(1, firstText), TodoListIntent.EditorSaveSucceeded(1, firstText))
        assertEquals(3L, unchangedSnapshot.textOverrides.getValue(1).revision)
        // Запись A завершилась, но raw A ещё старый: более ранний Flow B не должен мелькнуть.
        val written = unchangedSnapshot.reduce(TodoListIntent.EditorSaveSucceeded(1, unchangedSnapshot.textOverrides.getValue(1)))
        assertTrue(written.textOverrides.getValue(1).saved)
        val intermediate = loaded(state = written, tasks = listOf(original.copy(task = original.task.copy(title = "Томаты"))))
        assertEquals("Рассада", intermediate.rows.single().item.task.title)
        val confirmed = loaded(state = intermediate, tasks = listOf(original))
        assertTrue(confirmed.textOverrides.isEmpty())
        assertEquals("Рассада", confirmed.rows.single().item.task.title)
    }

    @Test
    fun `поток пришедший до подтверждения записи освобождает защиту текста`() {
        val original = item(1, "Рассада")
        val edited = loaded(tasks = listOf(original)).reduce(
            TodoListIntent.StartRename(1), TodoListIntent.EditorNoteChanged("Новая заметка"),
            TodoListIntent.EditorFinish(1)
        )
        val token = edited.textOverrides.getValue(1)
        val matching = original.copy(task = original.task.copy(note = "Новая заметка"))
        val beforeSuccess = loaded(state = edited, tasks = listOf(matching))
        assertFalse(beforeSuccess.textOverrides.isEmpty())
        val saved = beforeSuccess.reduce(TodoListIntent.EditorSaveSucceeded(1, token))
        assertTrue(saved.textOverrides.isEmpty())
        val external = matching.copy(task = matching.task.copy(note = "Изменено в Подробно"))
        val refreshed = loaded(state = saved, tasks = listOf(external))
        assertEquals("Изменено в Подробно", refreshed.rows.single().item.task.note)
    }

    @Test
    fun `заметка сохраняется в черновике при правке названия и очищается для следующей задачи`() {
        val typed = loaded().reduce(
            TodoListIntent.StartNew,
            TodoListIntent.EditorNoteChanged("Полить утром\nПроверить землю"),
            TodoListIntent.EditorTextChanged("Рассада")
        )
        assertEquals("Полить утром\nПроверить землю", typed.editor?.note)
        assertEquals("Рассада", typed.editor?.text)
        val next = typed.reduce(TodoListIntent.EditorSubmit)
        assertEquals("", next.editor?.text)
        assertEquals("", next.editor?.note)
    }

    @Test
    fun `редактирование загружает существующую заметку и не теряет её после обновления данных`() {
        val original = item(1, "Рассада").let { it.copy(task = it.task.copy(note = "Старое примечание")) }
        val editing = loaded(tasks = listOf(original)).reduce(
            TodoListIntent.StartRename(1),
            TodoListIntent.EditorTextChanged("Томаты")
        )
        assertEquals("Старое примечание", editing.editor?.note)
        val changed = editing.reduce(TodoListIntent.EditorNoteChanged("Новая заметка\nВторая строка"))
        val refreshed = loaded(state = changed, tasks = listOf(original))
        assertEquals("Томаты", refreshed.editor?.text)
        assertEquals("Новая заметка\nВторая строка", refreshed.editor?.note)
    }

    private val list = DomainTodoList(id = 10, idPT = 1, title = "Посевная", colorIndex = 2)

    private val planned = DomainTodoStatus(1, 1, "planned", 1, 0, TodoDefaultStatus.PLANNED, false)
    private val inProgress = DomainTodoStatus(2, 1, "in_progress", 3, 1, TodoDefaultStatus.IN_PROGRESS, false)
    private val done = DomainTodoStatus(3, 1, "done", 2, 2, TodoDefaultStatus.DONE, true)

    private val urgent = DomainTodoTag(id = 7, idPT = 1, title = "Срочно")
    private val buy = DomainTodoTag(id = 8, idPT = 1, title = "Закупка")

    private fun item(
        id: Long,
        title: String,
        status: DomainTodoStatus = planned,
        priority: TodoPriority = TodoPriority.NONE,
        tags: List<DomainTodoTag> = emptyList(),
        parentId: Long? = null,
        position: Int = id.toInt(),
        subtaskCount: Int = 0,
        commentCount: Int = 0
    ) = DomainTodoTaskItem(
        task = DomainTodoTask(
            id = id,
            listId = list.id,
            parentId = parentId,
            title = title,
            priority = priority,
            statusId = status.id,
            position = position
        ),
        status = status,
        tags = tags,
        subtaskCount = subtaskCount,
        commentCount = commentCount
    )

    // 1 — задача с подзадачами 11 (в планах) и 12 (завершена); 3 — завершённая задача
    private val tasks = listOf(
        item(1, "Купить семена", priority = TodoPriority.HIGH, tags = listOf(buy, urgent), subtaskCount = 2),
        item(2, "Вспахать поле", status = inProgress, priority = TodoPriority.LOW),
        item(3, "Посадить картофель", status = done),
        item(4, "Купить удобрения", priority = TodoPriority.HIGH, tags = listOf(buy)),
        item(12, "Огурцы", status = done, parentId = 1, position = 1),
        item(11, "Томаты", parentId = 1, position = 0)
    )

    private fun loaded(
        state: TodoListState = TodoListState(),
        tasks: List<DomainTodoTaskItem> = this.tasks,
        tags: List<DomainTodoTag> = listOf(urgent, buy)
    ) = todoListReduce(state, TodoListIntent.DataLoaded(list, tasks, tags))

    private fun TodoListState.visibleIds(): List<Long> = rows.map { it.item.task.id }

    private fun TodoListState.reduce(vararg intents: TodoListIntent): TodoListState =
        intents.fold(this) { acc, intent -> todoListReduce(acc, intent) }

    @Test
    fun `загрузка собирает дерево, подзадачи под родителем, завершённые скрыты`() {
        val state = loaded()

        assertFalse(state.isLoading)
        assertEquals(list, state.list)
        assertEquals(listOf(1L, 11L, 2L, 4L), state.visibleIds())
        assertEquals(listOf(false, true, false, false), state.rows.map { it.isSubtask })
        assertEquals(1, state.rows.first().childCount)
        assertEquals(2, state.completedCount)
    }

    @Test
    fun `показ завершённых добавляет их на свои места`() {
        val state = loaded().reduce(TodoListIntent.ToggleShowCompleted)

        assertTrue(state.showCompleted)
        assertEquals(listOf(1L, 11L, 12L, 2L, 3L, 4L), state.visibleIds())
        assertEquals(2, state.rows.first().childCount)
        assertTrue(state.rows.first { it.item.task.id == 3L }.isDone)

        assertEquals(listOf(1L, 11L, 2L, 4L), state.reduce(TodoListIntent.ToggleShowCompleted).visibleIds())
    }

    @Test
    fun `сворачивание подзадач родителя`() {
        val collapsed = loaded().reduce(TodoListIntent.ToggleCollapse(1))
        assertEquals(listOf(1L, 2L, 4L), collapsed.visibleIds())
        assertTrue(collapsed.rows.first().isCollapsed)

        val expanded = collapsed.reduce(TodoListIntent.ToggleCollapse(1))
        assertEquals(listOf(1L, 11L, 2L, 4L), expanded.visibleIds())
    }

    @Test
    fun `отметка закрашивает сразу и держит строку до конца паузы`() {
        val marked = loaded().reduce(TodoListIntent.ToggleDone(2))

        assertTrue(marked.rows.first { it.item.task.id == 2L }.isDone)
        assertEquals(listOf(1L, 11L, 2L, 4L), marked.visibleIds())
        assertTrue(2L in marked.lingering)
        assertEquals(3, marked.completedCount)

        val hidden = marked.reduce(TodoListIntent.HideLingering(2))
        assertEquals(listOf(1L, 11L, 4L), hidden.visibleIds())
    }

    @Test
    fun `повторный тап в паузе отменяет отметку`() {
        val undone = loaded().reduce(TodoListIntent.ToggleDone(2), TodoListIntent.ToggleDone(2))

        assertFalse(undone.rows.first { it.item.task.id == 2L }.isDone)
        assertFalse(2L in undone.lingering)
        // Истёкший таймер уже ничего не прячет
        assertEquals(listOf(1L, 11L, 2L, 4L), undone.reduce(TodoListIntent.HideLingering(2)).visibleIds())
    }

    @Test
    fun `при показанных завершённых отметка без паузы, снятие возвращает в работу`() {
        val shown = loaded().reduce(TodoListIntent.ToggleShowCompleted, TodoListIntent.ToggleDone(2))
        assertTrue(shown.lingering.isEmpty())
        assertTrue(shown.rows.first { it.item.task.id == 2L }.isDone)

        val unmarked = shown.reduce(TodoListIntent.ToggleDone(3))
        assertFalse(unmarked.rows.first { it.item.task.id == 3L }.isDone)
    }

    @Test
    fun `оптимистичная отметка уходит, когда БД вернула то же, и откатывается при ошибке`() {
        val marked = loaded().reduce(TodoListIntent.ToggleDone(2))

        // БД ещё не записала — отметка держится
        assertEquals(mapOf(2L to true), loaded(marked).doneOverrides)

        val fromDb = loaded(marked, tasks = tasks.map { if (it.task.id == 2L) item(2, "Вспахать поле", status = done) else it })
        assertTrue(fromDb.doneOverrides.isEmpty())
        assertTrue(fromDb.rows.first { it.item.task.id == 2L }.isDone)

        val failed = marked.reduce(TodoListIntent.ToggleDoneFailed(2))
        assertTrue(failed.doneOverrides.isEmpty())
        assertFalse(failed.rows.first { it.item.task.id == 2L }.isDone)
    }

    @Test
    fun `отмеченный родитель при скрытых завершённых исчезает вместе с подзадачами`() {
        val state = loaded().reduce(TodoListIntent.ToggleDone(1), TodoListIntent.HideLingering(1))
        assertEquals(listOf(2L, 4L), state.visibleIds())
    }

    @Test
    fun `новая задача — Enter очищает поле и оставляет ввод, пустой Enter завершает`() {
        val started = loaded().reduce(TodoListIntent.StartNew)
        assertEquals(TodoInlineEditor(null, ""), started.editor)

        val typed = started.reduce(TodoListIntent.EditorTextChanged("Полить"))
        assertEquals("Полить", typed.editor?.text)

        val submitted = typed.reduce(TodoListIntent.EditorSubmit)
        assertEquals(TodoInlineEditor(null, ""), submitted.editor)

        assertNull(submitted.reduce(TodoListIntent.EditorSubmit).editor)
    }

    @Test
    fun `потеря фокуса закрывает только свою строку ввода`() {
        val renaming = loaded().reduce(TodoListIntent.StartNew, TodoListIntent.StartRename(2))
        assertEquals(TodoInlineEditor(2, "Вспахать поле"), renaming.editor)

        // Старая строка новой задачи теряет фокус уже после перехода к переименованию
        assertEquals(renaming.editor, renaming.reduce(TodoListIntent.EditorFinish(null)).editor)
        assertNull(renaming.reduce(TodoListIntent.EditorFinish(2)).editor)
        assertNull(renaming.reduce(TodoListIntent.EditorInfo).editor)
    }

    @Test
    fun `переименование — Enter закрывает строку, перевод строки заменяется пробелом`() {
        val renaming = loaded().reduce(
            TodoListIntent.StartRename(4),
            TodoListIntent.EditorTextChanged("Купить\nнавоз")
        )
        assertEquals("Купить навоз", renaming.editor?.text)
        assertNull(renaming.reduce(TodoListIntent.EditorSubmit).editor)
    }

    @Test
    fun `переименование задачи, удалённой в другом месте, закрывается`() {
        val renaming = loaded().reduce(TodoListIntent.StartRename(4))
        assertNull(loaded(renaming, tasks = tasks.filter { it.task.id != 4L }).editor)
    }

    @Test
    fun `свайп удаляет простую задачу сразу, а с подзадачами — после подтверждения`() {
        val simple = loaded().reduce(TodoListIntent.RequestDeleteTask(4))
        assertNull(simple.deleteCandidate)
        assertEquals(listOf(1L, 11L, 2L), simple.visibleIds())
        assertTrue(simple.reduce(TodoListIntent.DeleteTaskFailed(4)).visibleIds().contains(4L))

        val withSubtasks = loaded().reduce(TodoListIntent.RequestDeleteTask(1))
        assertEquals(1L, withSubtasks.deleteCandidate?.task?.id)
        assertEquals(listOf(1L, 11L, 2L, 4L), withSubtasks.visibleIds())

        assertNull(withSubtasks.reduce(TodoListIntent.DismissDeleteTask).deleteCandidate)
        val confirmed = withSubtasks.reduce(TodoListIntent.ConfirmDeleteTask)
        assertNull(confirmed.deleteCandidate)
        assertEquals(listOf(2L, 4L), confirmed.visibleIds())
    }

    @Test
    fun `очистка завершённых — только когда они есть`() {
        val asked = loaded().reduce(TodoListIntent.RequestClearCompleted)
        assertTrue(asked.showClearCompleted)
        assertFalse(asked.reduce(TodoListIntent.ConfirmClearCompleted).showClearCompleted)

        val nothing = loaded(tasks = tasks.filter { !it.task.title.startsWith("П") && it.task.id != 12L })
        assertEquals(0, nothing.completedCount)
        assertFalse(nothing.reduce(TodoListIntent.RequestClearCompleted).showClearCompleted)
    }

    @Test
    fun `поиск по названию без учёта регистра находит и подзадачи, очистка запроса сбрасывает выдачу`() {
        val state = loaded().reduce(TodoListIntent.Search("  КУПИТЬ "))
        assertTrue(state.hasFilters)
        // Подходит родитель — видны все его подзадачи
        assertEquals(listOf(1L, 11L, 4L), state.visibleIds())

        // Подходит только подзадача — родитель остаётся ради неё
        assertEquals(listOf(1L, 11L), state.reduce(TodoListIntent.Search("томат")).visibleIds())

        val cleared = state.reduce(TodoListIntent.Search(""))
        assertFalse(cleared.hasFilters)
        assertEquals("", cleared.query)
        assertEquals(listOf(1L, 11L, 2L, 4L), cleared.visibleIds())
    }

    @Test
    fun `ввод запроса завершает правку и сразу ищет по обновлённой заметке`() {
        val result = loaded(tasks = listOf(item(1, "Рассада"))).reduce(
            TodoListIntent.StartRename(1),
            TodoListIntent.EditorNoteChanged("Полить утром"),
            TodoListIntent.Search("утром")
        )

        assertNull(result.editor)
        assertEquals("Полить утром", result.textOverrides[1]?.note)
        assertEquals(listOf(1L), result.visibleIds())
    }

    @Test
    fun `фильтры по приоритету и тегу складываются, повторный выбор снимает`() {
        val low = loaded().reduce(TodoListIntent.SelectPriority(TodoPriority.LOW))
        assertEquals(listOf(2L), low.visibleIds())
        assertNull(low.reduce(TodoListIntent.SelectPriority(TodoPriority.LOW)).priorityFilter)

        val both = loaded().reduce(TodoListIntent.SelectTag(buy.id), TodoListIntent.SelectPriority(TodoPriority.HIGH))
        assertEquals(listOf(1L, 11L, 4L), both.visibleIds())
        assertEquals(listOf(1L, 11L), both.reduce(TodoListIntent.SelectTag(urgent.id)).visibleIds())

        val reset = both.reduce(TodoListIntent.ResetFilters)
        assertFalse(reset.hasFilters)
        assertEquals(4, reset.visibleIds().size)

        assertTrue(loaded().reduce(TodoListIntent.OpenFilters).showFilterSheet)
        assertFalse(loaded().reduce(TodoListIntent.OpenFilters, TodoListIntent.CloseFilters).showFilterSheet)
    }

    @Test
    fun `удалённый тег снимает фильтр при перезагрузке`() {
        val filtered = loaded().reduce(TodoListIntent.SelectTag(urgent.id))

        val state = loaded(filtered, tags = listOf(buy))
        assertNull(state.tagFilter)
        assertEquals(4, state.visibleIds().size)
    }

    @Test
    fun `правка списка открывает шторку с его названием, пустое название — ошибка`() {
        val opened = loaded().reduce(TodoListIntent.OpenEditList)
        assertEquals("Посевная", opened.form?.title)
        assertEquals(2, opened.form?.colorIndex)

        val invalid = opened.reduce(TodoListIntent.FormTitleChanged(""), TodoListIntent.SaveForm)
        assertEquals(true, invalid.form?.titleError)

        val saved = invalid.reduce(TodoListIntent.FormTitleChanged("Уборка"), TodoListIntent.SaveForm)
        assertNull(saved.form)
    }

    @Test
    fun `удаление списка с подтверждением`() {
        val requested = loaded().reduce(TodoListIntent.RequestDeleteList)
        assertTrue(requested.showDeleteList)

        assertFalse(requested.reduce(TodoListIntent.DismissDeleteList).showDeleteList)
        assertFalse(requested.reduce(TodoListIntent.ConfirmDeleteList).showDeleteList)
    }
}
