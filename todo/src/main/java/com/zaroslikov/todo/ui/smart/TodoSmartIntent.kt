package com.zaroslikov.todo.ui.smart

import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import java.time.LocalDateTime

sealed interface TodoSmartIntent {
    /** [tasks] — все задачи и подзадачи проекта; отбор по виду делает редьюсер. */
    data class DataLoaded(
        val tasks: List<DomainTodoProjectTask>,
        val now: LocalDateTime
    ) : TodoSmartIntent

    // Отметка выполнения: строка ещё ~1,5 с на экране, повторный тап отменяет
    data class ToggleDone(val taskId: Long) : TodoSmartIntent
    data class HideLingering(val taskId: Long) : TodoSmartIntent
    data class ToggleDoneFailed(val taskId: Long) : TodoSmartIntent

    // Ввод новой задачи в списке «Сегодня»
    data object OpenNewTask : TodoSmartIntent
    data class NewTaskTitleChanged(val title: String) : TodoSmartIntent
    data class NewTaskNoteChanged(val note: String) : TodoSmartIntent
    data class SubmitNewTask(
        val defaultListTitle: String,
        val action: NewTaskAction = NewTaskAction.KEEP_EDITING
    ) : TodoSmartIntent
    data class NewTaskAddFailed(val draft: TodoSmartTaskDraft) : TodoSmartIntent
}

enum class NewTaskAction { KEEP_EDITING, FINISH, OPEN_DETAILS }
