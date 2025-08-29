package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.IncubatorDao
import com.zaroslikov.data.room.mapper.table.toDomainIncubator
import com.zaroslikov.data.room.mapper.table.toIncubator
import com.zaroslikov.domain.models.table.DomainIncubator
import com.zaroslikov.domain.repository.IncubatorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IncubatorRepositoryImpl @Inject constructor(private val incubatorDao: IncubatorDao) :
    IncubatorRepository {
    override fun getIncubatorListArh4(idPT: Long): Flow<List<DomainIncubator>> {
        return incubatorDao.getIncubatorListArh4(idPT)
            .map { it -> it.map { it.toDomainIncubator() } }
    }

    override fun getIncubatorList(id: Long): Flow<List<DomainIncubator>> {
        return incubatorDao.getIncubatorList(id).map { it -> it.map { it.toDomainIncubator() } }
    }

    override fun getIncubatorList2(id: Long): Flow<List<DomainIncubator>> {
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

    override fun getIncubatorCountNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int> {
        return incubatorDao.getIncubatorCountNewYear(dateBegin, dateEnd)
    }

    override fun getEggInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int> {
        return incubatorDao.getEggInIncubatorNewYear(dateBegin, dateEnd)
    }

    override fun getChickenInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int> {
        return incubatorDao.getChickenInIncubatorNewYear(dateBegin, dateEnd)
    }

    override fun getTypeIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<String> {
        return incubatorDao.getTypeIncubatorNewYear(dateBegin, dateEnd)
    }
}