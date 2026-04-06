package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.ProfileDao
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toDomainProfileTable
import com.zaroslikov.data.room.mapper.table.toProfileTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.profile.DomainProfileTable
import com.zaroslikov.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class ProfileTableRepositoryImpl @Inject constructor(private val profileDao: ProfileDao) :
    ProfileRepository {
    override fun getAllProfileTableForExport(): Flow<List<DomainProfileTable>> {
        return profileDao.getAllProfileTableForExport()
            .map { it -> it.map { it.toDomainProfileTable() } }
    }

    override suspend fun clearAndInsertProfileTableForImport(domainProfileTable: List<DomainProfileTable>) {
        return profileDao.clearAndInsertAllProfileTable(domainProfileTable.map { it.toProfileTable() })
    }

    override suspend fun updateProfile(profileTable: DomainProfileTable) {
        return profileDao.updateProfile(profileTable.toProfileTable())
    }

    override fun getProfile(): Flow<DomainProfileTable> {
        return profileDao.getProfile().map { it.toDomainProfileTable() }
    }

    override fun getAllCurrenciesSorted(): Flow<List<Suffix>> {
        return profileDao.getAllCurrenciesSorted()
    }
}