package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.IncubatorTableDao
import com.zaroslikov.data.room.mapper.table.toDomainIncubatorTable
import com.zaroslikov.data.room.mapper.table.toIncubatorTable
import com.zaroslikov.domain.models.table.DomainIncubatorTable
import com.zaroslikov.domain.repository.IncubatorTableRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IncubatorTableRepositoryImpl @Inject constructor(private val incubatorTableDao: IncubatorTableDao) :
    IncubatorTableRepository {
    override fun getAllIncubator(): Flow<List<DomainIncubatorTable>> {
        return incubatorTableDao.getAllIncubator()
            .map { it -> it.map { it.toDomainIncubatorTable() } }
    }

    override fun getIncubator(id: Long): Flow<DomainIncubatorTable> {
        return incubatorTableDao.getIncubator(id).map { it.toDomainIncubatorTable() }
    }

    override suspend fun insertIncubator(domainIncubatorTable: DomainIncubatorTable) {
        return incubatorTableDao.insertIncubator(domainIncubatorTable.toIncubatorTable())
    }

    override suspend fun insertIncubatorLong(domainIncubatorTable: DomainIncubatorTable): Long {
        return incubatorTableDao.insertIncubatorLong(domainIncubatorTable.toIncubatorTable())
    }

    override suspend fun updateIncubator(domainIncubatorTable: DomainIncubatorTable) {
        return incubatorTableDao.updateIncubator(domainIncubatorTable.toIncubatorTable())
    }

    override suspend fun deleteIncubator(domainIncubatorTable: DomainIncubatorTable) {
        return incubatorTableDao.deleteIncubator(domainIncubatorTable.toIncubatorTable())
    }

    override fun getBrandIncubatorList(): Flow<List<String>> {
        return incubatorTableDao.getBrandIncubatorList()
    }

    override fun getModelIncubatorList(): Flow<List<String>> {
        return incubatorTableDao.getModelIncubatorList()
    }
}
