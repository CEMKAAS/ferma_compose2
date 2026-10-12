package com.zaroslikov.crm.ui.dashboard

import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import com.zaroslikov.domain.models.enums.Suffix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class CrmDashboardReduceTest {

    private val today = LocalDate.of(2026, 10, 12)
    private val months = crmLastMonths(today)

    private fun client(id: Long, name: String, createdDate: String = "2025-01-01") = DomainCrmClient(
        id = id,
        idPT = 1,
        name = name,
        salesKey = name,
        source = CrmClientSource.SALES,
        createdDate = createdDate
    )

    private val clients = listOf(
        client(1, "Анна"),
        client(2, "Борис"),
        client(3, "Вера", createdDate = "2026-10-05")
    )

    private val stats = listOf(
        DomainCrmBuyerStats("Анна", total = 1000.0, orders = 2, firstDate = "2026-10-01", lastDate = "2026-10-10"),
        DomainCrmBuyerStats("Борис", total = 3000.0, orders = 4, firstDate = "2025-02-01", lastDate = "2026-03-01"),
        // Покупатель, клиент которого в архиве, — в метрики не попадает
        DomainCrmBuyerStats("Архивный", total = 9999.0, orders = 1, firstDate = "2026-01-01", lastDate = "2026-01-01")
    )

    private fun data(
        monthRevenue: List<DomainCrmMonthRevenue> = listOf(
            DomainCrmMonthRevenue(2026, 9, total = 800.0, orders = 4),
            DomainCrmMonthRevenue(2026, 10, total = 1000.0, orders = 5)
        ),
        tasks: List<DomainCrmTask> = emptyList(),
        interactions: List<DomainCrmInteraction> = emptyList()
    ) = crmDashboardData(
        stats = stats,
        clients = clients,
        monthRevenue = monthRevenue,
        months = months,
        upcomingTasks = tasks,
        recentInteractions = interactions,
        salesWithoutBuyer = 3,
        currency = Suffix.RUBLE,
        today = today
    )

    @Test
    fun `двенадцать месяцев заканчиваются текущим`() {
        assertEquals(12, months.size)
        assertEquals(YearMonth.of(2025, 11), months.first())
        assertEquals(YearMonth.of(2026, 10), months.last())
    }

    @Test
    fun `пропущенные месяцы заполняются нулём`() {
        val bars = data().months

        assertEquals(12, bars.size)
        assertEquals(1000.0, bars.last().total, 0.0)
        assertEquals(800.0, bars[10].total, 0.0)
        assertTrue(bars.take(10).all { it.total == 0.0 })
    }

    @Test
    fun `выручка за месяц и динамика к прошлому`() {
        val result = data()

        assertEquals(1000.0, result.revenueMonth, 0.0)
        assertEquals(25.0, result.revenueDynamics!!, 0.0001)
        assertEquals(5, result.ordersMonth)
        assertEquals(3, result.salesWithoutBuyer)
    }

    @Test
    fun `без продаж в прошлом месяце динамики нет`() {
        val result = data(monthRevenue = listOf(DomainCrmMonthRevenue(2026, 10, 500.0, 1)))

        assertNull(result.revenueDynamics)
    }

    @Test
    fun `клиентские метрики считаются по неархивным клиентам`() {
        val result = data()

        assertEquals(3, result.clientsCount)
        // Анна — новая (активна), Борис — неактивен, Вера — без продаж
        assertEquals(1, result.activeCount)
        // Анна — первая покупка в октябре, Вера — создана в октябре вручную
        assertEquals(2, result.newThisMonth)
    }

    @Test
    fun `средний чек — выручка текущего месяца на число заказов`() {
        // 1000 за 5 заказов в октябре; продажи других месяцев и покупатели не влияют
        assertEquals(200.0, data().avgCheck, 0.0001)
        assertEquals(0.0, data(monthRevenue = listOf(DomainCrmMonthRevenue(2026, 9, 800.0, 4))).avgCheck, 0.0)
    }

    @Test
    fun `топ клиентов по выручке без клиентов без продаж`() {
        val top = data().topClients

        assertEquals(listOf(2L, 1L), top.map { it.clientId })
        assertEquals(3000.0, top.first().total, 0.0)
    }

    @Test
    fun `задачи и взаимодействия получают имена клиентов`() {
        val result = data(
            tasks = listOf(DomainCrmTask(id = 1, idPT = 1, clientId = 2, title = "Позвонить", dueDate = "2026-10-12")),
            interactions = listOf(
                DomainCrmInteraction(id = 1, clientId = 1, type = CrmInteractionType.CALL, date = "2026-10-10", note = ""),
                DomainCrmInteraction(id = 2, clientId = 42, type = CrmInteractionType.NOTE, date = "2026-10-09", note = "")
            )
        )

        assertEquals("Борис", result.upcomingTasks.single().clientName)
        assertEquals(listOf("Анна"), result.recentInteractions.map { it.clientName })
    }

    @Test
    fun `загрузка, отметка задачи и лист создания`() {
        val task = DomainCrmTask(id = 5, idPT = 1, title = "Отправить заказ", dueDate = "2026-10-12")
        val loaded = crmDashboardReduce(
            CrmDashboardState(),
            CrmDashboardIntent.DataLoaded(data(tasks = listOf(task)), clients, today.toString())
        )
        assertFalse(loaded.isLoading)
        assertEquals(1, loaded.data.upcomingTasks.size)

        val toggled = crmDashboardReduce(loaded, CrmDashboardIntent.ToggleTask(task))
        assertTrue(toggled.data.upcomingTasks.isEmpty())

        val opened = crmDashboardReduce(toggled, CrmDashboardIntent.OpenTaskSheet)
        assertTrue(opened.showTaskSheet)
        val created = crmDashboardReduce(opened, CrmDashboardIntent.CreateTask("Задача", "2026-10-13", 1, ""))
        assertFalse(created.showTaskSheet)
    }
}
