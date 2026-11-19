package com.zaroslikov.todo.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoTaskBlockedException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Минимальная MVI-база экранов задач: экран шлёт всё через [onIntent], ViewModel сначала
 * прогоняет интент через чистый [reducer] ([sendIntent]), затем выполняет побочные эффекты.
 */
abstract class TodoBaseViewModel<S, I>(
    initialState: S,
    private val reducer: (S, I) -> S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // Channel, а не SharedFlow: событие, отправленное до подписки экрана (например, NavigateBack
    // при загрузке), дождётся подписчика и не потеряется
    private val _events = Channel<TodoUiEvent>(Channel.BUFFERED)
    val events: Flow<TodoUiEvent> = _events.receiveAsFlow()

    abstract fun onIntent(intent: I)

    protected fun updateState(update: (S) -> S) {
        _state.update(update)
    }

    protected fun getState(): S = _state.value

    protected fun sendIntent(intent: I) {
        _state.update { reducer(it, intent) }
    }

    protected fun emitEvent(event: TodoUiEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    /**
     * Запись в БД «выстрелил и забыл»: ошибка не роняет приложение, а показывается сообщением.
     * [onError] откатывает оптимистичное изменение состояния.
     */
    protected fun launchSafe(onError: () -> Unit = {}, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onError()
                emitEvent(TodoUiEvent.Message(
                    if (e is TodoTaskBlockedException) R.string.todo_msg_task_blocked else R.string.todo_msg_error
                ))
            }
        }
    }
}
