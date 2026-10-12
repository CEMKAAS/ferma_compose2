package com.zaroslikov.crm.domain.repository

import com.zaroslikov.crm.domain.model.DomainCrmInteraction
import kotlinx.coroutines.flow.Flow

interface CrmInteractionRepository {

    fun getForClient(clientId: Long): Flow<List<DomainCrmInteraction>>

    /** Последние взаимодействия по неархивным клиентам проекта. */
    fun getRecentForProject(idPT: Long, limit: Int): Flow<List<DomainCrmInteraction>>

    suspend fun insertInteraction(interaction: DomainCrmInteraction): Long

    suspend fun deleteInteraction(interaction: DomainCrmInteraction)
}
