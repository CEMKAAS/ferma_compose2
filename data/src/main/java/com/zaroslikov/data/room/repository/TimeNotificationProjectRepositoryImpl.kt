package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.TimeNotificationProjectDao
import com.zaroslikov.data.room.mapper.dto.project.toDomainTimeNotificationProjectDto
import com.zaroslikov.data.room.mapper.table.toDomainTimeNotificationProject
import com.zaroslikov.data.room.mapper.table.toTimeNotificationProjectTable
import com.zaroslikov.domain.models.dto.project.DomainTimeNotificationProjectDto
import com.zaroslikov.domain.models.table.project.DomainTimeNotificationProject
import com.zaroslikov.domain.repository.TimeNotificationProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TimeNotificationProjectRepositoryImpl @Inject constructor(private val timeNotificationProjectDao: TimeNotificationProjectDao) :
    TimeNotificationProjectRepository {
    override fun getAllTimeNotificationTableForExport(): Flow<List<DomainTimeNotificationProject>> {
        return timeNotificationProjectDao.getAllTimeNotificationTableForExport()
            .map { it -> it.map { it.toDomainTimeNotificationProject() } }
    }

    override suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotificationProject: List<DomainTimeNotificationProject>) {
        return timeNotificationProjectDao.clearAndInsertTimeNotificationTableForImport(
            domainTimeNotificationProject.map { it.toTimeNotificationProjectTable() })
    }

    override fun getTimeNotificationByProjectId(projectId: Long): Flow<List<DomainTimeNotificationProject>> {
        return timeNotificationProjectDao.getTimeNotificationByProjectId(projectId)
            .map { it -> it.map { it.toDomainTimeNotificationProject() } }
    }

    override fun getTimeNotificationInAllActiveProject(): Flow<List<DomainTimeNotificationProjectDto>> {
        return timeNotificationProjectDao.getTimeNotificationInAllActiveProject()
            .map { it -> it.map { it.toDomainTimeNotificationProjectDto() } }
    }

    override suspend fun insertTimeNotification(domainTimeNotificationProject: DomainTimeNotificationProject): Long {
        return timeNotificationProjectDao.insertTimeNotification(domainTimeNotificationProject.toTimeNotificationProjectTable())
    }

    override suspend fun updateTimeNotification(domainTimeNotificationProject: DomainTimeNotificationProject) {
        return timeNotificationProjectDao.updateTimeNotification(domainTimeNotificationProject.toTimeNotificationProjectTable())
    }

    override suspend fun deleteTimeNotification(domainTimeNotificationProject: DomainTimeNotificationProject) {
        return timeNotificationProjectDao.deleteTimeNotification(domainTimeNotificationProject.toTimeNotificationProjectTable())
    }

    override suspend fun deleteTimeNotificationById(id: Long) {
        return timeNotificationProjectDao.deleteTimeNotificationById(id)
    }
}