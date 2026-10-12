package com.zaroslikov.crm.data.room.repository

import com.zaroslikov.crm.data.room.dao.CrmTaskDao
import com.zaroslikov.crm.data.room.mapper.toDomain
import com.zaroslikov.crm.data.room.mapper.toTable
import com.zaroslikov.crm.domain.model.DomainCrmTask
import com.zaroslikov.crm.domain.repository.CrmTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CrmTaskRepositoryImpl @Inject constructor(
    private val crmTaskDao: CrmTaskDao
) : CrmTaskRepository {

    override fun getTasks(idPT: Long, isDone: Boolean): Flow<List<DomainCrmTask>> {
        return crmTaskDao.getTasks(idPT, isDone).map { list -> list.map { it.toDomain() } }
    }

    override fun getUpcoming(idPT: Long, limit: Int): Flow<List<DomainCrmTask>> {
        return crmTaskDao.getUpcoming(idPT, limit).map { list -> list.map { it.toDomain() } }
    }

    override fun getForClient(clientId: Long): Flow<List<DomainCrmTask>> {
        return crmTaskDao.getForClient(clientId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsertTask(task: DomainCrmTask): Long {
        return crmTaskDao.upsertTask(task.toTable())
    }

    override suspend fun setDone(id: Long, isDone: Boolean) {
        crmTaskDao.setDone(id, isDone)
    }

    override suspend fun deleteTask(task: DomainCrmTask) {
        crmTaskDao.deleteTask(task.toTable())
    }
}
