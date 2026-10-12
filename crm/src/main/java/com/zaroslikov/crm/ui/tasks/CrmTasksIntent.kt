package com.zaroslikov.crm.ui.tasks

import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmTask

sealed interface CrmTasksIntent {
    data class DataLoaded(
        val activeTasks: List<DomainCrmTask>,
        val doneTasks: List<DomainCrmTask>,
        val clients: List<DomainCrmClient>,
        val clientNames: Map<Long, String>,
        val today: String
    ) : CrmTasksIntent

    data class SelectTab(val tab: CrmTasksTab) : CrmTasksIntent
    data class ToggleDone(val task: DomainCrmTask) : CrmTasksIntent

    data object OpenSheet : CrmTasksIntent
    data object CloseSheet : CrmTasksIntent
    data class CreateTask(
        val title: String,
        val dueDate: String,
        val clientId: Long?,
        val note: String
    ) : CrmTasksIntent

    data class RequestDelete(val task: DomainCrmTask) : CrmTasksIntent
    data object DismissDelete : CrmTasksIntent
    data object ConfirmDelete : CrmTasksIntent
}
