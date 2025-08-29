package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainIncubator
import kotlinx.coroutines.flow.Flow

interface IncubatorRepository {
    fun getIncubatorListArh4(idPT: Long): Flow<List<DomainIncubator>>
    fun getIncubatorList(id: Long): Flow<List<DomainIncubator>>
    fun getIncubatorList2(id: Long): Flow<List<DomainIncubator>>
    fun getIncubator(id: Long): Flow<DomainIncubator>
    fun getIncubatorEditDay(id: Long, day: Int): Flow<DomainIncubator>
    suspend fun insertIncubator(domainIncubator: DomainIncubator)
    suspend fun updateIncubator(domainIncubator: DomainIncubator)

    fun getIncubatorCountNewYear(
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
    ): Flow<String>
}