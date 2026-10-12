package com.zaroslikov.crm.data.room.repository

import com.zaroslikov.crm.data.room.dao.CrmInteractionDao
import com.zaroslikov.crm.data.room.mapper.toDomain
import com.zaroslikov.crm.data.room.mapper.toTable
import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import com.zaroslikov.crm.domain.repository.CrmInteractionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CrmInteractionRepositoryImpl @Inject constructor(
    private val crmInteractionDao: CrmInteractionDao
) : CrmInteractionRepository {

    override fun getForClient(clientId: Long): Flow<List<DomainCrmInteraction>> {
        return crmInteractionDao.getForClient(clientId).map { list -> list.map { it.toDomain() } }
    }

    override fun getRecentForProject(idPT: Long, limit: Int): Flow<List<DomainCrmInteraction>> {
        return crmInteractionDao.getRecentForProject(idPT, limit)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertInteraction(interaction: DomainCrmInteraction): Long {
        return crmInteractionDao.insertInteraction(interaction.toTable())
    }

    override suspend fun deleteInteraction(interaction: DomainCrmInteraction) {
        crmInteractionDao.deleteInteraction(interaction.toTable())
    }
}
