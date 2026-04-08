package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.TimeNotificationDao
import com.zaroslikov.data.room.mapper.table.toDomainTimeNotification
import com.zaroslikov.data.room.mapper.table.toTimeNotificationTable
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotification
import com.zaroslikov.domain.repository.TimeNotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class TimeNotificationRepositoryImpl @Inject constructor(private val timeNotificationDao: TimeNotificationDao) :
    TimeNotificationRepository {
    override fun getAllTimeNotificationTableForExport(): Flow<List<DomainTimeNotification>> {
        return timeNotificationDao.getAllTimeNotificationTableForExport()
            .map { it -> it.map { it.toDomainTimeNotification() } }
    }

    override suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotification: List<DomainTimeNotification>) {
        return timeNotificationDao.clearAndInsertTimeNotificationTableForImport(
            domainTimeNotification.map { it.toTimeNotificationTable() })
    }

    override fun getTimeNotificationByBookmarkId(bookmarkId: Long): Flow<List<DomainTimeNotification>> {
        return timeNotificationDao.getTimeNotificationByBookmarkId(bookmarkId)
            .map { it -> it.map { it.toDomainTimeNotification() } }
    }

    override suspend fun insertTimeNotification(domainTimeNotification: DomainTimeNotification): Long {
        return timeNotificationDao.insertTimeNotification(domainTimeNotification.toTimeNotificationTable())
    }

    override suspend fun updateTimeNotification(domainTimeNotification: DomainTimeNotification) {
        return timeNotificationDao.updateTimeNotification(domainTimeNotification.toTimeNotificationTable())
    }

    override suspend fun deleteTimeNotification(domainTimeNotification: DomainTimeNotification) {
        return timeNotificationDao.deleteTimeNotification(domainTimeNotification.toTimeNotificationTable())
    }

    override suspend fun deleteTimeNotificationById(id: Long) {
        return timeNotificationDao.deleteTimeNotificationById(id)
    }
}