package com.zaroslikov.crm.ui.clients

import com.zaroslikov.crm.domain.model.CrmClientActivity
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.domain.models.enums.Suffix

enum class CrmClientsFilter { ALL, FAVORITE, VIP, NEW, INACTIVE, ARCHIVE }

enum class CrmClientsSort { REVENUE, NAME, LAST_PURCHASE }

/** Клиент со статистикой продаж по его `salesKey`. */
data class CrmClientRow(
    val client: DomainCrmClient,
    val total: Double = 0.0,
    val orders: Int = 0,
    val lastDate: String? = null,
    val activity: CrmClientActivity = CrmClientActivity.NO_SALES
)

data class CrmClientsState(
    val isLoading: Boolean = true,
    val currency: Suffix = Suffix.RUBLE,
    val query: String = "",
    val filter: CrmClientsFilter = CrmClientsFilter.ALL,
    val sort: CrmClientsSort = CrmClientsSort.REVENUE,
    val rows: List<CrmClientRow> = emptyList(),
    val archivedRows: List<CrmClientRow> = emptyList(),
    /** Значения контактов по id клиента — для поиска. */
    val contacts: Map<Long, List<String>> = emptyMap(),
    /** Отфильтрованный и отсортированный список, который видит пользователь. */
    val visible: List<CrmClientRow> = emptyList(),
    val clientToDelete: DomainCrmClient? = null
)
