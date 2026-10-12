package com.zaroslikov.crm.ui.client_card

import com.zaroslikov.crm.domain.model.CrmInteractionType
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.model.DomainCrmTask

sealed interface CrmClientCardIntent {
    data class Loaded(val data: CrmClientCardData) : CrmClientCardIntent

    data object ToggleFavorite : CrmClientCardIntent
    data object ToggleShowAllSales : CrmClientCardIntent

    data object ShowArchiveDialog : CrmClientCardIntent
    data object DismissArchiveDialog : CrmClientCardIntent
    data object ConfirmArchive : CrmClientCardIntent

    // Взаимодействия
    /** [today] — дата по умолчанию; редьюсер не читает часы сам. */
    data class OpenInteractionSheet(val today: String) : CrmClientCardIntent
    data object CloseInteractionSheet : CrmClientCardIntent
    data class InteractionTypeChange(val type: CrmInteractionType) : CrmClientCardIntent
    data class InteractionDateChange(val date: String) : CrmClientCardIntent
    data class InteractionNoteChange(val note: String) : CrmClientCardIntent
    data object SaveInteraction : CrmClientCardIntent
    data class AskDeleteInteraction(val interaction: DomainCrmInteraction) : CrmClientCardIntent
    data object DismissDeleteInteraction : CrmClientCardIntent
    data object ConfirmDeleteInteraction : CrmClientCardIntent

    // Задачи
    data class OpenTaskSheet(val today: String) : CrmClientCardIntent
    data object CloseTaskSheet : CrmClientCardIntent
    data class TaskTitleChange(val title: String) : CrmClientCardIntent
    data class TaskDateChange(val date: String) : CrmClientCardIntent
    data class TaskNoteChange(val note: String) : CrmClientCardIntent
    data object SaveTask : CrmClientCardIntent
    data class ToggleTaskDone(val task: DomainCrmTask) : CrmClientCardIntent
}
