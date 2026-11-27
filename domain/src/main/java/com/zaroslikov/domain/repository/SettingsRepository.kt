package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.WarehouseCard
import com.zaroslikov.domain.models.table.DomainSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getAllSettingsTableForExport(): Flow<List<DomainSettings>>

    suspend fun clearAndInsertSettingsTableForImport(domainSettings: List<DomainSettings>)
    fun getSettings(idPT: Long): Flow<DomainSettings>
    suspend fun insertSettings(domainSettings: DomainSettings)
    suspend fun insertSettingsLong(domainSettings: DomainSettings): Long
    suspend fun updateSettings(domainSettings: DomainSettings)
    suspend fun updateWarehouseCards(idPT: Long, cards: List<WarehouseCard>) =
        updateWarehouseCardCodes(idPT, cards.distinct().map(WarehouseCard::code))
    suspend fun updateWarehouseCardCodes(idPT: Long, codes: List<String>)
    suspend fun deleteSettings(domainSettings: DomainSettings)
    fun getCurrencySuffixList(): Flow<List<Suffix>>
}
