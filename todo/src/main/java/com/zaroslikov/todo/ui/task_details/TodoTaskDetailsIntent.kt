package com.zaroslikov.todo.ui.task_details

import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoPriority

sealed interface TodoTaskDetailsIntent {
    /** Открыть шторку с экрана-хоста, начав новую историю переходов. */
    data class Open(val taskId: Long) : TodoTaskDetailsIntent
    /** Перейти к подзадаче, родителю или связи, оставив возможность вернуться. */
    data class NavigateToTask(val taskId: Long) : TodoTaskDetailsIntent
    data object Back : TodoTaskDetailsIntent
    /** «Готово» или смахивание шторки. */
    data object Close : TodoTaskDetailsIntent
    data class Loaded(val data: TodoTaskDetailsData) : TodoTaskDetailsIntent
    /** Задача [taskId] пропала из БД (удалена в другом месте) — шторка закрывается. */
    data class Gone(val taskId: Long) : TodoTaskDetailsIntent

    // Название и примечание: черновик, запись — по CommitText
    data class TitleChange(val title: String) : TodoTaskDetailsIntent
    data class NoteChange(val note: String) : TodoTaskDetailsIntent
    /** Потеря фокуса полем: пустое название не сохраняется, возвращается прежнее. */
    data object CommitText : TodoTaskDetailsIntent

    // Остальные поля сохраняются сразу
    /** `null` — срок выключен (вместе со временем). */
    data class SetDueDate(val date: String?) : TodoTaskDetailsIntent
    /** `null` — без времени; без даты время не ставится. */
    data class SetDueTime(val time: String?) : TodoTaskDetailsIntent
    data object OpenRepeatSheet : TodoTaskDetailsIntent
    data object CloseRepeatSheet : TodoTaskDetailsIntent
    data class RepeatPeriodChange(val period: com.zaroslikov.todo.domain.model.TodoRepeatPeriod) : TodoTaskDetailsIntent
    data class RepeatEndChange(val date: String?) : TodoTaskDetailsIntent
    data object SaveRepeat : TodoTaskDetailsIntent
    data class SetPriority(val priority: TodoPriority) : TodoTaskDetailsIntent
    data class AssigneeNameChange(val name: String) : TodoTaskDetailsIntent
    data object OpenAssigneeChoices : TodoTaskDetailsIntent
    data object CloseAssigneeChoices : TodoTaskDetailsIntent
    data class SelectAssignee(val name: String) : TodoTaskDetailsIntent
    data object AddAssignee : TodoTaskDetailsIntent
    data class RemoveAssignee(val name: String) : TodoTaskDetailsIntent
    data class AssigneeAdded(val taskId: Long, val name: String) : TodoTaskDetailsIntent
    data class SetStatus(val statusId: Long) : TodoTaskDetailsIntent
    data class ToggleTag(val tagId: Long) : TodoTaskDetailsIntent
    data object OpenTagsSheet : TodoTaskDetailsIntent
    data object CloseTagsSheet : TodoTaskDetailsIntent
    data object ResetTags : TodoTaskDetailsIntent
    data object SaveTags : TodoTaskDetailsIntent
    data class TagCreated(
        val taskId: Long,
        val tag: DomainTodoTag,
        val session: TodoTagSelectionSession
    ) : TodoTaskDetailsIntent
    /** Подсписок задачи верхнего уровня; `null` — «Без подсписка». */
    data class SetSection(val sectionId: Long?) : TodoTaskDetailsIntent
    /** Только задача верхнего уровня; перенос подтверждает поток БД. */
    data class SetList(val listId: Long) : TodoTaskDetailsIntent

    // Новый тег во вложенной шторке: название и цвет
    data object StartNewTag : TodoTaskDetailsIntent
    data class NewTagChange(val title: String) : TodoTaskDetailsIntent
    data class NewTagColorChange(val colorIndex: Int) : TodoTaskDetailsIntent
    /** Пустое название оставляет форму открытой с ошибкой поля. */
    data object SubmitNewTag : TodoTaskDetailsIntent
    data object CancelNewTag : TodoTaskDetailsIntent

    // Подзадачи
    data class QuickSubtaskChange(val title: String) : TodoTaskDetailsIntent
    data object AddQuickSubtask : TodoTaskDetailsIntent
    /** Подзадача [title] записана — поле можно очистить. */
    data class QuickSubtaskAdded(val title: String) : TodoTaskDetailsIntent
    data class ToggleSubtaskDone(val subtaskId: Long) : TodoTaskDetailsIntent
    data class DeleteSubtask(val subtaskId: Long) : TodoTaskDetailsIntent

    // Связи
    data object OpenLinkSheet : TodoTaskDetailsIntent
    data object CloseLinkSheet : TodoTaskDetailsIntent
    data class LinkTypeChange(val type: TodoLinkType, val isOutgoing: Boolean = true) : TodoTaskDetailsIntent
    data class LinkQueryChange(val query: String) : TodoTaskDetailsIntent
    data class LinkShowCompletedChange(val showCompleted: Boolean) : TodoTaskDetailsIntent
    data class LinkSearchLoaded(
        val tasks: List<DomainTodoTask>,
        val lists: Map<Long, DomainTodoList> = emptyMap()
    ) : TodoTaskDetailsIntent
    data class AddLink(val linkedTask: DomainTodoTask) : TodoTaskDetailsIntent
    data class DeleteLink(val link: DomainTodoLinkedTask) : TodoTaskDetailsIntent

    // Комментарии
    data class CommentTextChange(val text: String) : TodoTaskDetailsIntent
    data object SendComment : TodoTaskDetailsIntent
    data class StartEditComment(val comment: DomainTodoComment) : TodoTaskDetailsIntent
    data class EditCommentTextChange(val text: String) : TodoTaskDetailsIntent
    data object SaveEditComment : TodoTaskDetailsIntent
    data object CancelEditComment : TodoTaskDetailsIntent
    data class AskDeleteComment(val comment: DomainTodoComment) : TodoTaskDetailsIntent
    data object DismissDeleteComment : TodoTaskDetailsIntent
    data object ConfirmDeleteComment : TodoTaskDetailsIntent

    // Удаление задачи
    data object ShowDeleteDialog : TodoTaskDetailsIntent
    data object DismissDeleteDialog : TodoTaskDetailsIntent
    /** Шторка закрывается сразу, удаление — побочным эффектом. */
    data object ConfirmDelete : TodoTaskDetailsIntent
}
