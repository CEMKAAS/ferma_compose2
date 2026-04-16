package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.TimeNotificationIncubatorDao
import com.zaroslikov.data.room.mapper.dto.incubator.toDomainTimeNotificationDto
import com.zaroslikov.data.room.mapper.table.toDomainTimeNotification
import com.zaroslikov.data.room.mapper.table.toTimeNotificationTable
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotificationIncubator
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotificationIncubatorDto
import com.zaroslikov.domain.repository.TimeNotificationIncubatorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class TimeNotificationIncubatorRepositoryImpl @Inject constructor(private val timeNotificationIncubatorDao: TimeNotificationIncubatorDao) :
    TimeNotificationIncubatorRepository {
    override fun getAllTimeNotificationTableForExport(): Flow<List<DomainTimeNotificationIncubator>> {
        return timeNotificationIncubatorDao.getAllTimeNotificationTableForExport()
            .map { it -> it.map { it.toDomainTimeNotification() } }
    }

    override suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotificationIncubator: List<DomainTimeNotificationIncubator>) {
        return timeNotificationIncubatorDao.clearAndInsertTimeNotificationTableForImport(
            domainTimeNotificationIncubator.map { it.toTimeNotificationTable() })
    }

    override fun getTimeNotificationByBookmarkId(bookmarkId: Long): Flow<List<DomainTimeNotificationIncubator>> {
        return timeNotificationIncubatorDao.getTimeNotificationByBookmarkId(bookmarkId)
            .map { it -> it.map { it.toDomainTimeNotification() } }
    }

    override fun getTimeNotificationInAllActiveBookmark(): Flow<List<DomainTimeNotificationIncubatorDto>> {
        return timeNotificationIncubatorDao.getTimeNotificationInAllActiveBookmark()
            .map { it -> it.map { it.toDomainTimeNotificationDto() } }
    }

    override suspend fun insertTimeNotification(domainTimeNotificationIncubator: DomainTimeNotificationIncubator): Long {
        return timeNotificationIncubatorDao.insertTimeNotification(domainTimeNotificationIncubator.toTimeNotificationTable())
    }

    override suspend fun updateTimeNotification(domainTimeNotificationIncubator: DomainTimeNotificationIncubator) {
        return timeNotificationIncubatorDao.updateTimeNotification(domainTimeNotificationIncubator.toTimeNotificationTable())
    }

    override suspend fun deleteTimeNotification(domainTimeNotificationIncubator: DomainTimeNotificationIncubator) {
        return timeNotificationIncubatorDao.deleteTimeNotification(domainTimeNotificationIncubator.toTimeNotificationTable())
    }

    override suspend fun deleteTimeNotificationById(id: Long) {
        return timeNotificationIncubatorDao.deleteTimeNotificationById(id)
    }
}