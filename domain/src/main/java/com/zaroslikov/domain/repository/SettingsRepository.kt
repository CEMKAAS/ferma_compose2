package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(idPT: Long): Flow<DomainSettings>
    suspend fun insertSettings(domainSettings: DomainSettings)
    suspend fun insertSettingsLong(domainSettings: DomainSettings): Long
    suspend fun updateSettings(domainSettings: DomainSettings)
    suspend fun deleteSettings(domainSettings: DomainSettings)
}