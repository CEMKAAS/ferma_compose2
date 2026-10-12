package com.zaroslikov.crm.domain.repository

import com.zaroslikov.crm.domain.model.DomainCrmTask
import kotlinx.coroutines.flow.Flow

interface CrmTaskRepository {

    fun getTasks(idPT: Long, isDone: Boolean): Flow<List<DomainCrmTask>>

    /** Ближайшие невыполненные задачи проекта по сроку. */
    fun getUpcoming(idPT: Long, limit: Int): Flow<List<DomainCrmTask>>

    fun getForClient(clientId: Long): Flow<List<DomainCrmTask>>

    suspend fun upsertTask(task: DomainCrmTask): Long

    suspend fun setDone(id: Long, isDone: Boolean)

    suspend fun deleteTask(task: DomainCrmTask)
}
