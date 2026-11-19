package com.zaroslikov.todo.ui.list

import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoListViewMode
import com.zaroslikov.todo.domain.model.buildTodoSectionTree
import com.zaroslikov.todo.domain.model.isDone
import com.zaroslikov.todo.domain.model.todoInsertIndex
import com.zaroslikov.todo.domain.model.todoSectionIds
import com.zaroslikov.todo.domain.model.todoSubtaskIds
import com.zaroslikov.todo.domain.model.todoTopLevelIds
import com.zaroslikov.todo.domain.model.withTodoSectionMoved
import com.zaroslikov.todo.domain.model.withTodoSubtaskMoved
import com.zaroslikov.todo.domain.model.withTodoTaskMoved
import com.zaroslikov.todo.ui.lists.TodoTitleColorForm

fun todoListReduce(state: TodoListState, intent: TodoListIntent): TodoListState {
    val closesEditor = when (intent) {
        TodoListIntent.EditorSubmit, TodoListIntent.EditorInfo, TodoListIntent.StartNew,
        TodoListIntent.StartNewColumn, TodoListIntent.DragStarted, TodoListIntent.ToggleViewMode -> true
        is TodoListIntent.Search -> intent.query != state.query
        is TodoListIntent.EditorFinish -> state.editor?.taskId == intent.taskId
        is TodoListIntent.StartRename -> state.editor?.taskId != intent.taskId
        is TodoListIntent.StartNewInSection -> state.sections.any { it.id == intent.sectionId }
        is TodoListIntent.StartNewSubtask -> state.tasks.any { it.task.id == intent.parentId && it.task.parentId == null }
        is TodoListIntent.StartRenameSection -> state.sections.any { it.id == intent.sectionId } &&
            state.sectionEditor?.sectionId != intent.sectionId
        else -> false
    }
    return reduceTodoList(if (closesEditor) state.withEditorTextSaved() else state, intent)
}

private fun reduceTodoList(state: TodoListState, intent: TodoListIntent): TodoListState = when (intent) {
    is TodoListIntent.DataLoaded -> {
        val byId = intent.tasks.associateBy { it.task.id }
        // Удалённый тег не должен оставлять фильтр, который ничего не показывает
        val tagFilter = state.tagFilter?.takeIf { id -> intent.tags.any { it.id == id } }
        val sectionIds = intent.sections.map { it.id }.toSet()
        state.copy(
            isLoading = false,
            list = intent.list,
            tasks = intent.tasks,
            dataRevision = state.dataRevision + 1,
            textOverrides = state.textOverrides.filter { (id, text) ->
                byId[id]?.task?.let { !text.saved || it.title != text.title || it.note != text.note } == true
            },
            tags = intent.tags,
            sections = intent.sections,
            searchLabels = intent.searchLabels,
            tagFilter = tagFilter,
            // Оптимистичная отметка живёт, пока БД не вернула то же значение
            doneOverrides = state.doneOverrides.filter { (id, done) -> byId[id]?.let { it.isDone != done } == true },
            lingering = state.lingering.filterTo(mutableSetOf()) { it in byId },
            hiddenIds = state.hiddenIds.filterTo(mutableSetOf()) { it in byId },
            collapsed = state.collapsed.filterTo(mutableSetOf()) { it in byId },
            // Задачу, которую переименовывали, или подсписок новой задачи удалили в другом месте —
            // строка ввода закрывается. Подсписок, которого ещё не было (только что создан
            // из «Нового столбика»), строку не закрывает — он придёт следующей выгрузкой
            editor = state.editor?.takeIf {
                if (it.taskId == null) (it.parentId == null || it.parentId in byId) &&
                    (it.sectionId == null || it.sectionId in sectionIds ||
                    state.sections.none { section -> section.id == it.sectionId })
                else it.taskId in byId
            },
            sectionEditor = state.sectionEditor?.takeIf { it.sectionId == null || it.sectionId in sectionIds }
        ).withRows()
    }

    is TodoListIntent.ToggleDone -> {
        val item = state.tasks.firstOrNull { it.task.id == intent.taskId }
        if (item == null) state
        else {
            val done = !state.isDone(item)
            state.copy(
                doneOverrides = state.doneOverrides + (intent.taskId to done),
                // При скрытых завершённых строка ещё немного видна — повторный тап успевает отменить
                lingering = if (done && !state.showCompleted) state.lingering + intent.taskId
                else state.lingering - intent.taskId
            ).withRows()
        }
    }

    is TodoListIntent.HideLingering -> state.copy(lingering = state.lingering - intent.taskId).withRows()

    is TodoListIntent.ToggleDoneFailed -> state.copy(
        doneOverrides = state.doneOverrides - intent.taskId,
        lingering = state.lingering - intent.taskId
    ).withRows()

    TodoListIntent.ToggleShowCompleted -> state.copy(showCompleted = !state.showCompleted).withRows()

    is TodoListIntent.ToggleCollapse -> state.copy(
        collapsed = if (intent.taskId in state.collapsed) state.collapsed - intent.taskId
        else state.collapsed + intent.taskId
    ).withRows()

    // Введённый текст сохраняет ViewModel (до редьюсера); здесь только сама строка ввода.
    // Ввод задачи и названия подсписка одновременно не открыты
    TodoListIntent.StartNew -> state.copy(editor = TodoInlineEditor(null, ""), sectionEditor = null)

    is TodoListIntent.StartNewSubtask -> {
        val parent = state.tasks.firstOrNull { it.task.id == intent.parentId && it.task.parentId == null }?.task
        if (parent == null) state
        else state.copy(
            editor = TodoInlineEditor(null, "", parent.sectionId, parentId = parent.id),
            sectionEditor = null,
            collapsed = state.collapsed - parent.id
        ).withRows()
    }

    is TodoListIntent.StartNewInSection ->
        if (state.sections.none { it.id == intent.sectionId }) state
        else state.copy(
            editor = TodoInlineEditor(null, "", intent.sectionId),
            sectionEditor = null,
            // Строка ввода в свёрнутом подсписке не видна — подсписок разворачивается
            sections = state.sections.map { if (it.id == intent.sectionId) it.copy(isCollapsed = false) else it }
        ).withRows()

    is TodoListIntent.StartRename -> {
        val item = state.tasks.firstOrNull { it.task.id == intent.taskId }?.withText(state.textOverrides[intent.taskId])
        if (item == null || state.editor?.taskId == intent.taskId) state
        else state.copy(editor = TodoInlineEditor(intent.taskId, item.task.title, note = item.task.note), sectionEditor = null)
    }

    // Перевод строки в однострочном поле не нужен — его вставляет вставка из буфера
    is TodoListIntent.EditorTextChanged -> state.copy(
        editor = state.editor?.copy(text = intent.text.replace('\n', ' '))
    )

    is TodoListIntent.EditorNoteChanged -> state.copy(editor = state.editor?.copy(note = intent.note))

    TodoListIntent.EditorSubmit -> {
        val editor = state.editor
        state.copy(
            editor = if (editor?.taskId == null && editor != null && editor.text.isNotBlank()) editor.copy(text = "", note = "")
            else null
        )
    }

    is TodoListIntent.EditorFinish ->
        if (state.editor != null && state.editor.taskId == intent.taskId) state.copy(editor = null) else state

    TodoListIntent.EditorInfo -> state.copy(editor = null)

    is TodoListIntent.EditorSaveFailed ->
        if (state.textOverrides[intent.taskId] == intent.text)
            state.copy(textOverrides = state.textOverrides - intent.taskId).withRows()
        else state

    is TodoListIntent.EditorSaveSucceeded -> {
        val pending = state.textOverrides[intent.taskId]
        if (pending == null || pending.revision != intent.text.revision) state
        else {
            val raw = state.tasks.firstOrNull { it.task.id == intent.taskId }?.task
            // Поток мог опередить возврат из записи. Принимаем только снимок новее начала правки,
            // иначе старый raw A снял бы защиту у последовательности A → B → A.
            val confirmed = state.dataRevision > pending.dataRevision &&
                raw?.title == pending.title && raw.note == pending.note
            val overrides = if (confirmed) state.textOverrides - intent.taskId
                else state.textOverrides + (intent.taskId to pending.copy(saved = true))
            state.copy(textOverrides = overrides)
        }
    }

    is TodoListIntent.RequestDeleteTask -> {
        val item = state.tasks.firstOrNull { it.task.id == intent.taskId }
        when {
            item == null -> state
            item.needsDeleteConfirm -> state.copy(deleteCandidate = item)
            else -> state.copy(
                hiddenIds = state.hiddenIds + intent.taskId,
                editor = state.editor?.takeIf { it.taskId != intent.taskId }
            ).withRows()
        }
    }

    TodoListIntent.ConfirmDeleteTask -> {
        val id = state.deleteCandidate?.task?.id
        if (id == null) state
        else state.copy(
            deleteCandidate = null,
            hiddenIds = state.hiddenIds + id,
            editor = state.editor?.takeIf { it.taskId != id }
        ).withRows()
    }

    TodoListIntent.DismissDeleteTask -> state.copy(deleteCandidate = null)

    is TodoListIntent.DeleteTaskFailed -> state.copy(hiddenIds = state.hiddenIds - intent.taskId).withRows()

    TodoListIntent.RequestClearCompleted -> state.copy(showClearCompleted = state.completedCount > 0)
    TodoListIntent.ConfirmClearCompleted, TodoListIntent.DismissClearCompleted -> state.copy(showClearCompleted = false)

    is TodoListIntent.Search -> if (intent.query == state.query) state else {
        val updated = state.withSectionTitleSaved().copy(query = intent.query, editor = null)
        val withMetadata = if (intent.query.isBlank())
            updated.copy(searchText = emptyMap(), searchNow = null)
        else updated
        withMetadata.withRows()
    }

    is TodoListIntent.SearchMetadataLoaded -> if (state.query.isNotBlank())
        state.copy(searchText = intent.text, searchNow = intent.now).withRows()
    else state

    TodoListIntent.OpenFilters -> state.copy(showFilterSheet = true)
    TodoListIntent.CloseFilters -> state.copy(showFilterSheet = false)

    is TodoListIntent.SelectPriority -> state.copy(
        priorityFilter = intent.priority?.takeIf { it != state.priorityFilter }
    ).withRows()

    is TodoListIntent.SelectTag -> state.copy(
        tagFilter = intent.tagId?.takeIf { it != state.tagFilter }
    ).withRows()

    TodoListIntent.ResetFilters -> state.copy(query = "", priorityFilter = null, tagFilter = null).withRows()

    is TodoListIntent.ToggleSectionCollapse -> state.copy(
        sections = state.sections.map { if (it.id == intent.sectionId) it.copy(isCollapsed = !it.isCollapsed) else it }
    ).withRows()

    TodoListIntent.OpenNewSection -> state.copy(sectionForm = TodoSectionForm())

    is TodoListIntent.SectionFormTitleChanged ->
        state.copy(sectionForm = state.sectionForm?.copy(title = intent.title.replace('\n', ' '), titleError = false))

    // Запись делает ViewModel; невалидная форма остаётся с ошибкой
    TodoListIntent.SaveSectionForm -> state.copy(
        sectionForm = state.sectionForm?.let { if (it.isValid) null else it.copy(titleError = true) }
    )

    TodoListIntent.CloseSectionForm -> state.copy(sectionForm = null)

    TodoListIntent.StartNewColumn -> state.copy(editor = null, sectionEditor = TodoSectionEditor(null, ""))

    is TodoListIntent.StartRenameSection -> {
        val section = state.sections.firstOrNull { it.id == intent.sectionId }
        if (section == null || state.sectionEditor?.sectionId == intent.sectionId) state
        else state.copy(editor = null, sectionEditor = TodoSectionEditor(section.id, section.title))
    }

    is TodoListIntent.SectionTitleChanged ->
        state.copy(sectionEditor = state.sectionEditor?.copy(title = intent.title.replace('\n', ' ')))

    is TodoListIntent.SectionTitleFinish -> state.withSectionTitleSaved()

    // Пока писали в БД, пользователь мог начать другой ввод — тогда его не перебиваем
    is TodoListIntent.ColumnCreated ->
        if (state.editor != null || state.sectionEditor != null) state
        else state.copy(editor = TodoInlineEditor(null, "", intent.sectionId))

    is TodoListIntent.RequestDeleteSection ->
        state.copy(sectionToDelete = state.sections.firstOrNull { it.id == intent.sectionId })

    TodoListIntent.DismissDeleteSection -> state.copy(sectionToDelete = null)

    // Оптимистично: подсписок пропадает, его задачи — «без подсписка», как сделает ON DELETE SET NULL
    TodoListIntent.ConfirmDeleteSection -> state.sectionToDelete?.let { section ->
        state.copy(
            sectionToDelete = null,
            sections = state.sections.filter { it.id != section.id },
            tasks = state.tasks.map {
                if (it.task.sectionId == section.id) it.copy(task = it.task.copy(sectionId = null)) else it
            },
            editor = state.editor?.takeIf { it.taskId != null || it.sectionId != section.id },
            sectionEditor = state.sectionEditor?.takeIf { it.sectionId != section.id }
        ).withRows()
    } ?: state

    // В конец подсписка, как сделает репозиторий; если задача уже в нём — ничего
    is TodoListIntent.MoveTask ->
        if (state.tasks.firstOrNull { it.task.id == intent.taskId }?.task?.sectionId == intent.sectionId) state
        else state.copy(
            tasks = state.tasks.withTodoTaskMoved(intent.taskId, intent.sectionId, Int.MAX_VALUE, state.sectionIds)
        ).withRows()

    // Ввод сохраняет ViewModel; переименование подсписка видно сразу, как по Enter
    TodoListIntent.DragStarted -> state.withSectionTitleSaved().copy(editor = null)

    // Место среди видимых переводится в место среди всех задач подсписка: скрытые
    // завершённые и не прошедшие фильтр остаются на своих местах относительно видимых
    is TodoListIntent.DropTask -> {
        val visible = state.groups.firstOrNull { it.section?.id == intent.sectionId }?.cards.orEmpty()
            .map { it.item.task.id } - intent.taskId
        val full = state.tasks.todoTopLevelIds(intent.sectionId, state.sectionIds) - intent.taskId
        val index = todoInsertIndex(full, visible, intent.index)
        state.copy(tasks = state.tasks.withTodoTaskMoved(intent.taskId, intent.sectionId, index, state.sectionIds)).withRows()
    }

    is TodoListIntent.DropSubtask -> {
        val parentId = state.tasks.firstOrNull { it.task.id == intent.taskId }?.task?.parentId
        if (parentId == null) state
        else {
            val visible = state.rows.filter { it.isSubtask && it.item.task.parentId == parentId }
                .map { it.item.task.id } - intent.taskId
            val index = todoInsertIndex(state.tasks.todoSubtaskIds(parentId) - intent.taskId, visible, intent.index)
            state.copy(tasks = state.tasks.withTodoSubtaskMoved(intent.taskId, index)).withRows()
        }
    }

    is TodoListIntent.DropSection -> {
        val visible = state.groups.mapNotNull { it.section?.id } - intent.sectionId
        val index = todoInsertIndex(state.sections.todoSectionIds() - intent.sectionId, visible, intent.index)
        state.copy(sections = state.sections.withTodoSectionMoved(intent.sectionId, index)).withRows()
    }

    // Откат только порядка: отметки, названия и сворачивание остаются как есть
    is TodoListIntent.ReorderFailed -> {
        val tasks = intent.tasks.associateBy { it.task.id }
        val sections = intent.sections.associateBy { it.id }
        state.copy(
            tasks = state.tasks.map { item ->
                val old = tasks[item.task.id]?.task ?: return@map item
                item.copy(task = item.task.copy(sectionId = old.sectionId, position = old.position))
            },
            sections = state.sections.map { section ->
                sections[section.id]?.let { section.copy(position = it.position) } ?: section
            }
        ).withRows()
    }

    // Введённое сохраняет ViewModel; строка ввода закрывается — у другого вида своя разметка
    TodoListIntent.ToggleViewMode -> state.list?.let { list ->
        val mode = if (list.viewMode == TodoListViewMode.LIST) TodoListViewMode.COLUMNS else TodoListViewMode.LIST
        state.copy(list = list.copy(viewMode = mode), editor = null, sectionEditor = null)
    } ?: state

    TodoListIntent.OpenEditList -> state.copy(
        form = state.list?.let { TodoTitleColorForm(id = it.id, title = it.title, colorIndex = it.colorIndex, icon = it.icon) }
    )

    is TodoListIntent.FormTitleChanged -> state.copy(form = state.form?.withTitle(intent.title))
    is TodoListIntent.FormColorChanged -> state.copy(form = state.form?.copy(colorIndex = intent.colorIndex))
    is TodoListIntent.FormIconChanged -> state.copy(form = state.form?.copy(icon = intent.icon))
    TodoListIntent.CloseForm -> state.copy(form = null)
    TodoListIntent.SaveForm -> state.copy(form = state.form?.submitted())

    TodoListIntent.RequestDeleteList -> state.copy(showDeleteList = true)
    TodoListIntent.DismissDeleteList, TodoListIntent.ConfirmDeleteList -> state.copy(showDeleteList = false)
}

/** id известных подсписков: задача с другим подсписком показывается «без подсписка». */
private val TodoListState.sectionIds: Set<Long> get() = sections.map { it.id }.toSet()

private fun DomainTodoTaskItem.withText(text: TodoTaskText?): DomainTodoTaskItem =
    if (text == null) this else copy(task = task.copy(title = text.title, note = text.note))

private fun TodoListState.withEditorTextSaved(): TodoListState {
    val editor = editor ?: return this
    val id = editor.taskId ?: return this
    val old = tasks.firstOrNull { it.task.id == id }?.withText(textOverrides[id]) ?: return this
    val revision = textRevision + 1
    val text = TodoTaskText(editor.text.trim().ifEmpty { old.task.title }, editor.note.trim(), revision, dataRevision = dataRevision)
    return copy(textOverrides = textOverrides + (id to text), textRevision = revision).withRows()
}

/**
 * Закрывает поле названия подсписка: у существующего непустое название применяется сразу
 * (запись делает ViewModel), пустое — отмена; новый подсписок создаёт ViewModel.
 */
private fun TodoListState.withSectionTitleSaved(): TodoListState {
    val editor = sectionEditor ?: return this
    val title = editor.title.trim()
    return if (editor.sectionId != null && title.isNotEmpty())
        copy(
            sectionEditor = null,
            sections = sections.map { if (it.id == editor.sectionId) it.copy(title = title) else it }
        ).withRows()
    else copy(sectionEditor = null)
}

/**
 * Пересчитывает видимые строки по подспискам ([buildTodoSectionTree]). Задача видна, если не
 * завершена, или завершённые показаны, или её только что отметили. Скрытая задача скрывает и
 * свои подзадачи. С фильтрами задача остаётся, если подходит она сама (тогда видны все её
 * подзадачи) или хотя бы одна подзадача (тогда видны только подходящие); подсписки без
 * подходящих задач при фильтрах скрываются. Свёрнутый подсписок не даёт строк вида «список»,
 * но его карточки для вида «столбцы» остаются. Поиск показывает и завершённые задачи
 * и раскрывает совпадения без изменения сохранённой свёрнутости.
 */
private fun TodoListState.withRows(): TodoListState {
    // Удалённая свайпом задача уносит и свои подзадачи (в БД они уйдут каскадом)
    val present = tasks.filter { it.task.id !in hiddenIds && it.task.parentId !in hiddenIds }
        .map { it.withText(textOverrides[it.task.id]) }
    val terms = todoSearchTerms(query)
    val searching = terms.isNotEmpty()
    fun shown(item: DomainTodoTaskItem) = showCompleted || !isDone(item) || item.task.id in lingering
    val groups = buildTodoSectionTree(sections, present).mapNotNull { group ->
        val cards = mutableListOf<TodoListRow>()
        val rows = mutableListOf<TodoListRow>()
        val sectionTitle = group.section?.title
        group.nodes.forEach { node ->
            if (!searching && !shown(node.item)) return@forEach
            val parentMatches = matches(node.item, terms, sectionTitle)
            val children = node.subtasks.filter {
                (shown(it) && parentMatches) ||
                    ((searching || shown(it)) && matches(it, terms, sectionTitle))
            }
            if (!parentMatches && children.isEmpty()) return@forEach
            val isCollapsed = !searching && node.item.task.id in collapsed && children.isNotEmpty()
            val row = TodoListRow(node.item, isDone(node.item), isSubtask = false, children.size, isCollapsed)
            cards += row
            rows += row
            if (!isCollapsed) children.forEach { rows += TodoListRow(it, isDone(it), isSubtask = true) }
        }
        val section = group.section
        when {
            section != null && hasFilters && cards.isEmpty() -> null
            !searching && section?.isCollapsed == true ->
                TodoListGroup(section, emptyList(), cards, isCollapsed = true, cardRows = rows)
            else -> TodoListGroup(section, rows, cards)
        }
    }
    return copy(rows = groups.flatMap { it.rows }, groups = groups, completedCount = present.count { isDone(it) })
}

private fun TodoListState.matches(item: DomainTodoTaskItem, terms: List<String>, sectionTitle: String?): Boolean {
    if (priorityFilter != null && item.task.priority != priorityFilter) return false
    if (tagFilter != null && item.tags.none { it.id == tagFilter }) return false
    return terms.isEmpty() || matchesTodoSearch(item, terms, sectionTitle)
}
