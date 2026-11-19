package com.zaroslikov.todo.ui.smart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.domain.model.todoMinuteTicker
import com.zaroslikov.todo.domain.model.todoToday
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import com.zaroslikov.todo.ui.base.TodoBaseViewModel
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.navigation.TodoSmartDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Сколько отмеченная задача ещё видна в умном списке — как в «Напоминаниях». */
private const val LINGER_MILLIS = 1500L

/** Умный список проекта: «Сегодня», «Запланировано», «Все» или «Завершено». */
@HiltViewModel
class TodoSmartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TodoTaskRepository
) : TodoBaseViewModel<TodoSmartState, TodoSmartIntent>(
    // Неизвестный код (ссылка из новой версии) — открываем «Все», а не падаем
    TodoSmartState(
        kind = TodoSmartList.fromCode(savedStateHandle[TodoSmartDestination.kind]) ?: TodoSmartList.ALL
    ),
    ::todoSmartReduce
) {

    private val idPT: Long = checkNotNull(savedStateHandle[TodoSmartDestination.itemIdPT])
    private val _openTask = Channel<Long>(Channel.BUFFERED)
    val openTask: Flow<Long> = _openTask.receiveAsFlow()

    /** Таймеры исчезновения только что отмеченных задач; повторный тап таймер отменяет. */
    private val lingerJobs = mutableMapOf<Long, Job>()

    init {
        viewModelScope.launch {
            combine(
                taskRepository.getProjectTasks(idPT),
                // «Сегодня» и просрочка зависят от текущей минуты
                todoMinuteTicker()
            ) { tasks, now -> TodoSmartIntent.DataLoaded(tasks, now) }
                .catch {
                    updateState { it.copy(isLoading = false) }
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
                }
                .collect { sendIntent(it) }
        }
    }

    override fun onIntent(intent: TodoSmartIntent) {
        val before = getState()
        sendIntent(intent)
        when (intent) {
            is TodoSmartIntent.ToggleDone -> before.tasks.firstOrNull { it.id == intent.taskId }?.let {
                setDone(intent.taskId, !before.isDone(it))
            }

            // Из «Сегодня» — сразу со сроком сегодня, иначе задача не появилась бы на экране
            is TodoSmartIntent.SubmitNewTask -> {
                val form = before.newTask
                if (form != null && form.title.isNotBlank()) launchSafe(
                    onError = { sendIntent(TodoSmartIntent.NewTaskAddFailed(form)) }
                ) {
                    val taskId = taskRepository.quickAddToProject(
                        idPT = idPT,
                        title = form.title,
                        dueDate = todoToday(),
                        defaultListTitle = intent.defaultListTitle,
                        note = form.note
                    )
                    if (intent.action == NewTaskAction.OPEN_DETAILS) _openTask.send(taskId)
                }
            }

            else -> Unit
        }
    }

    private fun setDone(taskId: Long, done: Boolean) {
        launchSafe(onError = { sendIntent(TodoSmartIntent.ToggleDoneFailed(taskId)) }) {
            taskRepository.setDone(taskId, done)
        }
        lingerJobs.remove(taskId)?.cancel()
        if (taskId in getState().lingering)
            lingerJobs[taskId] = viewModelScope.launch {
                delay(LINGER_MILLIS)
                lingerJobs.remove(taskId)
                sendIntent(TodoSmartIntent.HideLingering(taskId))
            }
    }
}
