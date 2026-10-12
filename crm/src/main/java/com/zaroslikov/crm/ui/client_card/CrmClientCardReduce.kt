package com.zaroslikov.crm.ui.client_card

import com.zaroslikov.domain.models.dto.crm.DomainCrmMonthRevenue
import java.time.YearMonth

/** Количество месяцев на графике выручки клиента. */
const val CRM_CARD_MONTHS = 12

/**
 * Ряд за [count] месяцев, заканчивающийся [lastMonth]; месяцы без продаж — с нулём.
 */
internal fun crmMonthSeries(
    rows: List<DomainCrmMonthRevenue>,
    lastMonth: YearMonth,
    count: Int = CRM_CARD_MONTHS
): List<CrmMonthPoint> {
    val byMonth = rows.groupBy { YearMonth.of(it.year, it.month) }
    return (count - 1 downTo 0).map { back ->
        val month = lastMonth.minusMonths(back.toLong())
        CrmMonthPoint(
            year = month.year,
            month = month.monthValue,
            total = byMonth[month]?.sumOf { it.total } ?: 0.0
        )
    }
}

object CrmClientCardReducer {

    fun reducer(state: CrmClientCardState, intent: CrmClientCardIntent): CrmClientCardState =
        when (intent) {
            is CrmClientCardIntent.Loaded -> state.copy(isLoading = false, data = intent.data)

            // Оптимистично переключаем звезду, БД подтвердит через Flow
            CrmClientCardIntent.ToggleFavorite -> state.copy(
                data = state.data?.let { it.copy(client = it.client.copy(isFavorite = !it.client.isFavorite)) }
            )

            CrmClientCardIntent.ToggleShowAllSales -> state.copy(showAllSales = !state.showAllSales)

            CrmClientCardIntent.ShowArchiveDialog -> state.copy(showArchiveDialog = true)
            CrmClientCardIntent.DismissArchiveDialog,
            CrmClientCardIntent.ConfirmArchive -> state.copy(showArchiveDialog = false)

            is CrmClientCardIntent.OpenInteractionSheet ->
                state.copy(interactionSheet = CrmInteractionDraft(date = intent.today))
            CrmClientCardIntent.CloseInteractionSheet -> state.copy(interactionSheet = null)
            is CrmClientCardIntent.InteractionTypeChange ->
                state.copy(interactionSheet = state.interactionSheet?.copy(type = intent.type))
            is CrmClientCardIntent.InteractionDateChange ->
                state.copy(interactionSheet = state.interactionSheet?.copy(date = intent.date, dateError = false))
            is CrmClientCardIntent.InteractionNoteChange ->
                state.copy(interactionSheet = state.interactionSheet?.copy(note = intent.note, noteError = false))
            CrmClientCardIntent.SaveInteraction -> state.copy(
                interactionSheet = state.interactionSheet?.let {
                    it.copy(noteError = it.note.isBlank(), dateError = it.date.isBlank())
                }
            )

            is CrmClientCardIntent.AskDeleteInteraction -> state.copy(interactionToDelete = intent.interaction)
            CrmClientCardIntent.DismissDeleteInteraction,
            CrmClientCardIntent.ConfirmDeleteInteraction -> state.copy(interactionToDelete = null)

            is CrmClientCardIntent.OpenTaskSheet -> state.copy(taskSheet = CrmTaskDraft(dueDate = intent.today))
            CrmClientCardIntent.CloseTaskSheet -> state.copy(taskSheet = null)
            is CrmClientCardIntent.TaskTitleChange ->
                state.copy(taskSheet = state.taskSheet?.copy(title = intent.title, titleError = false))
            is CrmClientCardIntent.TaskDateChange ->
                state.copy(taskSheet = state.taskSheet?.copy(dueDate = intent.date, dateError = false))
            is CrmClientCardIntent.TaskNoteChange ->
                state.copy(taskSheet = state.taskSheet?.copy(note = intent.note))
            CrmClientCardIntent.SaveTask -> state.copy(
                taskSheet = state.taskSheet?.let {
                    it.copy(titleError = it.title.isBlank(), dateError = it.dueDate.isBlank())
                }
            )

            // Отметка задачи — только побочный эффект, список обновит Flow
            is CrmClientCardIntent.ToggleTaskDone -> state
        }
}
