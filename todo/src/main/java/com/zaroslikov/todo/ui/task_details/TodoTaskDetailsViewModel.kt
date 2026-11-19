package com.zaroslikov.todo.ui.task_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.sortedForDisplay
import com.zaroslikov.todo.domain.model.isDone
import com.zaroslikov.todo.domain.model.normalizeTodoAssignees
import com.zaroslikov.todo.domain.repository.TodoCommentRepository
import com.zaroslikov.todo.domain.repository.TodoLinkRepository
import com.zaroslikov.todo.domain.repository.TodoListRepository
import com.zaroslikov.todo.domain.repository.TodoSectionRepository
import com.zaroslikov.todo.domain.repository.TodoStatusRepository
import com.zaroslikov.todo.domain.repository.TodoTagRepository
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import com.zaroslikov.todo.ui.base.TodoBaseViewModel
import com.zaroslikov.todo.ui.base.TodoUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/** Ключ SavedStateHandle: открытая задача шторки переживает пересоздание процесса. */
private const val KEY_TASK_ID = "todo_details_task_id"
private const val KEY_TASK_BACK_STACK = "todo_details_back_stack"

/**
 * Шторка «Подробно» задачи. Живёт на экране-хосте (список, умный список); задачу получает
 * не из навигации, а интентом [TodoTaskDetailsIntent.Open] и умеет переключаться между задачами.
 */
@HiltViewModel
class TodoTaskDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val listRepository: TodoListRepository,
    private val taskRepository: TodoTaskRepository,
    private val statusRepository: TodoStatusRepository,
    private val tagRepository: TodoTagRepository,
    private val linkRepository: TodoLinkRepository,
    private val commentRepository: TodoCommentRepository,
    private val sectionRepository: TodoSectionRepository
) : TodoBaseViewModel<TodoTaskDetailsState, TodoTaskDetailsIntent>(
    initialState = TodoTaskDetailsState(
        taskId = savedStateHandle.get<Long>(KEY_TASK_ID),
        isLoading = savedStateHandle.get<Long>(KEY_TASK_ID) != null,
        backStack = if (savedStateHandle.get<Long>(KEY_TASK_ID) != null)
            savedStateHandle.get<LongArray>(KEY_TASK_BACK_STACK)?.toList().orEmpty() else emptyList()
    ),
    reducer = TodoTaskDetailsReducer::reducer
) {

    /** Строка поиска шторки связи; `null` — шторка закрыта и поиск не нужен. */
    private val linkQuery = MutableStateFlow<String?>(null)

    private var loadJob: Job? = null

    /** Последние данные из БД — к ним откатываем оптимистичные изменения при ошибке. */
    private var loadedData: TodoTaskDetailsData? = null

    /**
     * Записи полей задачи по очереди: каждая читает свежую строку и пишет её целиком,
     * параллельные записи затёрли бы друг друга.
     */
    private val writeMutex = Mutex()

    init {
        getState().taskId?.let(::load)
    }

    override fun onIntent(intent: TodoTaskDetailsIntent) {
        // Reducer сбрасывает черновики и подтверждения — запоминаем их заранее
        val before = getState()
        sendIntent(intent)
        val after = getState()
        val taskId = before.taskId
        when (intent) {
            is TodoTaskDetailsIntent.Open,
            is TodoTaskDetailsIntent.NavigateToTask,
            TodoTaskDetailsIntent.Back -> {
                savedStateHandle[KEY_TASK_ID] = after.taskId
                savedStateHandle[KEY_TASK_BACK_STACK] = after.backStack.toLongArray()
                if (before.taskId != after.taskId) {
                    commitText(before)
                    after.taskId?.let(::load)
                }
            }

            TodoTaskDetailsIntent.Close -> {
                commitText(before)
                stopLoading()
            }

            is TodoTaskDetailsIntent.Gone -> if (!after.isOpen) stopLoading()

            TodoTaskDetailsIntent.CommitText -> commitText(before)

            is TodoTaskDetailsIntent.SetDueDate,
            is TodoTaskDetailsIntent.SetDueTime -> after.data?.task?.let { task ->
                saveTask(task.id) { it.copy(dueDate = task.dueDate, dueTime = task.dueTime,
                    repeatPeriod = task.repeatPeriod, repeatEndDate = task.repeatEndDate) }
            }
            TodoTaskDetailsIntent.SaveRepeat -> if (before.repeatDraft != null && after.repeatDraft == null)
                after.data?.task?.let { task ->
                    saveTask(task.id) { it.copy(repeatPeriod = task.repeatPeriod, repeatEndDate = task.repeatEndDate) }
                }

            is TodoTaskDetailsIntent.SetPriority -> taskId?.let { id ->
                saveTask(id) { it.copy(priority = intent.priority) }
            }

            TodoTaskDetailsIntent.AddAssignee -> {
                val name = before.assigneeName
                val task = before.data?.task
                if (task != null && name.isNotBlank() && task.assignees.none { it.equals(name.trim(), ignoreCase = true) })
                    addAssignee(task.id, name, before.assigneeName)
            }
            is TodoTaskDetailsIntent.SelectAssignee -> before.data?.task?.let { task ->
                if (intent.name in before.assigneeCandidates) addAssignee(task.id, intent.name, before.assigneeName)
            }
            is TodoTaskDetailsIntent.RemoveAssignee -> taskId?.let { id ->
                launchSafe(onError = ::rollback) {
                    writeMutex.withLock {
                        val fresh = taskRepository.getTask(id).first()?.task ?: return@withLock
                        taskRepository.setAssignees(id, fresh.assignees.filterNot { it.equals(intent.name, ignoreCase = true) })
                    }
                }
            }

            is TodoTaskDetailsIntent.SetSection -> {
                val data = before.data
                if (data != null && after.data?.task?.sectionId != data.task.sectionId)
                    launchSafe(onError = ::rollback) { taskRepository.setSection(data.task.id, intent.sectionId) }
            }

            is TodoTaskDetailsIntent.SetList -> before.data?.let { data ->
                if (!data.isSubtask && intent.listId != data.task.listId &&
                    data.projectLists.any { it.id == intent.listId && it.idPT == data.idPT }) {
                    launchSafe(onError = ::rollback) {
                        writeMutex.withLock { taskRepository.setList(data.task.id, intent.listId) }
                    }
                }
            }

            is TodoTaskDetailsIntent.SetStatus -> taskId?.let { id ->
                launchSafe(onError = ::rollback) { taskRepository.setStatus(id, intent.statusId) }
            }

            TodoTaskDetailsIntent.SaveTags -> before.tagSelection?.let { draft ->
                taskId?.let { id -> saveTask(id, tags = { draft.selectedIds }) }
            }

            TodoTaskDetailsIntent.SubmitNewTag -> {
                val form = before.newTag
                val data = before.data
                val session = before.tagSelection?.session
                if (form != null && form.isValid && data != null && session != null)
                    addTag(data, form.title.trim(), form.colorIndex, session)
            }

            TodoTaskDetailsIntent.AddQuickSubtask -> {
                val title = before.quickSubtask.trim()
                val data = before.data
                if (title.isNotEmpty() && data != null && !data.isSubtask) addSubtask(data, title)
            }
            is TodoTaskDetailsIntent.ToggleSubtaskDone ->
                before.data?.subtasks?.firstOrNull { it.task.id == intent.subtaskId }?.let { subtask ->
                    launchSafe { taskRepository.setDone(intent.subtaskId, !subtask.isDone) }
                }
            is TodoTaskDetailsIntent.DeleteSubtask -> launchSafe(onError = ::rollback) {
                val deletion = taskRepository.deleteWithUndo(intent.subtaskId) ?: return@launchSafe
                emitEvent(TodoUiEvent.Message(R.string.todo_list_msg_task_deleted) {
                    launchSafe { deletion.undo() }
                })
            }

            TodoTaskDetailsIntent.OpenLinkSheet -> linkQuery.value = ""
            is TodoTaskDetailsIntent.LinkQueryChange -> if (linkQuery.value != null) linkQuery.value = intent.query
            TodoTaskDetailsIntent.CloseLinkSheet -> linkQuery.value = null
            is TodoTaskDetailsIntent.AddLink -> {
                linkQuery.value = null
                val draft = before.linkSheet ?: return
                val id = taskId ?: return
                val link = draft.toLink(id, intent.linkedTask.id)
                launchSafe {
                    val added = linkRepository.addLink(link.taskId, link.linkedTaskId, link.type)
                    emitEvent(
                        TodoUiEvent.Message(if (added) R.string.todo_card_msg_link_added else R.string.todo_msg_link_exists)
                    )
                }
            }
            is TodoTaskDetailsIntent.DeleteLink -> launchSafe(onError = ::rollback) {
                linkRepository.deleteLink(intent.link.link.id)
                emitEvent(TodoUiEvent.Message(R.string.todo_card_msg_link_deleted))
            }

            TodoTaskDetailsIntent.SendComment -> {
                val text = before.commentText.trim()
                if (text.isNotEmpty() && taskId != null) launchSafe { commentRepository.add(taskId, text) }
            }
            TodoTaskDetailsIntent.SaveEditComment -> {
                val draft = before.editingComment ?: return
                if (validateCommentText(draft.text) == null)
                    launchSafe { commentRepository.update(draft.id, draft.text.trim()) }
            }
            TodoTaskDetailsIntent.ConfirmDeleteComment -> before.commentToDelete?.let { comment ->
                launchSafe {
                    commentRepository.delete(comment.id)
                    emitEvent(TodoUiEvent.Message(R.string.todo_card_msg_comment_deleted))
                }
            }

            TodoTaskDetailsIntent.ConfirmDelete -> taskId?.let { id ->
                // Останавливаем сбор раньше удаления: исчезновение задачи из Flow уже не важно
                stopLoading()
                launchSafe {
                    val deletion = taskRepository.deleteWithUndo(id) ?: return@launchSafe
                    emitEvent(TodoUiEvent.Message(R.string.todo_list_msg_task_deleted) {
                        launchSafe { deletion.undo() }
                    })
                }
            }

            else -> Unit
        }
    }

    private fun stopLoading() {
        loadJob?.cancel()
        loadJob = null
        loadedData = null
        linkQuery.value = null
        savedStateHandle[KEY_TASK_ID] = null
        savedStateHandle[KEY_TASK_BACK_STACK] = null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun load(taskId: Long) {
        loadJob?.cancel()
        loadedData = null
        linkQuery.value = null
        loadJob = viewModelScope.launch {
            val first = try {
                taskRepository.getTask(taskId).first()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onLoadError(taskId)
                return@launch
            }
            if (first == null) {
                onIntent(TodoTaskDetailsIntent.Gone(taskId))
                return@launch
            }
            // Проект и родитель не меняются; список может измениться при переносе.
            val listId = first.task.listId
            val parentId = first.task.parentId
            val idPT = try {
                val list = listRepository.getList(listId).first()
                if (list == null) {
                    onIntent(TodoTaskDetailsIntent.Gone(taskId))
                    return@launch
                }
                statusRepository.ensureDefaults(list.idPT)
                list.idPT
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onLoadError(taskId)
                return@launch
            }
            launch { observeLinkSearch(idPT) }

            val taskPart = taskRepository.getTask(taskId).flatMapLatest { item ->
                if (item == null) flowOf(TaskPart(null, null, null, emptyList()))
                else combine(
                    if (parentId != null) taskRepository.getTask(parentId).map { it?.task } else flowOf(null),
                    listRepository.getList(item.task.listId),
                    // Подсписок бывает только у задачи верхнего уровня
                    if (parentId == null) sectionRepository.getSections(item.task.listId) else flowOf(emptyList())
                ) { parent, list, sections -> TaskPart(item, parent, list, sections) }
            }
            val detailsPart = combine(
                statusRepository.getStatuses(idPT),
                tagRepository.getTags(idPT),
                // У подзадачи своих подзадач не бывает
                if (parentId == null) taskRepository.getSubtasks(taskId) else flowOf(emptyList()),
                linkRepository.getLinks(taskId),
                commentRepository.getComments(taskId)
            ) { statuses, tags, subtasks, links, comments -> DetailsPart(statuses, tags, subtasks, links, comments) }

            var failed = false
            val projectPart = combine(taskRepository.getProjectAssignees(idPT), listRepository.getLists(idPT)) { assignees, lists ->
                assignees to lists.map { it.list }
            }
            combine(taskPart, detailsPart, projectPart) { task, details, project ->
                Triple(task, details, project)
            }
                // Задачу удалили (например, вместе со списком) — закрываем шторку и прекращаем сбор
                .takeWhile { (task, _) -> task.item != null && task.list != null }
                .catch {
                    failed = true
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
                }
                .collect { (task, details, project) ->
                    if (getState().taskId != taskId) return@collect
                    val list = checkNotNull(task.list)
                    val loaded = TodoTaskDetailsData(
                        item = checkNotNull(task.item),
                        parent = task.parent,
                        listTitle = list.title,
                        listColorIndex = list.colorIndex,
                        idPT = idPT,
                        statuses = details.statuses.sortedForDisplay(),
                        tags = details.tags,
                        subtasks = details.subtasks,
                        links = details.links,
                        comments = details.comments,
                        sections = task.sections,
                        projectAssignees = project.first,
                        projectLists = project.second
                    )
                    loadedData = loaded
                    sendIntent(TodoTaskDetailsIntent.Loaded(loaded))
                }
            // После ошибки шторка остаётся с последними данными
            if (!failed) onIntent(TodoTaskDetailsIntent.Gone(taskId))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun observeLinkSearch(idPT: Long) {
        linkQuery
            .flatMapLatest { query ->
                if (query == null) emptyFlow() else combine(
                    taskRepository.searchProjectTasks(idPT, query),
                    listRepository.getLists(idPT)
                ) { tasks, lists ->
                    TodoTaskDetailsIntent.LinkSearchLoaded(tasks, lists.associate { it.list.id to it.list })
                }
            }
            .catch { emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error)) }
            .collect { sendIntent(it) }
    }

    private fun onLoadError(taskId: Long) {
        emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
        onIntent(TodoTaskDetailsIntent.Gone(taskId))
    }

    /** Откат оптимистичного изменения к последнему снимку из БД. */
    private fun rollback() {
        loadedData?.let { sendIntent(TodoTaskDetailsIntent.Loaded(it)) }
    }

    /** Выбор сохранённого имени и ввод нового используют одну последовательную запись. */
    private fun addAssignee(taskId: Long, name: String, draftName: String) {
        launchSafe(onError = ::rollback) {
            writeMutex.withLock {
                val fresh = taskRepository.getTask(taskId).first()?.task ?: return@withLock
                taskRepository.setAssignees(taskId, normalizeTodoAssignees(fresh.assignees + name))
                sendIntent(TodoTaskDetailsIntent.AssigneeAdded(taskId, draftName))
            }
        }
    }

    /**
     * Сохраняет черновики названия и примечания задачи из [state] (до редьюсера: при закрытии
     * и переключении он их сбрасывает). Пустое название не сохраняется.
     */
    private fun commitText(state: TodoTaskDetailsState) {
        val task = state.data?.task ?: return
        val title = state.title.trim()
        val note = state.note.trim()
        val renamed = title.isNotEmpty() && title != task.title
        val noteChanged = note != task.note.trim()
        if (!renamed && !noteChanged) return
        saveTask(task.id) { fresh ->
            fresh.copy(title = if (renamed) title else fresh.title, note = if (noteChanged) note else fresh.note)
        }
    }

    /**
     * Читает свежую задачу и её теги, применяет [change] и [tags] и пишет одним `upsert`.
     * Строка задачи не заменяется (DAO без REPLACE) — подзадачи, связи и комментарии целы.
     */
    private fun saveTask(
        taskId: Long,
        tags: (Set<Long>) -> Set<Long> = { it },
        change: (DomainTodoTask) -> DomainTodoTask = { it }
    ) {
        launchSafe(onError = ::rollback) {
            writeMutex.withLock {
                val item = taskRepository.getTask(taskId).first() ?: return@withLock
                taskRepository.upsert(change(item.task), tags(taskRepository.getTaskTagIds(taskId)))
            }
        }
    }

    /** В выборе тег создаётся в проекте и отмечается в черновике; привязка — по «Сохранить». */
    private fun addTag(data: TodoTaskDetailsData, title: String, colorIndex: Int, session: TodoTagSelectionSession) {
        val taskId = data.task.id
        launchSafe {
            val tagId = tagRepository.upsert(
                DomainTodoTag(idPT = data.idPT, title = title, colorIndex = colorIndex)
            )
            val tag = tagRepository.getTags(data.idPT).first().firstOrNull { it.id == tagId }
                ?: return@launchSafe
            sendIntent(TodoTaskDetailsIntent.TagCreated(taskId, tag, session))
        }
    }

    /** Поле очищается только после записи: без статусов или при ошибке текст остаётся. */
    private fun addSubtask(data: TodoTaskDetailsData, title: String) {
        val statusId = data.statuses.defaultStatusId()
        if (statusId == null) {
            emitEvent(TodoUiEvent.Message(R.string.todo_msg_error))
            return
        }
        launchSafe {
            // Список подзадачи репозиторий всё равно возьмёт у родителя
            taskRepository.upsert(
                DomainTodoTask(listId = data.task.listId, parentId = data.task.id, title = title, statusId = statusId),
                emptySet()
            )
            sendIntent(TodoTaskDetailsIntent.QuickSubtaskAdded(title))
        }
    }

    private data class TaskPart(
        val item: DomainTodoTaskItem?,
        val parent: DomainTodoTask?,
        val list: DomainTodoList?,
        val sections: List<DomainTodoSection>
    )

    private data class DetailsPart(
        val statuses: List<DomainTodoStatus>,
        val tags: List<DomainTodoTag>,
        val subtasks: List<DomainTodoTaskItem>,
        val links: List<DomainTodoLinkedTask>,
        val comments: List<DomainTodoComment>
    )
}
