package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainIncubatorParameters
import kotlinx.coroutines.flow.Flow

interface IncubatorParametersRepository {
    fun getIncubatorListArh4(idPT: Long): Flow<List<DomainIncubatorParameters>>
    fun getIncubatorList(id: Long): Flow<List<DomainIncubatorParameters>>
    fun getIncubatorList2(id: Long): Flow<List<DomainIncubatorParameters>>
    fun getIncubator(id: Long): Flow<DomainIncubatorParameters>
    fun getIncubatorEditDay(id: Long, day: Int): Flow<DomainIncubatorParameters>
    suspend fun insertIncubator(domainIncubatorParameters: DomainIncubatorParameters)
    suspend fun updateIncubator(domainIncubatorParameters: DomainIncubatorParameters)

/*    fun getIncubatorCountNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getEggInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getChickenInIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Int>

    fun getTypeIncubatorNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<String>*/
}