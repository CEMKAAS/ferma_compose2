package com.zaroslikov.crm.ui.clients

import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.domain.models.enums.Suffix

sealed interface CrmClientsIntent {
    data class DataLoaded(
        val rows: List<CrmClientRow>,
        val archivedRows: List<CrmClientRow>,
        val contacts: Map<Long, List<String>>,
        val currency: Suffix
    ) : CrmClientsIntent

    data class Search(val query: String) : CrmClientsIntent
    data class SelectFilter(val filter: CrmClientsFilter) : CrmClientsIntent
    data class SelectSort(val sort: CrmClientsSort) : CrmClientsIntent

    data class ToggleFavorite(val client: DomainCrmClient) : CrmClientsIntent
    data class Restore(val client: DomainCrmClient) : CrmClientsIntent

    data class RequestDeleteForever(val client: DomainCrmClient) : CrmClientsIntent
    data object DismissDelete : CrmClientsIntent
    data object ConfirmDelete : CrmClientsIntent
}
