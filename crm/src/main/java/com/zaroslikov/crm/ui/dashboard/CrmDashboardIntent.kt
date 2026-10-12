package com.zaroslikov.crm.ui.dashboard

import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmTask

sealed interface CrmDashboardIntent {
    data class DataLoaded(
        val data: CrmDashboardData,
        val clients: List<DomainCrmClient>,
        val today: String
    ) : CrmDashboardIntent

    data class ToggleTask(val task: DomainCrmTask) : CrmDashboardIntent

    data object OpenTaskSheet : CrmDashboardIntent
    data object CloseTaskSheet : CrmDashboardIntent
    data class CreateTask(
        val title: String,
        val dueDate: String,
        val clientId: Long?,
        val note: String
    ) : CrmDashboardIntent
}
