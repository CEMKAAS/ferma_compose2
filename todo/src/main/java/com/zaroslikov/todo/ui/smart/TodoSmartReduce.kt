package com.zaroslikov.todo.ui.smart

import com.zaroslikov.todo.domain.model.inSmartList
import com.zaroslikov.todo.domain.model.isDone
import com.zaroslikov.todo.domain.model.todoSmartSections

fun todoSmartReduce(state: TodoSmartState, intent: TodoSmartIntent): TodoSmartState = when (intent) {
    is TodoSmartIntent.DataLoaded -> {
        val byId = intent.tasks.associateBy { it.id }
        state.copy(
            isLoading = false,
            tasks = intent.tasks,
            now = intent.now,
            // Оптимистичная отметка живёт, пока БД не вернула то же значение
            doneOverrides = state.doneOverrides.filter { (id, done) -> byId[id]?.let { it.item.isDone != done } == true },
            lingering = state.lingering.filterTo(mutableSetOf()) { it in byId }
        ).withSections()
    }

    is TodoSmartIntent.ToggleDone -> {
        val task = state.tasks.firstOrNull { it.id == intent.taskId }
        if (task == null) state
        else {
            val done = !state.isDone(task)
            // Задача перестала подходить виду — ещё немного видна, повторный тап успевает отменить
            val stillIn = task.inSmartList(state.kind, state.now, done)
            state.copy(
                doneOverrides = state.doneOverrides + (intent.taskId to done),
                lingering = if (stillIn) state.lingering - intent.taskId else state.lingering + intent.taskId
            ).withSections()
        }
    }

    is TodoSmartIntent.HideLingering -> state.copy(lingering = state.lingering - intent.taskId).withSections()

    is TodoSmartIntent.ToggleDoneFailed -> state.copy(
        doneOverrides = state.doneOverrides - intent.taskId,
        lingering = state.lingering - intent.taskId
    ).withSections()

    TodoSmartIntent.OpenNewTask ->
        if (state.canAddTask && state.newTask == null) state.copy(newTask = TodoSmartTaskDraft()) else state

    is TodoSmartIntent.NewTaskTitleChanged ->
        state.copy(newTask = state.newTask?.copy(title = intent.title.replace('\n', ' ')))

    is TodoSmartIntent.NewTaskNoteChanged ->
        state.copy(newTask = state.newTask?.copy(note = intent.note))

    is TodoSmartIntent.SubmitNewTask -> state.copy(
        newTask = if (intent.action == NewTaskAction.KEEP_EDITING && state.newTask?.title?.isNotBlank() == true)
            TodoSmartTaskDraft()
        else null
    )

    // Не затираем следующий черновик, если пользователь уже начал его вводить.
    is TodoSmartIntent.NewTaskAddFailed ->
        if (state.newTask == null || state.newTask == TodoSmartTaskDraft()) state.copy(newTask = intent.draft)
        else state
}

private fun TodoSmartState.withSections(): TodoSmartState = copy(
    sections = todoSmartSections(
        tasks = tasks,
        kind = kind,
        now = now,
        isDone = { isDone(it) },
        keep = { it.id in lingering }
    )
)
