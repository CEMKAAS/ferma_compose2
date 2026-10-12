package com.zaroslikov.crm.ui.clients

import com.zaroslikov.crm.domain.model.CrmClientActivity
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.util.crmClientActivity
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats

fun crmClientsReduce(state: CrmClientsState, intent: CrmClientsIntent): CrmClientsState = when (intent) {
    is CrmClientsIntent.DataLoaded -> state.copy(
        isLoading = false,
        rows = intent.rows,
        archivedRows = intent.archivedRows,
        contacts = intent.contacts,
        currency = intent.currency
    ).withVisible()

    is CrmClientsIntent.Search -> state.copy(query = intent.query).withVisible()
    is CrmClientsIntent.SelectFilter -> state.copy(filter = intent.filter).withVisible()
    is CrmClientsIntent.SelectSort -> state.copy(sort = intent.sort).withVisible()

    // Оптимистично меняем звёздочку, не дожидаясь Flow из БД
    is CrmClientsIntent.ToggleFavorite -> state.copy(
        rows = state.rows.map {
            if (it.client.id == intent.client.id)
                it.copy(client = it.client.copy(isFavorite = !it.client.isFavorite))
            else it
        }
    ).withVisible()

    is CrmClientsIntent.Restore -> {
        val restored = state.archivedRows.filter { it.client.id == intent.client.id }
            .map { it.copy(client = it.client.copy(isArchived = false)) }
        state.copy(
            rows = state.rows + restored,
            archivedRows = state.archivedRows.filterNot { it.client.id == intent.client.id }
        ).withVisible()
    }

    // Как и во ViewModel: клиент из продаж вернулся бы при синхронизации — навсегда удаляются только ручные
    is CrmClientsIntent.RequestDeleteForever ->
        if (intent.client.source == CrmClientSource.MANUAL) state.copy(clientToDelete = intent.client)
        else state
    CrmClientsIntent.DismissDelete -> state.copy(clientToDelete = null)
    CrmClientsIntent.ConfirmDelete -> {
        val id = state.clientToDelete?.takeIf { it.source == CrmClientSource.MANUAL }?.id
        state.copy(
            clientToDelete = null,
            archivedRows = state.archivedRows.filterNot { it.client.id == id }
        ).withVisible()
    }
}

private fun CrmClientsState.withVisible(): CrmClientsState = copy(
    visible = crmFilterClients(
        rows = if (filter == CrmClientsFilter.ARCHIVE) archivedRows else rows,
        contacts = contacts,
        query = query,
        filter = filter,
        sort = sort
    )
)

/** Клиенты со статистикой: продажи матчатся по `salesKey`, активность вычисляется на [today]. */
fun crmClientRows(
    clients: List<DomainCrmClient>,
    stats: List<DomainCrmBuyerStats>,
    today: String
): List<CrmClientRow> {
    val byKey = stats.associateBy { it.buyer }
    return clients.map { client ->
        val stat = byKey[client.salesKey]
        CrmClientRow(
            client = client,
            total = stat?.total ?: 0.0,
            orders = stat?.orders ?: 0,
            lastDate = stat?.lastDate,
            activity = crmClientActivity(stat?.firstDate, stat?.lastDate, today)
        )
    }
}

/**
 * Поиск по имени и контактам, фильтр и сортировка. Для архива [rows] — архивные клиенты,
 * дополнительного отбора по фильтру не делается.
 */
fun crmFilterClients(
    rows: List<CrmClientRow>,
    contacts: Map<Long, List<String>>,
    query: String,
    filter: CrmClientsFilter,
    sort: CrmClientsSort
): List<CrmClientRow> {
    val text = query.trim().lowercase()
    // Телефон ищем и по цифрам: «+7 (900)» найдётся по «7900». Только если запрос похож
    // на номер — иначе «ул. 8 Марта» искала бы клиентов по цифре «8» в телефоне
    val digits = if (text.all { it.isDigit() || it in "+-() " }) text.filter { it.isDigit() } else ""
    val found = rows.filter { row ->
        val byFilter = when (filter) {
            CrmClientsFilter.ALL, CrmClientsFilter.ARCHIVE -> true
            CrmClientsFilter.FAVORITE -> row.client.isFavorite
            CrmClientsFilter.VIP -> row.client.isVip
            CrmClientsFilter.NEW -> row.activity == CrmClientActivity.NEW
            CrmClientsFilter.INACTIVE -> row.activity == CrmClientActivity.INACTIVE
        }
        byFilter && (text.isEmpty() ||
                row.client.name.lowercase().contains(text) ||
                contacts[row.client.id].orEmpty().any { value ->
                    value.lowercase().contains(text) ||
                            (digits.isNotEmpty() && value.filter { it.isDigit() }.contains(digits))
                })
    }
    val byName = compareBy<CrmClientRow> { it.client.name.lowercase() }
    return when (sort) {
        CrmClientsSort.REVENUE -> found.sortedWith(compareByDescending<CrmClientRow> { it.total }.then(byName))
        CrmClientsSort.NAME -> found.sortedWith(byName)
        // Клиенты без покупок — в конце; ISO-даты сравниваются как строки
        CrmClientsSort.LAST_PURCHASE -> found.sortedWith(
            compareByDescending<CrmClientRow> { it.lastDate ?: "" }.then(byName)
        )
    }
}
