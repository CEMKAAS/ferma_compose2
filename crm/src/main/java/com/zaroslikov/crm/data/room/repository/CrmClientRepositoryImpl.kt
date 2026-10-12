package com.zaroslikov.crm.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.crm.data.room.dao.CrmClientDao
import com.zaroslikov.crm.data.room.database.CrmDatabase
import com.zaroslikov.crm.data.room.mapper.toDomain
import com.zaroslikov.crm.data.room.mapper.toTable
import com.zaroslikov.crm.data.room.table.CrmClientTable
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.DomainCrmClient
import com.zaroslikov.crm.domain.model.DomainCrmContact
import com.zaroslikov.crm.domain.model.crmSalesKey
import com.zaroslikov.crm.domain.repository.CrmClientRepository
import com.zaroslikov.domain.models.dto.crm.DomainCrmBuyerStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CrmClientRepositoryImpl @Inject constructor(
    private val database: CrmDatabase,
    private val crmClientDao: CrmClientDao
) : CrmClientRepository {

    override fun getClients(idPT: Long): Flow<List<DomainCrmClient>> {
        return crmClientDao.getClients(idPT).map { list -> list.map { it.toDomain() } }
    }

    override fun getArchivedClients(idPT: Long): Flow<List<DomainCrmClient>> {
        return crmClientDao.getArchivedClients(idPT).map { list -> list.map { it.toDomain() } }
    }

    override fun getClient(id: Long): Flow<DomainCrmClient?> {
        return crmClientDao.getClient(id).map { it?.toDomain() }
    }

    override suspend fun existsBySalesKey(idPT: Long, salesKey: String): Boolean {
        return crmClientDao.existsBySalesKey(idPT, crmSalesKey(salesKey))
    }

    override suspend fun findBySalesKey(idPT: Long, salesKey: String): DomainCrmClient? {
        return crmClientDao.findBySalesKey(idPT, crmSalesKey(salesKey))?.toDomain()
    }

    override suspend fun insertClient(client: DomainCrmClient): Long {
        return crmClientDao.insertClient(client.toTable().copy(salesKey = crmSalesKey(client.salesKey)))
    }

    override suspend fun updateClient(client: DomainCrmClient) {
        crmClientDao.updateClient(
            id = client.id,
            name = client.name,
            isVip = client.isVip,
            note = client.note,
            isFavorite = client.isFavorite
        )
    }

    override suspend fun createClientWithContacts(
        client: DomainCrmClient,
        contacts: List<DomainCrmContact>
    ): Long = database.withTransaction {
        val id = insertClient(client)
        database.crmContactDao().replaceForClient(id, contacts.map { it.toTable() })
        id
    }

    override suspend fun updateClientWithContacts(
        client: DomainCrmClient,
        contacts: List<DomainCrmContact>
    ) {
        database.withTransaction {
            updateClient(client)
            database.crmContactDao().replaceForClient(client.id, contacts.map { it.toTable() })
        }
    }

    override suspend fun archiveClient(id: Long) {
        crmClientDao.setArchived(id, true)
    }

    override suspend fun restoreClient(id: Long) {
        crmClientDao.setArchived(id, false)
    }

    override suspend fun deleteClientForever(id: Long) {
        crmClientDao.deleteClient(id)
    }

    // buyer уже прошёл SQLite TRIM в запросе статистики — повторно не обрезаем
    override suspend fun syncFromSales(
        idPT: Long,
        stats: List<DomainCrmBuyerStats>,
        today: String
    ) {
        val clients = stats
            .filter { crmSalesKey(it.buyer).isNotEmpty() }
            .map {
                CrmClientTable(
                    idPT = idPT,
                    name = it.buyer,
                    salesKey = it.buyer,
                    source = CrmClientSource.SALES,
                    createdDate = it.firstDate ?: today
                )
            }
        if (clients.isNotEmpty()) crmClientDao.insertIgnore(clients)
    }
}
