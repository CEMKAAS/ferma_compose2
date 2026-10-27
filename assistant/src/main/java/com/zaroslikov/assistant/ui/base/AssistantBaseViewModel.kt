package com.zaroslikov.assistant.ui.base

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.assistant.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Одноразовые события экрана помощника. */
sealed interface AssistantUiEvent {
    data object NavigateBack : AssistantUiEvent
    data class Message(@StringRes val resId: Int) : AssistantUiEvent

    /** Появилось новое сообщение — прокрутить список вниз. */
    data object ScrollToBottom : AssistantUiEvent
}

/**
 * Минимальная MVI-база (как `CrmBaseViewModel`): экран шлёт всё через [onIntent], ViewModel сначала
 * прогоняет интент через чистый [reducer] ([sendIntent]), затем выполняет побочные эффекты.
 */
abstract class AssistantBaseViewModel<S, I>(
    initialState: S,
    private val reducer: (S, I) -> S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // Channel: событие, отправленное до подписки экрана, дождётся подписчика
    private val _events = Channel<AssistantUiEvent>(Channel.BUFFERED)
    val events: Flow<AssistantUiEvent> = _events.receiveAsFlow()

    abstract fun onIntent(intent: I)

    protected fun updateState(update: (S) -> S) = _state.update(update)

    protected fun getState(): S = _state.value

    protected fun sendIntent(intent: I) = _state.update { reducer(it, intent) }

    protected fun emitEvent(event: AssistantUiEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    /** Работа с БД «выстрелил и забыл»: ошибка не роняет приложение, а показывается сообщением. */
    protected fun launchSafe(onError: (() -> Unit)? = null, block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onError?.invoke()
                emitEvent(AssistantUiEvent.Message(R.string.assistant_msg_error))
            }
        }
    }
}
