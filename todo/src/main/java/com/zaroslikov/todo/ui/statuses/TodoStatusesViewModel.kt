package com.zaroslikov.todo.ui.statuses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.repository.TodoStatusRepository
import com.zaroslikov.todo.ui.base.TodoBaseViewModel
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.navigation.TodoStatusesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoStatusesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val statusRepository: TodoStatusRepository
) : TodoBaseViewModel<TodoStatusesState, TodoStatusesIntent>(TodoStatusesState(), ::todoStatusesReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[TodoStatusesDestination.itemIdPT])

    /** Последние статусы из БД — ими возвращаем оптимистично скрытую строку, если удаление не прошло. */
    private var loadedStatuses: List<DomainTodoStatus> = emptyList()

    init {
        viewModelScope.launch {
            statusRepository.getStatuses(idPT)
                .onStart { statusRepository.ensureDefaults(idPT) }
                .catch {
                    updateState { it.copy(isLoading = false) }
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
                }
                .collect {
                    loadedStatuses = it
                    sendIntent(TodoStatusesIntent.DataLoaded(it))
                }
        }
    }

    override fun onIntent(intent: TodoStatusesIntent) {
        // Форму, правимый статус и статус для удаления берём до редьюсера — он их сбрасывает
        val form = getState().form
        val editing = getState().editingStatus
        val statusToDelete = getState().statusToDelete
        sendIntent(intent)
        when (intent) {
            TodoStatusesIntent.SaveForm -> if (form != null && form.isValid) launchSafe {
                // У существующего статуса сохраняем позицию, код и признак завершения;
                // если его успели удалить — не создаём вместо него новый
                val base = if (form.isNew) DomainTodoStatus(idPT = idPT, title = "")
                else editing ?: return@launchSafe
                statusRepository.upsert(
                    base.copy(
                        title = if (form.titleEditable) form.title.trim() else base.title,
                        colorIndex = form.colorIndex
                    )
                )
            }

            TodoStatusesIntent.ConfirmDelete -> if (statusToDelete != null) launchSafe(
                onError = { sendIntent(TodoStatusesIntent.DataLoaded(loadedStatuses)) }
            ) {
                val deleted = statusRepository.delete(statusToDelete)
                // Дефолтный статус не удалился — Flow не обновится, возвращаем строку сами
                if (!deleted) sendIntent(TodoStatusesIntent.DataLoaded(loadedStatuses))
                emitEvent(
                    TodoUiEvent.Message(if (deleted) R.string.todo_msg_status_deleted else R.string.todo_msg_default_status)
                )
            }

            else -> Unit
        }
    }
}
