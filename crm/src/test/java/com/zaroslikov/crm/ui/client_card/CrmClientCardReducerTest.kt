package com.zaroslikov.crm.ui.client_card

import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import com.zaroslikov.domain.models.enums.Suffix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.YearMonth

class CrmClientCardReducerTest {

    private companion object {
        const val TODAY = "2026-10-12"
    }

    private fun reduce(state: CrmClientCardState, vararg intents: CrmClientCardIntent) =
        intents.fold(state) { acc, intent -> CrmClientCardReducer.reducer(acc, intent) }

    private val data = CrmClientCardData(
        client = DomainCrmClient(id = 1, idPT = 1, name = "Иван", salesKey = "Иван", createdDate = "2026-01-01"),
        contacts = emptyList(),
        interactions = emptyList(),
        tasks = emptyList(),
        stats = null,
        sales = emptyList(),
        products = emptyList(),
        monthly = emptyList(),
        currency = Suffix.RUBLE
    )

    private val loaded = reduce(CrmClientCardState(), CrmClientCardIntent.Loaded(data))

    @Test
    fun `загрузка снимает индикатор и кладёт данные`() {
        assertFalse(loaded.isLoading)
        assertEquals(data, loaded.data)
    }

    @Test
    fun `переключение избранного меняет флаг клиента`() {
        val state = reduce(loaded, CrmClientCardIntent.ToggleFavorite)
        assertTrue(state.data!!.client.isFavorite)
        assertFalse(reduce(state, CrmClientCardIntent.ToggleFavorite).data!!.client.isFavorite)
    }

    @Test
    fun `взаимодействие без текста не проходит проверку`() {
        val state = reduce(loaded, CrmClientCardIntent.OpenInteractionSheet(TODAY), CrmClientCardIntent.SaveInteraction)
        val draft = state.interactionSheet!!
        assertTrue(draft.noteError)
        assertFalse(draft.isValid)
    }

    @Test
    fun `заполненное взаимодействие валидно, ввод сбрасывает ошибку`() {
        val state = reduce(
            loaded,
            CrmClientCardIntent.OpenInteractionSheet(TODAY),
            CrmClientCardIntent.SaveInteraction,
            CrmClientCardIntent.InteractionNoteChange("Договорились о доставке"),
            CrmClientCardIntent.InteractionTypeChange(CrmInteractionType.MEETING),
            CrmClientCardIntent.SaveInteraction
        )
        val draft = state.interactionSheet!!
        assertTrue(draft.isValid)
        assertEquals(CrmInteractionType.MEETING, draft.type)
    }

    @Test
    fun `задача без названия или даты не проходит проверку`() {
        val state = reduce(
            loaded,
            CrmClientCardIntent.OpenTaskSheet(TODAY),
            CrmClientCardIntent.TaskDateChange(""),
            CrmClientCardIntent.SaveTask
        )
        val draft = state.taskSheet!!
        assertTrue(draft.titleError)
        assertTrue(draft.dateError)
        assertFalse(draft.isValid)
    }

    @Test
    fun `новая задача по умолчанию на переданную дату и валидна с названием`() {
        val state = reduce(
            loaded,
            CrmClientCardIntent.OpenTaskSheet(TODAY),
            CrmClientCardIntent.TaskTitleChange("Позвонить"),
            CrmClientCardIntent.SaveTask
        )
        val draft = state.taskSheet!!
        assertTrue(draft.isValid)
        assertEquals(TODAY, draft.dueDate)
    }

    @Test
    fun `закрытие листа сбрасывает черновик`() {
        val state = reduce(loaded, CrmClientCardIntent.OpenTaskSheet(TODAY), CrmClientCardIntent.CloseTaskSheet)
        assertNull(state.taskSheet)
    }

    @Test
    fun `подтверждение удаления взаимодействия закрывает диалог`() {
        val interaction = DomainCrmInteraction(id = 3, clientId = 1, type = CrmInteractionType.CALL, date = "2026-02-01", note = "x")
        val asked = reduce(loaded, CrmClientCardIntent.AskDeleteInteraction(interaction))
        assertNotNull(asked.interactionToDelete)
        assertNull(reduce(asked, CrmClientCardIntent.ConfirmDeleteInteraction).interactionToDelete)
    }

    @Test
    fun `диалог архива открывается и закрывается`() {
        val shown = reduce(loaded, CrmClientCardIntent.ShowArchiveDialog)
        assertTrue(shown.showArchiveDialog)
        assertFalse(reduce(shown, CrmClientCardIntent.ConfirmArchive).showArchiveDialog)
    }

    @Test
    fun `ряд по месяцам дополняется нулями и переходит через год`() {
        val rows = listOf(
            DomainCrmMonthRevenue(year = 2025, month = 12, total = 100.0, orders = 1),
            DomainCrmMonthRevenue(year = 2026, month = 2, total = 50.0, orders = 2)
        )
        val series = crmMonthSeries(rows, YearMonth.of(2026, 2), count = 4)
        assertEquals(listOf(11, 12, 1, 2), series.map { it.month })
        assertEquals(listOf(2025, 2025, 2026, 2026), series.map { it.year })
        assertEquals(listOf(0.0, 100.0, 0.0, 50.0), series.map { it.total })
    }

    @Test
    fun `лист взаимодействия открывается с переданной датой`() {
        val state = reduce(loaded, CrmClientCardIntent.OpenInteractionSheet(TODAY))
        assertEquals(TODAY, state.interactionSheet!!.date)
    }

    @Test
    fun `ряд по месяцам суммирует строки одного месяца и игнорирует выходящие за окно`() {
        val rows = listOf(
            DomainCrmMonthRevenue(year = 2026, month = 10, total = 100.0, orders = 1),
            DomainCrmMonthRevenue(year = 2026, month = 10, total = 25.5, orders = 1),
            // Раньше окна в 3 месяца — в ряд не попадает
            DomainCrmMonthRevenue(year = 2026, month = 6, total = 999.0, orders = 1)
        )
        val series = crmMonthSeries(rows, YearMonth.of(2026, 10), count = 3)
        assertEquals(listOf(8, 9, 10), series.map { it.month })
        assertEquals(listOf(0.0, 0.0, 125.5), series.map { it.total })
    }
}
