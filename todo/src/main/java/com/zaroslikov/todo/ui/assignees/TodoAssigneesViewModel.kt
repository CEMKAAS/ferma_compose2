package com.zaroslikov.todo.ui.assignees

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import com.zaroslikov.todo.ui.base.TodoBaseViewModel
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.navigation.TodoAssigneesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoAssigneesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TodoTaskRepository
) : TodoBaseViewModel<TodoAssigneesState, TodoAssigneesIntent>(TodoAssigneesState(), ::todoAssigneesReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[TodoAssigneesDestination.itemIdPT])

    init {
        viewModelScope.launch {
            taskRepository.getProjectAssigneeProfiles(idPT)
                .catch {
                    updateState { it.copy(isLoading = false) }
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
                }
                .collect { sendIntent(TodoAssigneesIntent.Loaded(it)) }
        }
    }

    override fun onIntent(intent: TodoAssigneesIntent) {
        val before = getState()
        if (before.isSaving) return
        sendIntent(intent)
        when (intent) {
            is TodoAssigneesIntent.Save -> if (getState().isSaving) save(checkNotNull(before.form), intent.fallbackName)
            TodoAssigneesIntent.Delete -> before.nameToDelete?.let { delete(it) }
            else -> Unit
        }
    }

    private fun save(form: TodoAssigneeForm, fallbackName: String) = launchSafe(
        onError = { sendIntent(TodoAssigneesIntent.SaveFailed(R.string.todo_msg_error)) }
    ) {
        val saved = taskRepository.saveProjectAssigneeProfile(idPT, form.originalName, form.profile, fallbackName)
        if (saved) {
            sendIntent(TodoAssigneesIntent.Saved)
        } else {
            sendIntent(TodoAssigneesIntent.SaveFailed(R.string.todo_msg_error))
        }
    }

    private fun delete(name: String) = launchSafe(
        onError = { sendIntent(TodoAssigneesIntent.SaveFailed(R.string.todo_msg_error)) }
    ) {
        taskRepository.deleteProjectAssignee(idPT, name)
        sendIntent(TodoAssigneesIntent.Saved)
    }
}
