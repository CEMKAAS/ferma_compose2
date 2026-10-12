package com.zaroslikov.crm.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.crm.data.room.database.CrmDatabase
import com.zaroslikov.crm.data.room.mapper.toDomain
import com.zaroslikov.crm.data.room.mapper.toTable
import com.zaroslikov.crm.domain.model.DomainCrmBackup
import com.zaroslikov.crm.domain.model.crmSalesKey
import com.zaroslikov.crm.domain.repository.CrmBackupRepository
import javax.inject.Inject

class CrmBackupRepositoryImpl @Inject constructor(
    private val database: CrmDatabase
) : CrmBackupRepository {

    override suspend fun export(): DomainCrmBackup = database.withTransaction {
        DomainCrmBackup(
            clients = database.crmClientDao().getAllForExport().map { it.toDomain() },
            contacts = database.crmContactDao().getAllForExport().map { it.toDomain() },
            interactions = database.crmInteractionDao().getAllForExport().map { it.toDomain() },
            tasks = database.crmTaskDao().getAllForExport().map { it.toDomain() }
        )
    }

    /**
     * Бэкап — внешний файл, ему не доверяем: строки чужих проектов, дубликаты id и
     * `(idPT, sales_key)` отбрасываются, а не валят импорт по ограничениям БД. Контакты и
     * взаимодействия без уцелевшего клиента отбрасываются, у задач обнуляется `clientId`.
     */
    override suspend fun import(backup: DomainCrmBackup, projectIds: Set<Long>) {
        val clients = backup.clients
            .filter { it.idPT in projectIds }
            .map { it.copy(salesKey = crmSalesKey(it.salesKey)) }
            .distinctBy { it.id }
            .distinctBy { it.idPT to it.salesKey }
        val clientIds = clients.map { it.id }.toSet()
        val contacts = backup.contacts.filter { it.clientId in clientIds }.distinctBy { it.id }
        val interactions = backup.interactions.filter { it.clientId in clientIds }.distinctBy { it.id }
        val tasks = backup.tasks
            .filter { it.idPT in projectIds }
            .distinctBy { it.id }
            .map { if (it.clientId != null && it.clientId !in clientIds) it.copy(clientId = null) else it }
        database.withTransaction {
            clearTables()
            database.crmClientDao().insertAllForImport(clients.map { it.toTable() })
            database.crmContactDao().insertAll(contacts.map { it.toTable() })
            database.crmInteractionDao().insertAllForImport(interactions.map { it.toTable() })
            database.crmTaskDao().insertAllForImport(tasks.map { it.toTable() })
        }
    }

    override suspend fun deleteForProject(idPT: Long) {
        database.withTransaction {
            database.crmTaskDao().deleteForProject(idPT)
            database.crmClientDao().deleteForProject(idPT)
        }
    }

    override suspend fun clearAll() {
        database.withTransaction { clearTables() }
    }

    // Порядок: сначала дочерние таблицы, потом клиенты
    private suspend fun clearTables() {
        database.crmTaskDao().clear()
        database.crmInteractionDao().clear()
        database.crmContactDao().clear()
        database.crmClientDao().clear()
    }
}
