package com.zaroslikov.crm.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.zaroslikov.crm.R
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.crm.domain.repository.CrmTaskRepository
import com.zaroslikov.crm.ui.base.CrmBaseViewModel
import com.zaroslikov.crm.ui.base.CrmUiEvent
import com.zaroslikov.crm.ui.navigation.CrmTasksDestination
import com.zaroslikov.crm.util.today
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CrmTasksViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: CrmTaskRepository,
    clientRepository: CrmClientRepository
) : CrmBaseViewModel<CrmTasksState, CrmTasksIntent>(CrmTasksState(), ::crmTasksReduce) {

    private val idPT: Long = checkNotNull(savedStateHandle[CrmTasksDestination.itemIdPT])

    init {
        viewModelScope.launch {
            combine(
                taskRepository.getTasks(idPT, isDone = false),
                taskRepository.getTasks(idPT, isDone = true),
                clientRepository.getClients(idPT),
                clientRepository.getArchivedClients(idPT)
            ) { active, done, clients, archived ->
                CrmTasksIntent.DataLoaded(
                    activeTasks = active,
                    doneTasks = done,
                    clients = clients,
                    clientNames = (clients + archived).associate { it.id to it.name },
                    today = today()
                )
            }
                .catch {
                    // Без индикатора навсегда: показываем то, что есть, и сообщение об ошибке
                    updateState { it.copy(isLoading = false) }
                    emitEvent(CrmUiEvent.Message(R.string.crm_msg_load_error))
                }
                .collect { sendIntent(it) }
        }
    }

    override fun onIntent(intent: CrmTasksIntent) {
        // Задачу для удаления берём до редьюсера — он её сбрасывает
        val taskToDelete = getState().taskToDelete
        sendIntent(intent)
        when (intent) {
            is CrmTasksIntent.ToggleDone -> launchSafe {
                taskRepository.setDone(intent.task.id, !intent.task.isDone)
            }

            is CrmTasksIntent.CreateTask -> launchSafe {
                taskRepository.upsertTask(
                    DomainCrmTask(
                        idPT = idPT,
                        clientId = intent.clientId,
                        title = intent.title.trim(),
                        dueDate = intent.dueDate,
                        note = intent.note.trim()
                    )
                )
                emitEvent(CrmUiEvent.Message(R.string.crm_msg_task_saved))
            }

            CrmTasksIntent.ConfirmDelete -> if (taskToDelete != null) launchSafe {
                taskRepository.deleteTask(taskToDelete)
                emitEvent(CrmUiEvent.Message(R.string.crm_msg_task_deleted))
            }

            else -> Unit
        }
    }
}
