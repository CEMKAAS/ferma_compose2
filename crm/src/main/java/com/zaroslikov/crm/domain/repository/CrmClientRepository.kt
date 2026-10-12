package com.zaroslikov.crm.domain.repository

import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import kotlinx.coroutines.flow.Flow

/** Ключ продаж во всех методах нормализуется через `crmSalesKey`. */
interface CrmClientRepository {

    /** Клиенты проекта без архивных. */
    fun getClients(idPT: Long): Flow<List<DomainCrmClient>>

    fun getArchivedClients(idPT: Long): Flow<List<DomainCrmClient>>

    fun getClient(id: Long): Flow<DomainCrmClient?>

    suspend fun existsBySalesKey(idPT: Long, salesKey: String): Boolean

    /** Клиент проекта (в том числе архивный) с таким ключом продаж или `null`. */
    suspend fun findBySalesKey(idPT: Long, salesKey: String): DomainCrmClient?

    /** Падает на дубликате `(idPT, sales_key)` — проверяй [existsBySalesKey] заранее. */
    suspend fun insertClient(client: DomainCrmClient): Long

    suspend fun updateClient(client: DomainCrmClient)

    /**
     * Создаёт клиента и его контакты в одной транзакции; возвращает id клиента.
     * Падает на дубликате `(idPT, sales_key)`, как [insertClient].
     */
    suspend fun createClientWithContacts(client: DomainCrmClient, contacts: List<DomainCrmContact>): Long

    /** Обновляет клиента и заменяет все его контакты в одной транзакции. */
    suspend fun updateClientWithContacts(client: DomainCrmClient, contacts: List<DomainCrmContact>)

    suspend fun archiveClient(id: Long)

    suspend fun restoreClient(id: Long)

    suspend fun deleteClientForever(id: Long)

    /**
     * Заводит клиентов по покупателям из продаж. Уже существующие (в том числе архивные)
     * не трогает — архивный клиент не воскресает.
     */
    suspend fun syncFromSales(idPT: Long, stats: List<DomainCrmBuyerStats>, today: String)
}
