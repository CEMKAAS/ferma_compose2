package com.zaroslikov.todo.ui.lists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.todoMinuteTicker
import com.zaroslikov.todo.domain.repository.TodoListRepository
import com.zaroslikov.todo.domain.repository.TodoStatusRepository
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import com.zaroslikov.todo.ui.base.TodoBaseViewModel
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.navigation.TodoDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Главный экран задач проекта: плитки умных списков и «Мои списки». Всё считается из одного
 * потока задач проекта и списков.
 */
@HiltViewModel
class TodoListsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val listRepository: TodoListRepository,
    private val taskRepository: TodoTaskRepository,
    private val statusRepository: TodoStatusRepository
) : TodoBaseViewModel<TodoListsState, TodoListsIntent>(TodoListsState(), ::todoListsReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[TodoDestination.itemIdPT])

    /** Последние данные из БД — ими возвращаем оптимистично скрытый список при ошибке удаления. */
    private var loaded: TodoListsIntent.DataLoaded? = null

    init {
        viewModelScope.launch {
            combine(
                listRepository.getLists(idPT),
                taskRepository.getProjectTasks(idPT),
                // «Сегодня» и просрочка зависят от текущей минуты
                todoMinuteTicker()
            ) { lists, tasks, now -> TodoListsIntent.DataLoaded(lists.map { it.list }, tasks, now) }
                // Дефолтные статусы нужны до первой задачи — создаём их при входе в модуль
                .onStart { statusRepository.ensureDefaults(idPT) }
                .catch {
                    updateState { it.copy(isLoading = false) }
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
                }
                .collect {
                    loaded = it
                    sendIntent(it)
                }
        }
    }

    override fun onIntent(intent: TodoListsIntent) {
        // Формы и список для удаления берём до редьюсера — он их сбрасывает
        val before = getState()
        sendIntent(intent)
        when (intent) {
            TodoListsIntent.SaveForm -> {
                val form = before.form
                if (form != null && form.isValid) launchSafe {
                    listRepository.upsert(
                        DomainTodoList(
                            id = form.id,
                            idPT = idPT,
                            title = form.title.trim(),
                            colorIndex = form.colorIndex,
                            icon = form.icon
                        )
                    )
                }
            }

            // Оптимистично в state; ошибка записи возвращает прежние порядок и закрепление
            is TodoListsIntent.SetPinned -> if (getState().lists != before.lists)
                launchSafe(onError = { sendIntent(TodoListsIntent.ListsChangeFailed(before.lists)) }) {
                    listRepository.setPinned(intent.listId, intent.pinned)
                }

            is TodoListsIntent.DropList -> {
                val after = getState().lists
                if (after != before.lists)
                    launchSafe(onError = { sendIntent(TodoListsIntent.ListsChangeFailed(before.lists)) }) {
                        listRepository.reorderLists(idPT, after.map { it.id })
                    }
            }

            TodoListsIntent.ConfirmDelete -> before.listToDelete?.let { list ->
                launchSafe(onError = { loaded?.let { sendIntent(it) } }) {
                    listRepository.delete(list.id)
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_list_deleted))
                }
            }

            else -> Unit
        }
    }
}
