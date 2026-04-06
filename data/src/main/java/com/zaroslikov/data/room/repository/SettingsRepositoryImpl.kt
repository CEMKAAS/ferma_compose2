package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.SettingsDao
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toDomainSettings
import com.zaroslikov.data.room.mapper.table.toSettingsTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import com.zaroslikov.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val settingsDao: SettingsDao) :
    SettingsRepository {
    override fun getAllSettingsTableForExport(): Flow<List<DomainSettings>> {
        return settingsDao.getAllSettingsTableForExport()
            .map { it -> it.map { it.toDomainSettings() } }
    }

    override suspend fun clearAndInsertSettingsTableForImport(domainSettings: List<DomainSettings>) {
        return settingsDao.clearAndInsertAllSettingsTableForImport(domainSettings.map { it.toSettingsTable() })
    }

    override fun getSettings(idPT: Long): Flow<DomainSettings> {
        return settingsDao.getSettings(idPT).map { it.toDomainSettings() }
    }

    override suspend fun insertSettings(domainSettings: DomainSettings) {
        return settingsDao.insertSettings(domainSettings.toSettingsTable())
    }

    override suspend fun insertSettingsLong(domainSettings: DomainSettings): Long {
        return settingsDao.insertSettingsLong(domainSettings.toSettingsTable())
    }

    override suspend fun updateSettings(domainSettings: DomainSettings) {
        return settingsDao.updateSettings(domainSettings.toSettingsTable())
    }

    override suspend fun deleteSettings(domainSettings: DomainSettings) {
        return settingsDao.deleteSettings(domainSettings.toSettingsTable())
    }

    override fun getCurrencySuffixList(): Flow<List<Suffix>> {
        return settingsDao.getCurrencySuffixList().map { it -> it.map { it } }
    }

}