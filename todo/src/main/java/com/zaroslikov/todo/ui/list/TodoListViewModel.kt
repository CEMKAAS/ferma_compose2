package com.zaroslikov.todo.ui.list

import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.todoMinuteTicker
import com.zaroslikov.todo.domain.model.todoSectionIds
import com.zaroslikov.todo.domain.model.todoSubtaskIds
import com.zaroslikov.todo.domain.model.todoTopLevelIds
import com.zaroslikov.todo.domain.repository.TodoCommentRepository
import com.zaroslikov.todo.domain.repository.TodoLinkRepository
import com.zaroslikov.todo.domain.repository.TodoListRepository
import com.zaroslikov.todo.domain.repository.TodoSectionRepository
import com.zaroslikov.todo.domain.repository.TodoStatusRepository
import com.zaroslikov.todo.domain.repository.TodoTagRepository
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import com.zaroslikov.todo.ui.base.TodoBaseViewModel
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.ui.navigation.TodoListDestination
import com.zaroslikov.todo.util.isoDateTimeToDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Ключ SavedStateHandle: «показывать завершённые» переживает пересоздание экрана. */
private const val KEY_SHOW_COMPLETED = "todo_list_show_completed"

/** Сколько отмеченная задача ещё видна при скрытых завершённых — как в «Напоминаниях». */
private const val LINGER_MILLIS = 1500L

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val listRepository: TodoListRepository,
    private val taskRepository: TodoTaskRepository,
    private val statusRepository: TodoStatusRepository,
    private val sectionRepository: TodoSectionRepository,
    tagRepository: TodoTagRepository,
    commentRepository: TodoCommentRepository,
    linkRepository: TodoLinkRepository,
    @ApplicationContext context: Context
) : TodoBaseViewModel<TodoListState, TodoListIntent>(
    TodoListState(showCompleted = savedStateHandle[KEY_SHOW_COMPLETED] ?: false),
    ::todoListReduce
) {

    private val listId: Long = checkNotNull(savedStateHandle[TodoListDestination.listId])
    private val textSaveMutex = Mutex()

    /** Дефолтные статусы проекта создаются один раз за жизнь экрана. */
    private var defaultsEnsured = false

    /** Список пропал (удалён здесь или в другом месте) — уходим назад ровно один раз. */
    private var closed = false

    /** Таймеры исчезновения только что отмеченных задач; повторный тап таймер отменяет. */
    private val lingerJobs = mutableMapOf<Long, Job>()

    // Открыть карточку после «ⓘ»: id новой задачи известен только после вставки
    private val _openTask = Channel<Long>(Channel.BUFFERED)
    val openTask: Flow<Long> = _openTask.receiveAsFlow()

    init {
        val searchLabels = todoSearchLabelIds().associateWith { context.getString(it) }
        val searchText = combine(
            commentRepository.getListComments(listId),
            linkRepository.getListLinks(listId)
        ) { comments, links ->
            val byTask = mutableMapOf<Long, MutableList<String>>()
            comments.forEach { comment ->
                val fields = byTask.getOrPut(comment.taskId) { mutableListOf() }
                fields += comment.text
                listOfNotNull(comment.createdAt, comment.editedAt).forEach { dateTime ->
                    fields += dateTime
                    fields += isoDateTimeToDisplay(dateTime)
                }
            }
            links.forEach { link ->
                val taskId = if (link.isOutgoing) link.link.taskId else link.link.linkedTaskId
                val fields = byTask.getOrPut(taskId) { mutableListOf() }
                fields += link.task.title
                fields += link.listTitle
                searchLabels[link.link.type.titleRes(link.isOutgoing)]?.let { fields += it }
            }
            byTask
        }.distinctUntilChanged()

        viewModelScope.launch {
            // Проект (idPT) известен только из самого списка
            listRepository.getList(listId)
                .flatMapLatest { list ->
                    if (list == null) flowOf(null)
                    else {
                        if (!defaultsEnsured) {
                            statusRepository.ensureDefaults(list.idPT)
                            defaultsEnsured = true
                        }
                        combine(
                            taskRepository.getAllTasks(listId),
                            tagRepository.getTags(list.idPT),
                            sectionRepository.getSections(listId)
                        ) { tasks, tags, sections ->
                            TodoListIntent.DataLoaded(list, tasks, tags, sections, searchLabels)
                        }
                    }
                }
                .catch {
                    updateState { it.copy(isLoading = false) }
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
                }
                .collect { loaded ->
                    if (loaded != null) sendIntent(loaded)
                    else if (!closed) {
                        closed = true
                        emitEvent(TodoUiEvent.NavigateBack)
                    }
                }
        }
        viewModelScope.launch {
            state.map { it.query.isNotBlank() }.distinctUntilChanged()
                .flatMapLatest { hasQuery ->
                    if (!hasQuery) emptyFlow()
                    else combine(searchText, todoMinuteTicker()) { text, now ->
                        TodoListIntent.SearchMetadataLoaded(text, now)
                    }
                        .catch { emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error)) }
                }
                .collect(::sendIntent)
        }
    }

    override fun onIntent(intent: TodoListIntent) {
        // Форму, строку ввода и кандидата на удаление берём до редьюсера — он их сбрасывает
        val before = getState()
        sendIntent(intent)
        when (intent) {
            is TodoListIntent.ToggleDone -> before.tasks.firstOrNull { it.task.id == intent.taskId }?.let {
                setDone(intent.taskId, !before.isDone(it))
            }

            TodoListIntent.ToggleShowCompleted -> savedStateHandle[KEY_SHOW_COMPLETED] = getState().showCompleted

            is TodoListIntent.Search -> if (intent.query != before.query) {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
            }

            // Начать новую строку, пока открыта другая, — сохранить то, что уже введено
            TodoListIntent.StartNew -> {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
            }
            is TodoListIntent.StartNewSubtask -> {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
            }
            is TodoListIntent.StartNewInSection -> {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
                if (before.sections.firstOrNull { it.id == intent.sectionId }?.isCollapsed == true)
                    launchSafe { sectionRepository.setCollapsed(intent.sectionId, false) }
            }
            is TodoListIntent.StartRename -> {
                if (before.editor?.taskId != intent.taskId) commit(before.editor)
                commitSectionTitle(before, startTask = false)
            }

            TodoListIntent.StartNewColumn -> {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
            }
            is TodoListIntent.StartRenameSection -> if (before.sectionEditor?.sectionId != intent.sectionId) {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
            }
            is TodoListIntent.SectionTitleFinish -> commitSectionTitle(before, startTask = intent.startTask)

            TodoListIntent.EditorSubmit -> commit(before.editor)

            is TodoListIntent.EditorFinish ->
                if (before.editor != null && before.editor.taskId == intent.taskId) commit(before.editor)

            TodoListIntent.EditorInfo -> openDetails(before.editor)

            is TodoListIntent.RequestDeleteTask ->
                // Без подтверждения редьюсер уже скрыл строку — удаляем сразу
                if (intent.taskId !in before.hiddenIds && intent.taskId in getState().hiddenIds) deleteTask(intent.taskId)

            TodoListIntent.ConfirmDeleteTask -> before.deleteCandidate?.let { deleteTask(it.task.id) }

            TodoListIntent.ConfirmClearCompleted -> launchSafe { taskRepository.deleteCompleted(listId) }

            is TodoListIntent.ToggleSectionCollapse ->
                getState().sections.firstOrNull { it.id == intent.sectionId }?.let { section ->
                    launchSafe { sectionRepository.setCollapsed(section.id, section.isCollapsed) }
                }

            TodoListIntent.SaveSectionForm -> {
                val form = before.sectionForm
                if (form != null && form.isValid) launchSafe { sectionRepository.add(listId, form.title) }
            }

            TodoListIntent.ConfirmDeleteSection -> before.sectionToDelete?.let { section ->
                launchSafe {
                    sectionRepository.delete(section.id)
                    emitEvent(TodoUiEvent.Message(R.string.todo_section_msg_deleted))
                }
            }

            is TodoListIntent.MoveTask -> if (getState().tasks != before.tasks)
                saveOrder(before) { taskRepository.setSection(intent.taskId, intent.sectionId) }

            // Перенос во время ввода: сначала сохраняется введённое
            TodoListIntent.DragStarted -> {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
            }

            // Позиция в БД — место задачи среди всех задач подсписка после оптимистичного переноса
            is TodoListIntent.DropTask -> {
                val after = getState()
                if (after.tasks != before.tasks) {
                    val known = after.sections.map { it.id }.toSet()
                    val index = after.tasks.todoTopLevelIds(intent.sectionId, known).indexOf(intent.taskId)
                    saveOrder(before) { taskRepository.moveTask(intent.taskId, intent.sectionId, index) }
                }
            }

            is TodoListIntent.DropSubtask -> {
                val after = getState()
                val parentId = after.tasks.firstOrNull { it.task.id == intent.taskId }?.task?.parentId
                if (parentId != null && after.tasks != before.tasks)
                    saveOrder(before) { taskRepository.reorderSubtasks(parentId, after.tasks.todoSubtaskIds(parentId)) }
            }

            is TodoListIntent.DropSection -> {
                val after = getState()
                if (after.sections != before.sections)
                    saveOrder(before) { sectionRepository.reorderSections(listId, after.sections.todoSectionIds()) }
            }

            TodoListIntent.ToggleViewMode -> {
                commit(before.editor)
                commitSectionTitle(before, startTask = false)
                getState().list?.let { list -> launchSafe { listRepository.setViewMode(listId, list.viewMode) } }
            }

            TodoListIntent.SaveForm -> {
                val list = before.list
                val form = before.form
                if (list != null && form != null && form.isValid) launchSafe {
                    listRepository.upsert(list.copy(title = form.title.trim(), colorIndex = form.colorIndex, icon = form.icon))
                }
            }

            // Назад уйдём, когда Flow списка вернёт null, — так не будет двойного popBackStack
            TodoListIntent.ConfirmDeleteList -> launchSafe {
                listRepository.delete(listId)
            }

            else -> Unit
        }
    }

    private fun setDone(taskId: Long, done: Boolean) {
        launchSafe(onError = { sendIntent(TodoListIntent.ToggleDoneFailed(taskId)) }) {
            taskRepository.setDone(taskId, done)
        }
        lingerJobs.remove(taskId)?.cancel()
        if (taskId in getState().lingering)
            lingerJobs[taskId] = viewModelScope.launch {
                delay(LINGER_MILLIS)
                lingerJobs.remove(taskId)
                sendIntent(TodoListIntent.HideLingering(taskId))
            }
    }

    /** Сохраняет строку ввода: новая задача — если текст не пустой, переименование — если изменилось. */
    private fun commit(editor: TodoInlineEditor?) {
        if (editor == null) return
        val text = editor.text.trim()
        if (editor.taskId == null) {
            if (text.isNotEmpty()) launchSafe { createTask(editor) }
        }
        else {
            val old = getState().tasks.firstOrNull { it.task.id == editor.taskId }?.task
            val saved = getState().textOverrides[editor.taskId]
                ?: TodoTaskText(text.ifEmpty { old?.title.orEmpty() }, editor.note.trim())
            if (saved.title.isNotEmpty())
                launchSafe(onError = { sendIntent(TodoListIntent.EditorSaveFailed(editor.taskId, saved)) }) {
                    saveText(editor.taskId, saved)
                }
        }
    }

    /**
     * Сохраняет название подсписка: пустое — отмена, новый подсписок создаётся и при [startTask]
     * сразу получает строку ввода первой задачи, у существующего — переименование, если изменилось.
     * [before] — состояние до редьюсера: он закрывает поле и уже показывает новое название.
     */
    private fun commitSectionTitle(before: TodoListState, startTask: Boolean) {
        val editor = before.sectionEditor
        val title = editor?.title?.trim().orEmpty()
        if (editor == null || title.isEmpty()) return
        if (editor.sectionId == null) launchSafe {
            val id = sectionRepository.add(listId, title)
            if (startTask) sendIntent(TodoListIntent.ColumnCreated(id))
        }
        else {
            val old = before.sections.firstOrNull { it.id == editor.sectionId }?.title
            if (title != old) launchSafe { sectionRepository.rename(editor.sectionId, title) }
        }
    }

    /** Запись нового порядка; ошибка возвращает порядок [before] — снимок до переноса. */
    private fun saveOrder(before: TodoListState, block: suspend () -> Unit) {
        launchSafe(onError = { sendIntent(TodoListIntent.ReorderFailed(before.tasks, before.sections)) }, block = block)
    }

    private fun openDetails(editor: TodoInlineEditor?) {
        if (editor == null) return
        val text = editor.text.trim()
        if (editor.taskId == null) {
            if (text.isNotEmpty()) launchSafe {
                _openTask.send(createTask(editor))
            }
        } else {
            val old = getState().tasks.firstOrNull { it.task.id == editor.taskId }?.task
            val saved = getState().textOverrides[editor.taskId]
                ?: TodoTaskText(text.ifEmpty { old?.title.orEmpty() }, editor.note.trim())
            launchSafe(onError = { sendIntent(TodoListIntent.EditorSaveFailed(editor.taskId, saved)) }) {
                if (saved.title.isNotEmpty()) saveText(editor.taskId, saved)
                _openTask.send(editor.taskId)
            }
        }
    }

    private suspend fun createTask(editor: TodoInlineEditor): Long =
        if (editor.parentId == null)
            taskRepository.quickAdd(listId, editor.text.trim(), sectionId = editor.sectionId, note = editor.note)
        else taskRepository.upsert(
            com.zaroslikov.todo.domain.model.DomainTodoTask(
                listId = listId, parentId = editor.parentId, title = editor.text.trim(),
                // Репозиторий подставляет «В планах» проекта для отсутствующего статуса.
                note = editor.note.trim(), statusId = 0
            ),
            emptySet()
        )

    private fun deleteTask(taskId: Long) {
        launchSafe(onError = { sendIntent(TodoListIntent.DeleteTaskFailed(taskId)) }) {
            val deletion = taskRepository.deleteWithUndo(taskId) ?: return@launchSafe
            emitEvent(TodoUiEvent.Message(R.string.todo_list_msg_task_deleted) {
                launchSafe {
                    deletion.undo()
                    sendIntent(TodoListIntent.DeleteTaskFailed(taskId))
                }
            })
        }
    }

    /** Быстрые повторные правки записываются в том же порядке, в котором закрывались редакторы. */
    private suspend fun saveText(taskId: Long, text: TodoTaskText) = textSaveMutex.withLock {
        taskRepository.updateText(taskId, text.title, text.note)
        sendIntent(TodoListIntent.EditorSaveSucceeded(taskId, text))
    }
}
