package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.incubator.DomainTimeNotificationIncubator
import com.zaroslikov.domain.models.table.incubator.DomainTimeNotificationIncubatorDto
import kotlinx.coroutines.flow.Flow

interface TimeNotificationIncubatorRepository {
    fun getAllTimeNotificationTableForExport(): Flow<List<DomainTimeNotificationIncubator>>
    suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotificationIncubator: List<DomainTimeNotificationIncubator>)
    fun getTimeNotificationByBookmarkId(bookmarkId: Long): Flow<List<DomainTimeNotificationIncubator>>

    fun getTimeNotificationInAllActiveBookmark(): Flow<List<DomainTimeNotificationIncubatorDto>>

    suspend fun insertTimeNotification(domainTimeNotificationIncubator: DomainTimeNotificationIncubator): Long
    suspend fun updateTimeNotification(domainTimeNotificationIncubator: DomainTimeNotificationIncubator)
    suspend fun deleteTimeNotification(domainTimeNotificationIncubator: DomainTimeNotificationIncubator)
    suspend fun deleteTimeNotificationById(id: Long)
}