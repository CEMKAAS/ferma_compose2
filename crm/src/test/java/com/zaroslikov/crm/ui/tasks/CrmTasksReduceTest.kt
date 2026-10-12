package com.zaroslikov.crm.ui.tasks

import com.zaroslikov.crm.domain.model.DomainCrmTask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CrmTasksReduceTest {

    private val today = "2026-10-12"

    private fun task(id: Long, due: String, isDone: Boolean = false, clientId: Long? = null) =
        DomainCrmTask(id = id, idPT = 1, clientId = clientId, title = "Задача $id", dueDate = due, isDone = isDone)

    private fun loaded(active: List<DomainCrmTask>, done: List<DomainCrmTask> = emptyList()) =
        crmTasksReduce(
            CrmTasksState(),
            CrmTasksIntent.DataLoaded(
                activeTasks = active,
                doneTasks = done,
                clients = emptyList(),
                clientNames = mapOf(7L to "Иван"),
                today = today
            )
        )

    @Test
    fun `активные группируются просрочено, сегодня, завтра, затем по датам`() {
        val state = loaded(
            listOf(
                task(1, "2026-10-20"),
                task(2, "2026-10-13"),
                task(3, "2026-10-01"),
                task(4, "2026-10-12"),
                task(5, "2026-10-11")
            )
        )

        assertFalse(state.isLoading)
        assertEquals(
            listOf(
                CrmTaskGroupKind.OVERDUE,
                CrmTaskGroupKind.TODAY,
                CrmTaskGroupKind.TOMORROW,
                CrmTaskGroupKind.DATE
            ),
            state.groups.map { it.kind }
        )
        assertEquals(listOf(3L, 5L), state.groups[0].items.map { it.task.id })
        assertEquals("2026-10-20", state.groups[3].date)
    }

    @Test
    fun `имя клиента подставляется по clientId`() {
        val state = loaded(listOf(task(1, today, clientId = 7), task(2, today, clientId = 99)))

        val items = state.groups.single().items
        assertEquals("Иван", items[0].clientName)
        assertNull(items[1].clientName)
    }

    @Test
    fun `выполненные группируются по датам, свежие сверху`() {
        val state = crmTasksReduce(
            loaded(emptyList(), done = listOf(task(1, "2026-09-01", true), task(2, "2026-10-01", true))),
            CrmTasksIntent.SelectTab(CrmTasksTab.DONE)
        )

        assertEquals(listOf("2026-10-01", "2026-09-01"), state.groups.map { it.date })
        assertTrue(state.groups.all { it.kind == CrmTaskGroupKind.DATE })
    }

    @Test
    fun `отметка переносит задачу в выполненные`() {
        val active = task(1, today)
        val state = crmTasksReduce(loaded(listOf(active)), CrmTasksIntent.ToggleDone(active))

        assertTrue(state.activeTasks.isEmpty())
        assertTrue(state.doneTasks.single().isDone)
        assertTrue(state.groups.isEmpty())
    }

    @Test
    fun `подтверждение удаления убирает задачу и закрывает диалог`() {
        val target = task(1, today)
        val requested = crmTasksReduce(loaded(listOf(target, task(2, today))), CrmTasksIntent.RequestDelete(target))
        assertEquals(target, requested.taskToDelete)

        val state = crmTasksReduce(requested, CrmTasksIntent.ConfirmDelete)
        assertNull(state.taskToDelete)
        assertEquals(listOf(2L), state.activeTasks.map { it.id })
    }

    @Test
    fun `создание задачи закрывает лист`() {
        val opened = crmTasksReduce(CrmTasksState(), CrmTasksIntent.OpenSheet)
        assertTrue(opened.showSheet)

        val state = crmTasksReduce(opened, CrmTasksIntent.CreateTask("Позвонить", today, null, ""))
        assertFalse(state.showSheet)
    }

    @Test
    fun `завтра на стыке годов`() {
        val groups = crmGroupTasks(
            tasks = listOf(task(1, "2026-12-31"), task(2, "2027-01-01"), task(3, "2027-01-02")),
            isDoneTab = false,
            today = "2026-12-31",
            clientNames = emptyMap()
        )

        assertEquals(
            listOf(CrmTaskGroupKind.TODAY, CrmTaskGroupKind.TOMORROW, CrmTaskGroupKind.DATE),
            groups.map { it.kind }
        )
        assertEquals(listOf(2L), groups[1].items.map { it.task.id })
        assertEquals("2027-01-02", groups[2].date)
    }
}
