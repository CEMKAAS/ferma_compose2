package com.zaroslikov.todo.ui.task_details

import androidx.annotation.StringRes
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.sortedForDisplay
import com.zaroslikov.todo.domain.model.normalizeRepeat
import com.zaroslikov.todo.domain.model.parseTodoDate
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.domain.model.normalizeTodoAssignees
import com.zaroslikov.todo.ui.lists.TodoTitleColorForm
import com.zaroslikov.todo.ui.theme.todoColorPalette

/** Ошибка текста комментария или `null`. */
@StringRes
internal fun validateCommentText(text: String): Int? =
    if (text.isBlank()) R.string.todo_card_error_comment_empty else null

/** Статус новой подзадачи — «В планах», если его нет — первый в порядке показа. */
internal fun List<DomainTodoStatus>.defaultStatusId(): Long? =
    (firstOrNull { it.defaultCode == TodoDefaultStatus.PLANNED } ?: sortedForDisplay().firstOrNull())?.id

object TodoTaskDetailsReducer {

    fun reducer(state: TodoTaskDetailsState, intent: TodoTaskDetailsIntent): TodoTaskDetailsState =
        when (intent) {
            // Другая задача — всё с чистого листа: черновики и шторки прежней задачи не нужны
            is TodoTaskDetailsIntent.Open ->
                if (intent.taskId == state.taskId) state.copy(backStack = emptyList())
                else TodoTaskDetailsState(taskId = intent.taskId, isLoading = true)

            is TodoTaskDetailsIntent.NavigateToTask ->
                if (intent.taskId == state.taskId) state
                else TodoTaskDetailsState(
                    taskId = intent.taskId,
                    isLoading = true,
                    backStack = state.backStack + listOfNotNull(state.taskId)
                )
            TodoTaskDetailsIntent.Back -> state.backStack.lastOrNull()?.let { taskId ->
                TodoTaskDetailsState(taskId = taskId, isLoading = true, backStack = state.backStack.dropLast(1))
            } ?: state

            TodoTaskDetailsIntent.Close -> TodoTaskDetailsState()

            is TodoTaskDetailsIntent.Loaded -> {
                val data = intent.data
                // Снимок прежней задачи мог прийти уже после переключения
                if (data.task.id != state.taskId) state
                else {
                    val old = state.data
                    state.copy(
                        isLoading = false,
                        data = data,
                        // Черновик, который правят прямо сейчас, не перетираем снимком из БД
                        title = if (old == null || state.title == old.task.title) data.task.title else state.title,
                        note = if (old == null || state.note == old.task.note) data.task.note else state.note
                    )
                }
            }

            is TodoTaskDetailsIntent.Gone ->
                if (intent.taskId == state.taskId) TodoTaskDetailsState() else state

            // Перевод строки в однострочном названии не нужен — его вставляет вставка из буфера
            is TodoTaskDetailsIntent.TitleChange -> state.copy(title = intent.title.replace('\n', ' '))
            is TodoTaskDetailsIntent.NoteChange -> state.copy(note = intent.note)
            TodoTaskDetailsIntent.CommitText -> state.data?.let { data ->
                state.copy(title = if (state.title.isBlank()) data.task.title else state.title.trim())
            } ?: state

            is TodoTaskDetailsIntent.SetDueDate ->
                state.withTask { it.copy(dueDate = intent.date, dueTime = if (intent.date == null) null else it.dueTime).normalizeRepeat() }
                    .let { if (intent.date == null) it.copy(repeatDraft = null) else it }
            TodoTaskDetailsIntent.OpenRepeatSheet -> state.data?.task?.let { task ->
                if (task.dueDate == null) state else state.copy(repeatDraft = TodoRepeatDraft(task.repeatPeriod, task.repeatEndDate))
            } ?: state
            TodoTaskDetailsIntent.CloseRepeatSheet -> state.copy(repeatDraft = null)
            is TodoTaskDetailsIntent.RepeatPeriodChange -> state.copy(repeatDraft = state.repeatDraft?.copy(
                period = intent.period, endDate = if (intent.period == TodoRepeatPeriod.NONE) null else state.repeatDraft.endDate,
                hasError = false
            ))
            is TodoTaskDetailsIntent.RepeatEndChange -> state.copy(repeatDraft = state.repeatDraft?.copy(endDate = intent.date, hasError = false))
            TodoTaskDetailsIntent.SaveRepeat -> {
                val draft = state.repeatDraft
                val due = parseTodoDate(state.data?.task?.dueDate)
                val end = parseTodoDate(draft?.endDate)
                if (draft == null || due == null) state
                else if (draft.period != TodoRepeatPeriod.NONE && draft.endDate != null && (end == null || end.isBefore(due)))
                    state.copy(repeatDraft = draft.copy(hasError = true))
                else state.withTask { it.copy(repeatPeriod = draft.period, repeatEndDate = draft.endDate).normalizeRepeat() }
                    .copy(repeatDraft = null)
            }
            // Время без даты не задаётся — переключатель времени в этом случае недоступен
            is TodoTaskDetailsIntent.SetDueTime ->
                state.withTask { if (it.dueDate == null) it else it.copy(dueTime = intent.time) }
            is TodoTaskDetailsIntent.SetPriority -> state.withTask { it.copy(priority = intent.priority) }
            is TodoTaskDetailsIntent.AssigneeNameChange -> state.copy(
                assigneeName = intent.name.replace('\n', ' '), showAssigneeChoices = true
            )
            TodoTaskDetailsIntent.OpenAssigneeChoices -> state.copy(showAssigneeChoices = true)
            TodoTaskDetailsIntent.CloseAssigneeChoices -> state.copy(showAssigneeChoices = false)
            is TodoTaskDetailsIntent.SelectAssignee ->
                if (intent.name !in state.assigneeCandidates) state else state.withTask {
                    it.copy(assignees = normalizeTodoAssignees(it.assignees + intent.name))
                }.copy(showAssigneeChoices = false)
            TodoTaskDetailsIntent.AddAssignee -> state.withTask {
                it.copy(assignees = normalizeTodoAssignees(it.assignees + state.assigneeName))
            }.copy(showAssigneeChoices = false)
            is TodoTaskDetailsIntent.RemoveAssignee -> state.withTask {
                it.copy(assignees = it.assignees.filterNot { name -> name.equals(intent.name, ignoreCase = true) })
            }
            is TodoTaskDetailsIntent.AssigneeAdded ->
                if (state.taskId == intent.taskId && state.assigneeName == intent.name) state.copy(assigneeName = "") else state
            // Перенос подтверждает БД вместе с названием списка и новыми подсписками.
            is TodoTaskDetailsIntent.SetList -> state
            // Подсписок — только у задачи верхнего уровня и только из подсписков её списка
            is TodoTaskDetailsIntent.SetSection -> state.data?.let { data ->
                val known = intent.sectionId == null || data.sections.any { it.id == intent.sectionId }
                if (data.isSubtask || !known) state else state.withTask { it.copy(sectionId = intent.sectionId) }
            } ?: state
            // Оптимистично меняем статус, БД подтвердит через Flow
            is TodoTaskDetailsIntent.SetStatus -> state.data?.let { data ->
                state.copy(
                    data = data.copy(
                        item = data.item.copy(
                            task = data.task.copy(statusId = intent.statusId),
                            status = data.statuses.firstOrNull { it.id == intent.statusId } ?: data.item.status
                        )
                    )
                )
            } ?: state
            is TodoTaskDetailsIntent.ToggleTag -> state.data?.let { data ->
                val draft = state.tagSelection
                if (draft != null) {
                    if (data.tags.none { it.id == intent.tagId }) state
                    else state.copy(tagSelection = draft.copy(selectedIds =
                        if (intent.tagId in draft.selectedIds) draft.selectedIds - intent.tagId
                        else draft.selectedIds + intent.tagId
                    ))
                } else state
            } ?: state

            TodoTaskDetailsIntent.OpenTagsSheet -> state.data?.let { data ->
                state.copy(tagSelection = TodoTagSelectionDraft(data.tagIds))
            } ?: state
            TodoTaskDetailsIntent.ResetTags -> state.copy(
                tagSelection = state.tagSelection?.copy(selectedIds = emptySet())
            )
            TodoTaskDetailsIntent.SaveTags -> {
                val draft = state.tagSelection
                val data = state.data
                if (draft == null || data == null) state
                else state.copy(
                    data = data.copy(item = data.item.copy(tags = data.tags.filter { it.id in draft.selectedIds })),
                    tagSelection = null, newTag = null
                )
            }
            TodoTaskDetailsIntent.CloseTagsSheet -> state.copy(tagSelection = null, newTag = null)
            is TodoTaskDetailsIntent.TagCreated -> {
                val draft = state.tagSelection
                val data = state.data
                if (state.taskId != intent.taskId || draft?.session !== intent.session || data == null) state
                else state.copy(
                    tagSelection = draft.copy(selectedIds = draft.selectedIds + intent.tag.id),
                    data = data.copy(tags = (data.tags.filterNot { it.id == intent.tag.id } + intent.tag).sortedBy { it.title.lowercase() })
                )
            }
            TodoTaskDetailsIntent.StartNewTag -> if (!state.showTagsSheet) state else state.copy(
                newTag = TodoTitleColorForm(colorIndex = (state.data?.tags?.size ?: 0) % todoColorPalette.size)
            )
            is TodoTaskDetailsIntent.NewTagChange -> state.copy(newTag = state.newTag?.withTitle(intent.title))
            is TodoTaskDetailsIntent.NewTagColorChange -> state.copy(newTag = state.newTag?.copy(colorIndex = intent.colorIndex))
            // Тег создаёт ViewModel, валидная форма закрывается — новый чип придёт из Flow
            TodoTaskDetailsIntent.SubmitNewTag -> state.copy(newTag = state.newTag?.submitted())
            TodoTaskDetailsIntent.CancelNewTag -> state.copy(newTag = null)

            is TodoTaskDetailsIntent.QuickSubtaskChange -> state.copy(quickSubtask = intent.title)
            // Поле очищает QuickSubtaskAdded после записи — при ошибке текст не теряется
            TodoTaskDetailsIntent.AddQuickSubtask -> state
            // Пока шла запись, пользователь мог начать новый текст — его не трогаем
            is TodoTaskDetailsIntent.QuickSubtaskAdded ->
                if (state.quickSubtask.trim() == intent.title) state.copy(quickSubtask = "") else state
            // Отметка подзадачи — только побочный эффект, список обновит Flow
            is TodoTaskDetailsIntent.ToggleSubtaskDone -> state
            is TodoTaskDetailsIntent.DeleteSubtask -> state.data?.let { data ->
                state.copy(data = data.copy(subtasks = data.subtasks.filter { it.task.id != intent.subtaskId }))
            } ?: state

            TodoTaskDetailsIntent.OpenLinkSheet -> state.copy(linkSheet = TodoLinkDraft())
            TodoTaskDetailsIntent.CloseLinkSheet -> state.copy(
                linkSheet = null, linkSearchResults = emptyList(), linkSearchLists = emptyMap()
            )
            is TodoTaskDetailsIntent.LinkTypeChange -> state.copy(
                linkSheet = state.linkSheet?.copy(type = intent.type, isOutgoing = intent.isOutgoing)
            )
            is TodoTaskDetailsIntent.LinkQueryChange -> state.copy(linkSheet = state.linkSheet?.copy(query = intent.query))
            is TodoTaskDetailsIntent.LinkShowCompletedChange ->
                state.copy(linkSheet = state.linkSheet?.copy(showCompleted = intent.showCompleted))
            // Поиск мог ответить после закрытия шторки — тогда результат не нужен
            is TodoTaskDetailsIntent.LinkSearchLoaded ->
                if (state.linkSheet == null) state else state.copy(
                    linkSearchResults = intent.tasks, linkSearchLists = intent.lists
                )
            is TodoTaskDetailsIntent.AddLink -> state.copy(
                linkSheet = null, linkSearchResults = emptyList(), linkSearchLists = emptyMap()
            )
            is TodoTaskDetailsIntent.DeleteLink -> state.data?.let { data ->
                state.copy(data = data.copy(links = data.links.filter { it.link.id != intent.link.link.id }))
            } ?: state

            is TodoTaskDetailsIntent.CommentTextChange -> state.copy(commentText = intent.text)
            TodoTaskDetailsIntent.SendComment ->
                if (state.commentText.isBlank()) state else state.copy(commentText = "")
            is TodoTaskDetailsIntent.StartEditComment ->
                state.copy(editingComment = TodoCommentDraft(id = intent.comment.id, text = intent.comment.text))
            is TodoTaskDetailsIntent.EditCommentTextChange ->
                state.copy(editingComment = state.editingComment?.copy(text = intent.text, textError = null))
            TodoTaskDetailsIntent.SaveEditComment -> state.editingComment?.let { draft ->
                val error = validateCommentText(draft.text)
                // Без ошибки шторка закрывается сразу, запись — побочным эффектом
                if (error == null) state.copy(editingComment = null)
                else state.copy(editingComment = draft.copy(textError = error))
            } ?: state
            TodoTaskDetailsIntent.CancelEditComment -> state.copy(editingComment = null)
            is TodoTaskDetailsIntent.AskDeleteComment -> state.copy(commentToDelete = intent.comment)
            TodoTaskDetailsIntent.DismissDeleteComment,
            TodoTaskDetailsIntent.ConfirmDeleteComment -> state.copy(commentToDelete = null)

            TodoTaskDetailsIntent.ShowDeleteDialog -> state.copy(showDeleteDialog = true)
            TodoTaskDetailsIntent.DismissDeleteDialog -> state.copy(showDeleteDialog = false)
            TodoTaskDetailsIntent.ConfirmDelete -> TodoTaskDetailsState()
        }

    /** Оптимистичная правка полей задачи; БД подтвердит через Flow. */
    private fun TodoTaskDetailsState.withTask(change: (DomainTodoTask) -> DomainTodoTask): TodoTaskDetailsState =
        data?.let { data -> copy(data = data.copy(item = data.item.copy(task = change(data.task)))) } ?: this
}
