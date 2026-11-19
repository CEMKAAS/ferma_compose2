package com.zaroslikov.todo.ui.task_details

import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.DomainTodoLink
import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoTaskDetailsReduceTest {

    private fun reduce(state: TodoTaskDetailsState, vararg intents: TodoTaskDetailsIntent) =
        intents.fold(state) { acc, intent -> TodoTaskDetailsReducer.reducer(acc, intent) }

    private val planned = DomainTodoStatus(id = 1, idPT = 7, title = "", defaultCode = TodoDefaultStatus.PLANNED)
    private val done = DomainTodoStatus(id = 3, idPT = 7, title = "", position = 2, defaultCode = TodoDefaultStatus.DONE, isDone = true)
    private val feed = DomainTodoTag(id = 10, idPT = 7, title = "Корм")
    private val repair = DomainTodoTag(id = 11, idPT = 7, title = "Ремонт")

    private val task = DomainTodoTask(id = 20, listId = 5, title = "Купить корм", note = "Мешок", statusId = 1)
    private fun other(id: Long) = DomainTodoTask(id = id, listId = 6, title = "Задача $id", statusId = 1)

    private val comment = DomainTodoComment(id = 30, taskId = 20, text = "Позвонить", createdAt = "2026-10-20T10:00")
    private val link = DomainTodoLinkedTask(
        link = DomainTodoLink(id = 40, taskId = 20, linkedTaskId = 23, type = TodoLinkType.BLOCKS),
        task = other(23),
        status = planned,
        listTitle = "Ремонт",
        isOutgoing = true
    )

    private val data = TodoTaskDetailsData(
        item = DomainTodoTaskItem(task = task, status = planned, tags = listOf(feed)),
        parent = null,
        listTitle = "Хозяйство",
        listColorIndex = 1,
        idPT = 7,
        statuses = listOf(planned, done),
        tags = listOf(feed, repair),
        subtasks = listOf(
            DomainTodoTaskItem(task = other(21).copy(parentId = 20, listId = 5), status = done),
            DomainTodoTaskItem(task = other(22).copy(parentId = 20, listId = 5), status = planned)
        ),
        links = listOf(link),
        comments = listOf(comment)
    )

    private val subtaskData = data.copy(
        item = DomainTodoTaskItem(task = other(21).copy(parentId = 20, listId = 5), status = done),
        parent = task,
        subtasks = emptyList(),
        links = emptyList(),
        comments = emptyList()
    )

    private val loaded = reduce(TodoTaskDetailsState(), TodoTaskDetailsIntent.Open(20), TodoTaskDetailsIntent.Loaded(data))

    @Test
    fun `переходы между задачами возвращаются по истории без добавления при возврате`() {
        val navigated = reduce(
            loaded,
            TodoTaskDetailsIntent.NavigateToTask(23),
            TodoTaskDetailsIntent.NavigateToTask(24)
        )
        assertEquals(listOf(20L, 23L), navigated.backStack)
        val back = reduce(navigated, TodoTaskDetailsIntent.Back)
        assertEquals(23L, back.taskId)
        assertEquals(listOf(20L), back.backStack)
        assertTrue(back.isLoading)
        assertNull(back.data)
        val root = reduce(back, TodoTaskDetailsIntent.Back)
        assertEquals(20L, root.taskId)
        assertTrue(root.backStack.isEmpty())
        assertEquals(root, reduce(root, TodoTaskDetailsIntent.Back))
    }

    @Test
    fun `повторный переход и опоздавший снимок не меняют историю`() {
        val navigated = reduce(loaded, TodoTaskDetailsIntent.NavigateToTask(23))
        assertEquals(listOf(20L), navigated.backStack)
        assertEquals(navigated, reduce(navigated, TodoTaskDetailsIntent.NavigateToTask(23)))
        assertEquals(navigated, reduce(navigated, TodoTaskDetailsIntent.Loaded(data)))
    }

    @Test
    fun `новое открытие закрытие удаление и исчезновение очищают историю`() {
        val navigated = reduce(loaded, TodoTaskDetailsIntent.NavigateToTask(23))
        assertTrue(reduce(navigated, TodoTaskDetailsIntent.Open(23)).backStack.isEmpty())
        assertTrue(reduce(navigated, TodoTaskDetailsIntent.Open(24)).backStack.isEmpty())
        assertEquals(TodoTaskDetailsState(), reduce(navigated, TodoTaskDetailsIntent.Close))
        assertEquals(TodoTaskDetailsState(), reduce(navigated, TodoTaskDetailsIntent.ConfirmDelete))
        assertEquals(TodoTaskDetailsState(), reduce(navigated, TodoTaskDetailsIntent.Gone(23)))
    }

    @Test
    fun `открытие показывает загрузку, снимок заполняет черновики`() {
        val opening = reduce(TodoTaskDetailsState(), TodoTaskDetailsIntent.Open(20))
        assertTrue(opening.isOpen)
        assertTrue(opening.isLoading)
        assertNull(opening.data)

        assertFalse(loaded.isLoading)
        assertEquals(data, loaded.data)
        assertEquals("Купить корм", loaded.title)
        assertEquals("Мешок", loaded.note)
        assertEquals(1, data.subtaskDoneCount)
        assertEquals(setOf(10L), data.tagIds)
    }

    @Test
    fun `переключение на подзадачу начинает с чистого листа`() {
        val dirty = reduce(
            loaded,
            TodoTaskDetailsIntent.QuickSubtaskChange("Ещё"),
            TodoTaskDetailsIntent.CommentTextChange("Текст"),
            TodoTaskDetailsIntent.OpenLinkSheet
        )
        val switched = reduce(dirty, TodoTaskDetailsIntent.Open(21))
        assertEquals(21L, switched.taskId)
        assertTrue(switched.isLoading)
        assertNull(switched.data)
        assertEquals("", switched.quickSubtask)
        assertEquals("", switched.commentText)
        assertNull(switched.linkSheet)

        val shown = reduce(switched, TodoTaskDetailsIntent.Loaded(subtaskData))
        assertTrue(shown.data!!.isSubtask)
        assertEquals(task, shown.data.parent)
        assertEquals("Задача 21", shown.title)
    }

    @Test
    fun `повторное открытие той же задачи ничего не сбрасывает`() {
        val typed = reduce(loaded, TodoTaskDetailsIntent.TitleChange("Новое"))
        assertEquals(typed, reduce(typed, TodoTaskDetailsIntent.Open(20)))
    }

    @Test
    fun `опоздавший снимок прежней задачи игнорируется`() {
        val switched = reduce(loaded, TodoTaskDetailsIntent.Open(21))
        assertEquals(switched, reduce(switched, TodoTaskDetailsIntent.Loaded(data)))
    }

    @Test
    fun `задача пропала — шторка закрывается, чужое исчезновение не трогает`() {
        assertFalse(reduce(loaded, TodoTaskDetailsIntent.Gone(20)).isOpen)
        assertEquals(loaded, reduce(loaded, TodoTaskDetailsIntent.Gone(99)))
        assertFalse(reduce(loaded, TodoTaskDetailsIntent.Close).isOpen)
    }

    @Test
    fun `черновик названия не перетирается снимком из БД, нетронутый — обновляется`() {
        val typed = reduce(loaded, TodoTaskDetailsIntent.TitleChange("Купить зерно"))
        val fromDb = data.copy(item = data.item.copy(task = task.copy(title = "Купить корм", note = "Два мешка")))
        val state = reduce(typed, TodoTaskDetailsIntent.Loaded(fromDb))
        assertEquals("Купить зерно", state.title)
        assertEquals("Два мешка", state.note)
    }

    @Test
    fun `пустое название не сохраняется — возвращается прежнее`() {
        val state = reduce(loaded, TodoTaskDetailsIntent.TitleChange("   "), TodoTaskDetailsIntent.CommitText)
        assertEquals("Купить корм", state.title)

        val trimmed = reduce(loaded, TodoTaskDetailsIntent.TitleChange("  Купить зерно "), TodoTaskDetailsIntent.CommitText)
        assertEquals("Купить зерно", trimmed.title)
        // Перевод строки из буфера в однострочном названии становится пробелом
        assertEquals("а б", reduce(loaded, TodoTaskDetailsIntent.TitleChange("а\nб")).title)
    }

    @Test
    fun `выключение даты сбрасывает и время, время без даты не ставится`() {
        val withDate = reduce(loaded, TodoTaskDetailsIntent.SetDueDate("2026-10-28"))
        assertEquals("2026-10-28", withDate.data!!.task.dueDate)
        assertNull(withDate.data.task.dueTime)

        val withTime = reduce(withDate, TodoTaskDetailsIntent.SetDueTime("10:00"))
        assertEquals("10:00", withTime.data!!.task.dueTime)
        // Новая дата время не трогает
        assertEquals("10:00", reduce(withTime, TodoTaskDetailsIntent.SetDueDate("2026-11-01")).data!!.task.dueTime)

        val off = reduce(withTime, TodoTaskDetailsIntent.SetDueDate(null))
        assertNull(off.data!!.task.dueDate)
        assertNull(off.data.task.dueTime)

        assertNull(reduce(off, TodoTaskDetailsIntent.SetDueTime("12:00")).data!!.task.dueTime)
        assertNull(reduce(withTime, TodoTaskDetailsIntent.SetDueTime(null)).data!!.task.dueTime)
    }

    @Test
    fun `приоритет и статус меняются оптимистично`() {
        val state = reduce(loaded, TodoTaskDetailsIntent.SetPriority(TodoPriority.HIGH), TodoTaskDetailsIntent.SetStatus(3))
        assertEquals(TodoPriority.HIGH, state.data!!.task.priority)
        assertEquals(3L, state.data.task.statusId)
        assertEquals(done, state.data.item.status)
    }

    @Test
    fun `сброс снимает все теги не закрывая выбор и не меняя другие поля`() {
        val selected = reduce(loaded, TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.ToggleTag(11))
        val reset = reduce(selected, TodoTaskDetailsIntent.ResetTags)
        assertTrue(reset.showTagsSheet)
        assertTrue(reset.tagSelection!!.selectedIds.isEmpty())
        assertEquals(loaded.data!!.tagIds, reset.data!!.tagIds)
        assertEquals(selected.data!!.task, reset.data.task)
        assertEquals(selected.data.tags, reset.data.tags)
        assertEquals(reset, reduce(reset, TodoTaskDetailsIntent.ResetTags))
    }

    @Test
    fun `выбор тегов остаётся открыт при выборе и создании и закрывается отдельно`() {
        val opened = reduce(loaded, TodoTaskDetailsIntent.OpenTagsSheet)
        assertTrue(opened.showTagsSheet)
        val selected = reduce(opened, TodoTaskDetailsIntent.ToggleTag(11))
        assertTrue(selected.showTagsSheet)
        assertEquals(setOf(10L, 11L), selected.tagSelection!!.selectedIds)
        assertEquals(setOf(10L), selected.data!!.tagIds)
        val creating = reduce(selected, TodoTaskDetailsIntent.StartNewTag)
        assertTrue(creating.showTagsSheet)
        assertNotNull(creating.newTag)
        assertTrue(reduce(creating, TodoTaskDetailsIntent.CancelNewTag).showTagsSheet)
        assertTrue(reduce(creating, TodoTaskDetailsIntent.NewTagChange("Новый"), TodoTaskDetailsIntent.SubmitNewTag).showTagsSheet)
        val closed = reduce(selected, TodoTaskDetailsIntent.CloseTagsSheet)
        assertFalse(closed.showTagsSheet)
        assertEquals(selected.data, closed.data)
        assertFalse(reduce(selected, TodoTaskDetailsIntent.NavigateToTask(21)).showTagsSheet)
        assertFalse(reduce(selected, TodoTaskDetailsIntent.Close).showTagsSheet)
    }

    @Test
    fun `сохранение применяет черновик а закрытие и повторное открытие восстанавливает сохранённые теги`() {
        val selected = reduce(loaded, TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.ToggleTag(11))
        val discarded = reduce(selected, TodoTaskDetailsIntent.CloseTagsSheet, TodoTaskDetailsIntent.OpenTagsSheet)
        assertEquals(setOf(10L), discarded.tagSelection!!.selectedIds)
        val saved = reduce(selected, TodoTaskDetailsIntent.SaveTags)
        assertFalse(saved.showTagsSheet)
        assertEquals(setOf(10L, 11L), saved.data!!.tagIds)
        assertEquals(setOf(10L, 11L), reduce(saved, TodoTaskDetailsIntent.OpenTagsSheet).tagSelection!!.selectedIds)
        assertEquals(loaded, reduce(loaded, TodoTaskDetailsIntent.SaveTags))
    }

    @Test
    fun `созданный тег выбирается только в исходном черновике и не прикрепляется до сохранения`() {
        val opened = reduce(loaded, TodoTaskDetailsIntent.OpenTagsSheet)
        val tag = DomainTodoTag(id = 12, idPT = 7, title = "Новый")
        val completed = TodoTaskDetailsIntent.TagCreated(20, tag, opened.tagSelection!!.session)
        val created = reduce(opened, completed)
        assertEquals(setOf(10L, 12L), created.tagSelection!!.selectedIds)
        assertEquals(setOf(10L), created.data!!.tagIds)
        assertTrue(created.data.tags.contains(tag))
        assertEquals(setOf(10L, 12L), reduce(created, TodoTaskDetailsIntent.SaveTags).data!!.tagIds)
        val reopened = reduce(opened, TodoTaskDetailsIntent.CloseTagsSheet, TodoTaskDetailsIntent.OpenTagsSheet)
        assertEquals(reopened, reduce(reopened, completed))
        val dismissed = reduce(opened, TodoTaskDetailsIntent.CloseTagsSheet)
        assertEquals(dismissed, reduce(dismissed, completed))
        val navigated = reduce(opened, TodoTaskDetailsIntent.NavigateToTask(21))
        assertEquals(navigated, reduce(navigated, completed))
    }

    @Test
    fun `теги отмечаются и снимаются, новый тег сохраняет выбранный цвет в форме`() {
        assertEquals(loaded, reduce(loaded, TodoTaskDetailsIntent.ToggleTag(11)))
        assertEquals(loaded, reduce(loaded, TodoTaskDetailsIntent.StartNewTag))
        val added = reduce(loaded, TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.ToggleTag(11))
        assertEquals(setOf(10L, 11L), added.tagSelection!!.selectedIds)
        val removed = reduce(added, TodoTaskDetailsIntent.ToggleTag(10))
        assertEquals(setOf(11L), removed.tagSelection!!.selectedIds)

        val typing = reduce(
            loaded, TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.StartNewTag,
            TodoTaskDetailsIntent.NewTagChange("Срочно"), TodoTaskDetailsIntent.NewTagColorChange(5)
        )
        assertEquals("Срочно", typing.newTag!!.title)
        assertEquals(5, typing.newTag.colorIndex)
        assertNull(reduce(typing, TodoTaskDetailsIntent.SubmitNewTag).newTag)
        assertNull(reduce(typing, TodoTaskDetailsIntent.CancelNewTag).newTag)
        // Закрытую форму не открывают опоздавшие события ввода
        assertNull(reduce(loaded, TodoTaskDetailsIntent.NewTagChange("x")).newTag)
        assertNull(reduce(loaded, TodoTaskDetailsIntent.NewTagColorChange(5)).newTag)
    }

    @Test
    fun `пустое название тега оставляет форму с ошибкой, исправление убирает ошибку`() {
        val invalid = reduce(
            loaded, TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.StartNewTag,
            TodoTaskDetailsIntent.NewTagChange("   "), TodoTaskDetailsIntent.NewTagColorChange(4),
            TodoTaskDetailsIntent.SubmitNewTag
        )
        assertTrue(invalid.newTag!!.titleError)
        assertEquals(4, invalid.newTag.colorIndex)
        val fixed = reduce(invalid, TodoTaskDetailsIntent.NewTagChange("Корм"))
        assertFalse(fixed.newTag!!.titleError)
        assertNull(reduce(fixed, TodoTaskDetailsIntent.SubmitNewTag).newTag)
    }

    @Test
    fun `отмена и смена задачи сбрасывают черновик тега, повторное создание начинает заново`() {
        val edited = reduce(
            loaded, TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.StartNewTag,
            TodoTaskDetailsIntent.NewTagChange("Срочно"), TodoTaskDetailsIntent.NewTagColorChange(5)
        )
        val cancelled = reduce(edited, TodoTaskDetailsIntent.CancelNewTag)
        assertNull(cancelled.newTag)
        val reopened = reduce(cancelled, TodoTaskDetailsIntent.StartNewTag)
        assertEquals("", reopened.newTag!!.title)
        assertEquals(2, reopened.newTag.colorIndex)
        assertFalse(reopened.newTag.titleError)
        assertNull(reduce(edited, TodoTaskDetailsIntent.Open(21)).newTag)
        assertNull(reduce(edited, TodoTaskDetailsIntent.Close).newTag)
    }

    @Test
    fun `быстрая подзадача очищает поле только после записи, удаление скрывает строку`() {
        val typed = reduce(loaded, TodoTaskDetailsIntent.QuickSubtaskChange("Мешки"), TodoTaskDetailsIntent.AddQuickSubtask)
        assertEquals("Мешки", typed.quickSubtask)
        assertEquals("", reduce(typed, TodoTaskDetailsIntent.QuickSubtaskAdded("Мешки")).quickSubtask)
        // Пока шла запись, начали новый текст — он остаётся
        val retyped = reduce(typed, TodoTaskDetailsIntent.QuickSubtaskChange("Вёдра"), TodoTaskDetailsIntent.QuickSubtaskAdded("Мешки"))
        assertEquals("Вёдра", retyped.quickSubtask)

        val deleted = reduce(loaded, TodoTaskDetailsIntent.DeleteSubtask(21))
        assertEquals(listOf(22L), deleted.data!!.subtasks.map { it.task.id })
    }

    @Test
    fun `связи — без самой задачи и уже связанных, удаление скрывает строку`() {
        val sheet = reduce(
            loaded,
            TodoTaskDetailsIntent.OpenLinkSheet,
            TodoTaskDetailsIntent.LinkTypeChange(TodoLinkType.BLOCKS),
            TodoTaskDetailsIntent.LinkSearchLoaded(listOf(task, other(23), other(24)))
        )
        assertEquals(TodoLinkType.BLOCKS, sheet.linkSheet!!.type)
        assertEquals(listOf(24L), sheet.linkCandidates.map { it.id })
        val added = reduce(sheet, TodoTaskDetailsIntent.AddLink(other(24)))
        assertNull(added.linkSheet)
        assertTrue(added.linkSearchResults.isEmpty())
        // Ответ поиска после закрытия шторки не нужен
        assertTrue(reduce(added, TodoTaskDetailsIntent.LinkSearchLoaded(listOf(other(25)))).linkSearchResults.isEmpty())

        assertTrue(reduce(loaded, TodoTaskDetailsIntent.DeleteLink(link)).data!!.links.isEmpty())
    }

    @Test
    fun `связать по умолчанию скрывает завершённые включая пользовательские статусы`() {
        val customDone = DomainTodoStatus(id = 8, idPT = 7, title = "Сдано", isDone = true)
        val sheet = reduce(
            loaded, TodoTaskDetailsIntent.Loaded(data.copy(statuses = listOf(planned, done, customDone))),
            TodoTaskDetailsIntent.OpenLinkSheet,
            TodoTaskDetailsIntent.LinkSearchLoaded(listOf(other(24), other(25).copy(statusId = 3), other(26).copy(statusId = 8)))
        )

        assertEquals(listOf(24L), sheet.linkCandidates.map { it.id })
    }

    @Test
    fun `завершённые исключаются до ограничения количества найденных задач`() {
        val tasks = (100L..160L).map { other(it).copy(statusId = 3) } + other(200)
        val sheet = reduce(loaded, TodoTaskDetailsIntent.OpenLinkSheet, TodoTaskDetailsIntent.LinkSearchLoaded(tasks))

        assertEquals(listOf(200L), sheet.linkCandidates.map { it.id })
    }

    @Test
    fun `все задачи сохраняет поиск и исключения а новое открытие сбрасывает фильтр`() {
        val doneTask = other(25).copy(statusId = done.id)
        val all = reduce(
            loaded, TodoTaskDetailsIntent.OpenLinkSheet,
            TodoTaskDetailsIntent.LinkQueryChange("Корм"),
            TodoTaskDetailsIntent.LinkSearchLoaded(listOf(task, other(23), other(24), doneTask)),
            TodoTaskDetailsIntent.LinkShowCompletedChange(true)
        )
        assertEquals("Корм", all.linkSheet!!.query)
        assertEquals(listOf(24L, 25L), all.linkCandidates.map { it.id })
        val searched = reduce(all, TodoTaskDetailsIntent.LinkSearchLoaded(listOf(doneTask)))
        assertTrue(searched.linkSheet!!.showCompleted)
        assertEquals(listOf(25L), searched.linkCandidates.map { it.id })
        val unfinished = reduce(searched, TodoTaskDetailsIntent.LinkShowCompletedChange(false))
        assertTrue(unfinished.linkCandidates.isEmpty())
        assertEquals("Корм", unfinished.linkSheet!!.query)
        val reopened = reduce(all, TodoTaskDetailsIntent.CloseLinkSheet, TodoTaskDetailsIntent.OpenLinkSheet)
        assertFalse(reopened.linkSheet!!.showCompleted)
    }

    @Test
    fun `обновление статуса найденной задачи и признака завершения пересчитывает фильтр`() {
        val candidate = other(24)
        val sheet = reduce(loaded, TodoTaskDetailsIntent.OpenLinkSheet, TodoTaskDetailsIntent.LinkSearchLoaded(listOf(candidate)))
        assertEquals(listOf(candidate), sheet.linkCandidates)
        val completed = reduce(sheet, TodoTaskDetailsIntent.LinkSearchLoaded(listOf(candidate.copy(statusId = done.id))))
        assertTrue(completed.linkCandidates.isEmpty())
        val changedStatus = reduce(sheet, TodoTaskDetailsIntent.Loaded(data.copy(statuses = listOf(planned.copy(isDone = true), done))))
        assertTrue(changedStatus.linkCandidates.isEmpty())
    }

    @Test
    fun `направление блокировки сохраняется при поиске и сбрасывается при новом открытии`() {
        val incoming = reduce(
            loaded,
            TodoTaskDetailsIntent.OpenLinkSheet,
            TodoTaskDetailsIntent.LinkTypeChange(TodoLinkType.BLOCKS, isOutgoing = false),
            TodoTaskDetailsIntent.LinkQueryChange("Корм")
        )
        assertEquals(TodoLinkType.BLOCKS, incoming.linkSheet!!.type)
        assertFalse(incoming.linkSheet.isOutgoing)
        assertEquals("Корм", incoming.linkSheet.query)
        assertEquals(
            DomainTodoLink(taskId = 24, linkedTaskId = 20, type = TodoLinkType.BLOCKS),
            incoming.linkSheet.toLink(20, 24)
        )

        val outgoing = reduce(incoming, TodoTaskDetailsIntent.LinkTypeChange(TodoLinkType.BLOCKS))
        assertTrue(outgoing.linkSheet!!.isOutgoing)
        assertEquals("Корм", outgoing.linkSheet.query)
        assertEquals(
            DomainTodoLink(taskId = 20, linkedTaskId = 24, type = TodoLinkType.BLOCKS),
            outgoing.linkSheet.toLink(20, 24)
        )

        val reopened = reduce(incoming, TodoTaskDetailsIntent.CloseLinkSheet, TodoTaskDetailsIntent.OpenLinkSheet)
        assertEquals(TodoLinkDraft(), reopened.linkSheet)
        assertNull(reduce(loaded, TodoTaskDetailsIntent.LinkTypeChange(TodoLinkType.BLOCKS, false)).linkSheet)
    }

    @Test
    fun `обычная связь не меняет стороны и не наследует направление блокировки`() {
        val relates = reduce(
            loaded,
            TodoTaskDetailsIntent.OpenLinkSheet,
            TodoTaskDetailsIntent.LinkTypeChange(TodoLinkType.BLOCKS, isOutgoing = false),
            TodoTaskDetailsIntent.LinkTypeChange(TodoLinkType.RELATES)
        )
        assertTrue(relates.linkSheet!!.isOutgoing)
        assertEquals(
            DomainTodoLink(taskId = 20, linkedTaskId = 24, type = TodoLinkType.RELATES),
            relates.linkSheet.toLink(20, 24)
        )
    }

    @Test
    fun `комментарии — отправка, правка с проверкой текста и удаление`() {
        assertEquals("", reduce(loaded, TodoTaskDetailsIntent.CommentTextChange("Привет"), TodoTaskDetailsIntent.SendComment).commentText)
        assertEquals(" ", reduce(loaded, TodoTaskDetailsIntent.CommentTextChange(" "), TodoTaskDetailsIntent.SendComment).commentText)

        val editing = reduce(loaded, TodoTaskDetailsIntent.StartEditComment(comment), TodoTaskDetailsIntent.EditCommentTextChange(""))
        val invalid = reduce(editing, TodoTaskDetailsIntent.SaveEditComment)
        assertEquals(R.string.todo_card_error_comment_empty, invalid.editingComment!!.textError)
        val fixed = reduce(invalid, TodoTaskDetailsIntent.EditCommentTextChange("Перезвонить"))
        assertNull(fixed.editingComment!!.textError)
        assertNull(reduce(fixed, TodoTaskDetailsIntent.SaveEditComment).editingComment)

        val asking = reduce(loaded, TodoTaskDetailsIntent.AskDeleteComment(comment))
        assertEquals(comment, asking.commentToDelete)
        assertNull(reduce(asking, TodoTaskDetailsIntent.ConfirmDeleteComment).commentToDelete)
    }

    @Test
    fun `удаление задачи — диалог, подтверждение закрывает шторку`() {
        val asking = reduce(loaded, TodoTaskDetailsIntent.ShowDeleteDialog)
        assertTrue(asking.showDeleteDialog)
        assertFalse(reduce(asking, TodoTaskDetailsIntent.DismissDeleteDialog).showDeleteDialog)
        assertTrue(reduce(asking, TodoTaskDetailsIntent.DismissDeleteDialog).isOpen)
        assertFalse(reduce(asking, TodoTaskDetailsIntent.ConfirmDelete).isOpen)
    }

    @Test
    fun `статус новой подзадачи — В планах, иначе первый по порядку`() {
        assertEquals(1L, listOf(done, planned).defaultStatusId())
        assertEquals(3L, listOf(done).defaultStatusId())
        assertNull(emptyList<DomainTodoStatus>().defaultStatusId())
    }
}
