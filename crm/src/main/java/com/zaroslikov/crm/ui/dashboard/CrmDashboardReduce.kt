package com.zaroslikov.crm.ui.dashboard

import com.zaroslikov.crm.domain.model.CrmClientActivity
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.ui.tasks.CrmTaskItem
import com.zaroslikov.crm.util.crmClientActivity
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import com.zaroslikov.domain.models.enums.Suffix
import java.time.LocalDate
import java.time.YearMonth

/** Сколько клиентов показывать в «Топ клиентов». */
const val CRM_TOP_CLIENTS = 5

fun crmDashboardReduce(state: CrmDashboardState, intent: CrmDashboardIntent): CrmDashboardState =
    when (intent) {
        is CrmDashboardIntent.DataLoaded -> state.copy(
            isLoading = false,
            data = intent.data,
            clients = intent.clients,
            today = intent.today
        )

        // Выполненная задача сразу пропадает из «Ближайших», не дожидаясь Flow из БД
        is CrmDashboardIntent.ToggleTask -> state.copy(
            data = state.data.copy(
                upcomingTasks = state.data.upcomingTasks.filterNot { it.task.id == intent.task.id }
            )
        )

        CrmDashboardIntent.OpenTaskSheet -> state.copy(showTaskSheet = true)
        CrmDashboardIntent.CloseTaskSheet -> state.copy(showTaskSheet = false)
        is CrmDashboardIntent.CreateTask -> state.copy(showTaskSheet = false)
    }

/** Последние [count] месяцев, заканчивая месяцем [today], от старого к новому. */
fun crmLastMonths(today: LocalDate, count: Int = 12): List<YearMonth> {
    val current = YearMonth.from(today)
    return (count - 1 downTo 0).map { current.minusMonths(it.toLong()) }
}

/**
 * Собирает показатели дашборда. Выручка, заказы и средний чек за месяц — по всем продажам проекта
 * (как на экране «Финансы»), клиентские метрики — по продажам с покупателем, сопоставленным
 * с неархивными [clients] по `salesKey`.
 */
fun crmDashboardData(
    stats: List<DomainCrmBuyerStats>,
    clients: List<DomainCrmClient>,
    monthRevenue: List<DomainCrmMonthRevenue>,
    months: List<YearMonth>,
    upcomingTasks: List<DomainCrmTask>,
    recentInteractions: List<DomainCrmInteraction>,
    salesWithoutBuyer: Int,
    currency: Suffix,
    today: LocalDate
): CrmDashboardData {
    val todayIso = today.toString()
    val revenueByMonth = monthRevenue.associateBy { YearMonth.of(it.year, it.month) }
    val bars = months.map { CrmMonthBar(it.year, it.monthValue, revenueByMonth[it]?.total ?: 0.0) }

    val current = YearMonth.from(today)
    val revenueMonth = revenueByMonth[current]?.total ?: 0.0
    val revenuePrev = revenueByMonth[current.minusMonths(1)]?.total ?: 0.0
    val dynamics = if (revenuePrev > 0.0) (revenueMonth - revenuePrev) / revenuePrev * 100.0 else null

    val statsByKey = stats.associateBy { it.buyer }
    val clientStats = clients.map { it to statsByKey[it.salesKey] }
    val activities = clientStats.map { (_, stat) -> crmClientActivity(stat?.firstDate, stat?.lastDate, todayIso) }
    val monthPrefix = current.toString()
    val newThisMonth = clientStats.count { (client, stat) ->
        (stat?.firstDate ?: client.createdDate).startsWith(monthPrefix)
    }

    // Средний чек — за текущий месяц по всем продажам, как соседние плитки выручки и заказов
    val ordersMonth = revenueByMonth[current]?.orders ?: 0
    val avgCheck = if (ordersMonth > 0) revenueMonth / ordersMonth else 0.0

    val top = clientStats
        .mapNotNull { (client, stat) ->
            stat?.takeIf { it.total > 0.0 }?.let { CrmTopClient(client.id, client.name, it.total) }
        }
        .sortedByDescending { it.total }
        .take(CRM_TOP_CLIENTS)

    val names = clients.associate { it.id to it.name }
    return CrmDashboardData(
        currency = currency,
        revenueMonth = revenueMonth,
        revenueDynamics = dynamics,
        clientsCount = clients.size,
        activeCount = activities.count { it == CrmClientActivity.NEW || it == CrmClientActivity.ACTIVE },
        newThisMonth = newThisMonth,
        avgCheck = avgCheck,
        ordersMonth = ordersMonth,
        months = bars,
        topClients = top,
        upcomingTasks = upcomingTasks.map { CrmTaskItem(it, it.clientId?.let(names::get)) },
        // Показываем только взаимодействия клиентов из списка (неархивных)
        recentInteractions = recentInteractions.mapNotNull { interaction ->
            names[interaction.clientId]?.let { CrmInteractionItem(interaction, it) }
        },
        salesWithoutBuyer = salesWithoutBuyer
    )
}
