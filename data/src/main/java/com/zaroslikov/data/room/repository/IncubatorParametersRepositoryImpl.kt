package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.IncubatorParametersDao
import com.zaroslikov.data.room.mapper.table.toDomainIncubatorParameters
import com.zaroslikov.data.room.mapper.table.toIncubatorParameters
import com.zaroslikov.domain.models.table.DomainIncubatorParameters
import com.zaroslikov.domain.repository.IncubatorParametersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class IncubatorParametersRepositoryImpl @Inject constructor(private val incubatorParametersDao: IncubatorParametersDao) :
    IncubatorParametersRepository {
    override fun getIncubatorListArh4(idPT: Long): Flow<List<DomainIncubatorParameters>> {
        return incubatorParametersDao.getIncubatorListArh4(idPT)
            .map { it -> it.map { it.toDomainIncubatorParameters() } }
    }

    override fun getIncubatorList(id: Long): Flow<List<DomainIncubatorParameters>> {
        return incubatorParametersDao.getIncubatorList(id).map { it -> it.map { it.toDomainIncubatorParameters() } }
    }

    override fun getIncubatorList2(id: Long): Flow<List<DomainIncubatorParameters>> {
        return incubatorParametersDao.getIncubatorList2(id).map { it -> it.map { it.toDomainIncubatorParameters() } }
    }

    override fun getIncubator(id: Long): Flow<DomainIncubatorParameters> {
        return incubatorParametersDao.getIncubator(id).map { it.toDomainIncubatorParameters() }
    }

    override fun getIncubatorEditDay(
        id: Long,
        day: Int
    ): Flow<DomainIncubatorParameters> {
        return incubatorParametersDao.getIncubatorEditDay(id, day).map { it.toDomainIncubatorParameters() }
    }

    override suspend fun insertIncubator(domainIncubatorParameters: DomainIncubatorParameters) {
        return incubatorParametersDao.insertIncubator(domainIncubatorParameters.toIncubatorParameters())
    }

    override suspend fun updateIncubator(domainIncubatorParameters: DomainIncubatorParameters) {
        return incubatorParametersDao.updateIncubator(domainIncubatorParameters.toIncubatorParameters())
    }

    /*override fun getIncubatorCountNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int> {
        return incubatorParametersDao.getIncubatorCountNewYearP(dateBegin, dateEnd)
    }

    override fun getEggInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int> {
        return incubatorParametersDao.getEggInIncubatorNewYear(dateBegin, dateEnd)
    }

    override fun getChickenInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int> {
        return incubatorParametersDao.getChickenInIncubatorNewYear(dateBegin, dateEnd)
    }

    override fun getTypeIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<String> {
        return incubatorParametersDao.getTypeIncubatorNewYear(dateBegin, dateEnd)
    }*/
}
