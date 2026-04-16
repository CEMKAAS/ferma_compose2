package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.DomainExpensesAnimal
import com.zaroslikov.domain.models.table.DomainBookmark
import com.zaroslikov.domain.models.table.app.DomainAppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getAllAppSettingsTableForExport(): Flow<List<DomainAppSettings>>
    suspend fun clearAndInsertAppSettingsTableForImport(domainAppSettings: List<DomainAppSettings>)

    suspend fun updateAppSettings(domainAppSettings: DomainAppSettings)

    fun getAppSettings(): Flow<DomainAppSettings>

    fun getFirstLaunch(): Flow<Boolean>
    suspend fun createAppSettings(domainAppSettings: DomainAppSettings)
}