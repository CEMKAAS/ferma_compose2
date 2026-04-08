package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.incubator.DomainTimeNotification
import kotlinx.coroutines.flow.Flow

interface TimeNotificationRepository {
    fun getAllTimeNotificationTableForExport(): Flow<List<DomainTimeNotification>>
    suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotification: List<DomainTimeNotification>)
    fun getTimeNotificationByBookmarkId(bookmarkId: Long): Flow<List<DomainTimeNotification>>
    suspend fun insertTimeNotification(domainTimeNotification: DomainTimeNotification): Long
    suspend fun updateTimeNotification(domainTimeNotification: DomainTimeNotification)
    suspend fun deleteTimeNotification(domainTimeNotification: DomainTimeNotification)
    suspend fun deleteTimeNotificationById(id: Long)
}