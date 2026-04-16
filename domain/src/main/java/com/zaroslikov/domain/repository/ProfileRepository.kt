package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainProjectTable
import com.zaroslikov.domain.models.table.profile.DomainProfileTable
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getAllProfileTableForExport(): Flow<List<DomainProfileTable>>

    suspend fun clearAndInsertProfileTableForImport(domainProfileTable: List<DomainProfileTable>)
    suspend fun updateProfile(profileTable: DomainProfileTable)

    fun getProfile(): Flow<DomainProfileTable>

    fun getAllCurrenciesSorted(): Flow<List<Suffix>>

    fun createProfile(profileTable: DomainProfileTable)
}