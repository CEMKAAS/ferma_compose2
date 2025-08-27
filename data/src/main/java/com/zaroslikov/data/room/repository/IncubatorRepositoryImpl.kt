package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.IncubatorDao
import com.zaroslikov.data.room.mapper.table.toDomainIncubator
import com.zaroslikov.data.room.mapper.table.toIncubator
import com.zaroslikov.domain.models.table.DomainIncubator
import com.zaroslikov.domain.repository.IncubatorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IncubatorRepositoryImpl(private val incubatorDao: IncubatorDao) : IncubatorRepository {
    override suspend fun getIncubatorListArh4(idPT: Long): Flow<List<DomainIncubator>> {
        return incubatorDao.getIncubatorListArh4(idPT)
            .map { it -> it.map { it.toDomainIncubator() } }
    }

    override fun getIncubatorList(id: Long): Flow<List<DomainIncubator>> {
        return incubatorDao.getIncubatorList(id).map { it -> it.map { it.toDomainIncubator() } }
    }

    override suspend fun getIncubatorList2(id: Long): Flow<List<DomainIncubator>> {
        return incubatorDao.getIncubatorList2(id).map { it -> it.map { it.toDomainIncubator() } }
    }

    override fun getIncubator(id: Long): Flow<DomainIncubator> {
        return incubatorDao.getIncubator(id).map { it.toDomainIncubator() }
    }

    override fun getIncubatorEditDay(
        id: Long,
        day: Int
    ): Flow<DomainIncubator> {
        return incubatorDao.getIncubatorEditDay(id, day).map { it.toDomainIncubator() }
    }

    override suspend fun insertIncubator(domainIncubator: DomainIncubator) {
        return incubatorDao.insertIncubator(domainIncubator.toIncubator())
    }

    override suspend fun updateIncubator(domainIncubator: DomainIncubator) {
        return incubatorDao.updateIncubator(domainIncubator.toIncubator())
    }
}