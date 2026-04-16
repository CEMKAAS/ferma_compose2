package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.project.DomainTimeNotificationProjectDto
import com.zaroslikov.domain.models.table.project.DomainTimeNotificationProject
import kotlinx.coroutines.flow.Flow


interface TimeNotificationProjectRepository {
    fun getAllTimeNotificationTableForExport(): Flow<List<DomainTimeNotificationProject>>
    suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotificationProject: List<DomainTimeNotificationProject>)
    fun getTimeNotificationByProjectId(projectId: Long): Flow<List<DomainTimeNotificationProject>>
    fun getTimeNotificationInAllActiveProject(): Flow<List<DomainTimeNotificationProjectDto>>
    suspend fun insertTimeNotification(domainTimeNotificationProject: DomainTimeNotificationProject): Long
    suspend fun updateTimeNotification(domainTimeNotificationProject: DomainTimeNotificationProject)
    suspend fun deleteTimeNotification(domainTimeNotificationProject: DomainTimeNotificationProject)
    suspend fun deleteTimeNotificationById(id: Long)
}