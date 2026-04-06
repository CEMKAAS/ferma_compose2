package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.AppSettingsDao
import com.zaroslikov.data.room.mapper.table.toAppSettingsTable
import com.zaroslikov.data.room.mapper.table.toDomainAppSettings
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import com.zaroslikov.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppSettingsRepositoryImpl @Inject constructor(private val appSettingsDao: AppSettingsDao) :
    AppSettingsRepository {
    override fun getAllAppSettingsTableForExport(): Flow<List<DomainAppSettings>> {
        return appSettingsDao.getAllAppSettingsTableForExport()
            .map { it -> it.map { it.toDomainAppSettings() } }
    }

    override suspend fun clearAndInsertAppSettingsTableForImport(domainAppSettings: List<DomainAppSettings>) {
        return appSettingsDao.clearAndInsertAppSettingsTableForImport(domainAppSettings.map { it.toAppSettingsTable() })
    }

    override suspend fun updateAppSettings(domainAppSettings: DomainAppSettings) {
        return appSettingsDao.updateAppSettings(domainAppSettings.toAppSettingsTable())
    }

    override fun getAppSettings(): Flow<DomainAppSettings> {
        return appSettingsDao.getAppSettings().map { it.toDomainAppSettings() }
    }
}