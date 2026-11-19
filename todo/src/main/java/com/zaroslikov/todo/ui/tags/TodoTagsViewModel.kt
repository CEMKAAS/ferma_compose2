package com.zaroslikov.todo.ui.tags

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.TodoDuplicateTagException
import com.zaroslikov.todo.domain.repository.TodoTagRepository
import com.zaroslikov.todo.ui.base.TodoBaseViewModel
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.navigation.TodoTagsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoTagsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tagRepository: TodoTagRepository
) : TodoBaseViewModel<TodoTagsState, TodoTagsIntent>(TodoTagsState(), ::todoTagsReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[TodoTagsDestination.itemIdPT])

    init {
        viewModelScope.launch {
            tagRepository.getTags(idPT)
                .catch {
                    updateState { it.copy(isLoading = false) }
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_load_error))
                }
                .collect { sendIntent(TodoTagsIntent.DataLoaded(it)) }
        }
    }

    override fun onIntent(intent: TodoTagsIntent) {
        // Форму и тег для удаления берём до редьюсера — он их сбрасывает
        val form = getState().form
        val tagToDelete = getState().tagToDelete
        sendIntent(intent)
        when (intent) {
            TodoTagsIntent.SaveForm -> if (form != null && form.isValid) launchSafe {
                try {
                    val existingId = if (form.id == 0L) {
                        getState().tags.firstOrNull { it.title == form.title.trim() }?.id
                    } else null
                    val id = tagRepository.upsert(
                        DomainTodoTag(id = form.id, idPT = idPT, title = form.title.trim(), colorIndex = form.colorIndex)
                    )
                    // Новый тег с занятым названием: репозиторий вернул id уже существующего
                    if (form.id == 0L && id == existingId) emitEvent(TodoUiEvent.Message(R.string.todo_msg_tag_exists))
                } catch (e: TodoDuplicateTagException) {
                    // Переименование в занятое название; шторка уже закрыта, чтобы снекбар был виден
                    emitEvent(TodoUiEvent.Message(R.string.todo_msg_tag_exists))
                }
            }

            TodoTagsIntent.ConfirmDelete -> if (tagToDelete != null) launchSafe {
                tagRepository.delete(tagToDelete.id)
                emitEvent(TodoUiEvent.Message(R.string.todo_msg_tag_deleted))
            }

            else -> Unit
        }
    }
}
