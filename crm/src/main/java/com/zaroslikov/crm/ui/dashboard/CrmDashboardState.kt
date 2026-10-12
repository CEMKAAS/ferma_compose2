package com.zaroslikov.crm.ui.dashboard

import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.ui.tasks.CrmTaskItem
import com.zaroslikov.crm.util.today
import com.zaroslikov.domain.models.enums.Suffix

/** Выручка за месяц для графика; пропущенные месяцы заполнены нулём. */
data class CrmMonthBar(
    val year: Int,
    val month: Int,
    val total: Double
)

data class CrmTopClient(
    val clientId: Long,
    val name: String,
    val total: Double
)

data class CrmInteractionItem(
    val interaction: DomainCrmInteraction,
    val clientName: String
)

/** Всё, что показывает дашборд; собирается чистой функцией `crmDashboardData`. */
data class CrmDashboardData(
    val currency: Suffix = Suffix.RUBLE,
    val revenueMonth: Double = 0.0,
    /** Изменение выручки к прошлому месяцу в процентах; `null`, если в прошлом месяце продаж не было. */
    val revenueDynamics: Double? = null,
    val clientsCount: Int = 0,
    val activeCount: Int = 0,
    val newThisMonth: Int = 0,
    val avgCheck: Double = 0.0,
    val ordersMonth: Int = 0,
    val months: List<CrmMonthBar> = emptyList(),
    val topClients: List<CrmTopClient> = emptyList(),
    val upcomingTasks: List<CrmTaskItem> = emptyList(),
    val recentInteractions: List<CrmInteractionItem> = emptyList(),
    val salesWithoutBuyer: Int = 0
)

data class CrmDashboardState(
    val isLoading: Boolean = true,
    val today: String = today(),
    val data: CrmDashboardData = CrmDashboardData(),
    /** Неархивные клиенты — для листа создания задачи. */
    val clients: List<DomainCrmClient> = emptyList(),
    val showTaskSheet: Boolean = false
)
