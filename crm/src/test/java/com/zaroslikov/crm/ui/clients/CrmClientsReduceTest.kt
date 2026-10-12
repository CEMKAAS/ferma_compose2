package com.zaroslikov.crm.ui.clients

import com.zaroslikov.crm.domain.model.CrmClientActivity
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import com.zaroslikov.domain.models.enums.Suffix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CrmClientsReduceTest {

    private val today = "2026-10-12"

    private fun client(
        id: Long,
        name: String,
        isVip: Boolean = false,
        isFavorite: Boolean = false,
        source: CrmClientSource = CrmClientSource.SALES
    ) =
        DomainCrmClient(
            id = id,
            idPT = 1,
            name = name,
            salesKey = name,
            source = source,
            isVip = isVip,
            isFavorite = isFavorite,
            createdDate = "2025-01-01"
        )

    private val anna = client(1, "Анна", isVip = true)
    private val boris = client(2, "Борис", isFavorite = true)
    private val vera = client(3, "Вера")

    private val stats = listOf(
        // Новая: первая покупка 2 дня назад
        DomainCrmBuyerStats("Анна", total = 500.0, orders = 1, firstDate = "2026-10-10", lastDate = "2026-10-10"),
        // Неактивный: последняя покупка полгода назад
        DomainCrmBuyerStats("Борис", total = 3000.0, orders = 5, firstDate = "2025-01-10", lastDate = "2026-04-01"),
        DomainCrmBuyerStats("Чужой", total = 1.0, orders = 1, firstDate = "2026-01-01", lastDate = "2026-01-01")
    )

    private fun loaded(): CrmClientsState {
        val rows = crmClientRows(listOf(anna, boris, vera), stats, today)
        return crmClientsReduce(
            CrmClientsState(),
            CrmClientsIntent.DataLoaded(
                rows = rows,
                archivedRows = crmClientRows(listOf(client(4, "Архивный", source = CrmClientSource.MANUAL)), stats, today),
                contacts = mapOf(3L to listOf("+7 (900) 123-45-67", "vera@mail.ru")),
                currency = Suffix.TENGE
            )
        )
    }

    @Test
    fun `статистика матчится по salesKey и считает активность`() {
        val rows = crmClientRows(listOf(anna, boris, vera), stats, today).associateBy { it.client.id }

        assertEquals(CrmClientActivity.NEW, rows.getValue(1).activity)
        assertEquals(CrmClientActivity.INACTIVE, rows.getValue(2).activity)
        assertEquals(CrmClientActivity.NO_SALES, rows.getValue(3).activity)
        assertEquals(5, rows.getValue(2).orders)
        assertEquals(0.0, rows.getValue(3).total, 0.0)
    }

    @Test
    fun `по умолчанию сортировка по выручке`() {
        val state = loaded()

        assertFalse(state.isLoading)
        assertEquals(Suffix.TENGE, state.currency)
        assertEquals(listOf("Борис", "Анна", "Вера"), state.visible.map { it.client.name })
    }

    @Test
    fun `сортировка по имени и по последней покупке`() {
        val byName = crmClientsReduce(loaded(), CrmClientsIntent.SelectSort(CrmClientsSort.NAME))
        assertEquals(listOf("Анна", "Борис", "Вера"), byName.visible.map { it.client.name })

        val byLast = crmClientsReduce(loaded(), CrmClientsIntent.SelectSort(CrmClientsSort.LAST_PURCHASE))
        assertEquals(listOf("Анна", "Борис", "Вера"), byLast.visible.map { it.client.name })
    }

    @Test
    fun `фильтры избранные, VIP, новые, неактивные`() {
        fun names(filter: CrmClientsFilter) =
            crmClientsReduce(loaded(), CrmClientsIntent.SelectFilter(filter)).visible.map { it.client.name }

        assertEquals(listOf("Борис"), names(CrmClientsFilter.FAVORITE))
        assertEquals(listOf("Анна"), names(CrmClientsFilter.VIP))
        assertEquals(listOf("Анна"), names(CrmClientsFilter.NEW))
        assertEquals(listOf("Борис"), names(CrmClientsFilter.INACTIVE))
        assertEquals(listOf("Архивный"), names(CrmClientsFilter.ARCHIVE))
    }

    @Test
    fun `поиск по имени, контакту и цифрам телефона`() {
        fun search(query: String) =
            crmClientsReduce(loaded(), CrmClientsIntent.Search(query)).visible.map { it.client.name }

        assertEquals(listOf("Борис"), search("бор"))
        assertEquals(listOf("Вера"), search("MAIL.RU"))
        assertEquals(listOf("Вера"), search("7900123"))
        assertTrue(search("никого").isEmpty())
        // Запрос не похож на номер — цифра «9» не ищется в телефоне Веры
        assertTrue(search("Марта 9").isEmpty())
    }

    @Test
    fun `избранное переключается оптимистично`() {
        val state = crmClientsReduce(loaded(), CrmClientsIntent.ToggleFavorite(vera))

        assertTrue(state.rows.first { it.client.id == 3L }.client.isFavorite)
    }

    @Test
    fun `восстановление переносит клиента из архива`() {
        val archived = loaded().archivedRows.single().client
        val state = crmClientsReduce(loaded(), CrmClientsIntent.Restore(archived))

        assertTrue(state.archivedRows.isEmpty())
        assertFalse(state.rows.first { it.client.id == 4L }.client.isArchived)
    }

    @Test
    fun `удаление навсегда убирает клиента из архива после подтверждения`() {
        val archive = crmClientsReduce(loaded(), CrmClientsIntent.SelectFilter(CrmClientsFilter.ARCHIVE))
        val target = archive.archivedRows.single().client
        val requested = crmClientsReduce(archive, CrmClientsIntent.RequestDeleteForever(target))
        assertEquals(target, requested.clientToDelete)

        val dismissed = crmClientsReduce(requested, CrmClientsIntent.DismissDelete)
        assertNull(dismissed.clientToDelete)
        assertEquals(1, dismissed.archivedRows.size)

        val state = crmClientsReduce(requested, CrmClientsIntent.ConfirmDelete)
        assertNull(state.clientToDelete)
        assertTrue(state.visible.isEmpty())
    }

    @Test
    fun `клиент из продаж навсегда не удаляется`() {
        val fromSales = client(5, "Из продаж").copy(isArchived = true)
        val base = loaded().let {
            it.copy(archivedRows = it.archivedRows + crmClientRows(listOf(fromSales), stats, today))
        }

        val requested = crmClientsReduce(base, CrmClientsIntent.RequestDeleteForever(fromSales))
        assertNull(requested.clientToDelete)

        // Даже если подтверждение пришло, клиент из продаж остаётся в архиве
        val confirmed = crmClientsReduce(base.copy(clientToDelete = fromSales), CrmClientsIntent.ConfirmDelete)
        assertNull(confirmed.clientToDelete)
        assertTrue(confirmed.archivedRows.any { it.client.id == 5L })
    }
}
