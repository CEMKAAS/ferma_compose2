package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.RecyclingDao
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.RecyclingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class RecyclingRepositoryImpl @Inject constructor(
    private val recyclingDao: RecyclingDao
) : RecyclingRepository {

    override fun getAllRecyclingForExport(): Flow<List<DomainRecycling>> =
        recyclingDao.getAllRecyclingForExport().map { list -> list.map { it.toDomainMap() } }

    override suspend fun clearAndInsertRecyclingForImport(list: List<DomainRecycling>) =
        recyclingDao.clearAndInsertRecyclingForImport(list.map { it.toRoomMap() })

    override fun getRecyclingList(idPT: Long): Flow<List<DomainRecycling>> =
        recyclingDao.getRecyclingList(idPT).map { list -> list.map { it.toDomainMap() } }

    override suspend fun insertRecycling(
        recycling: DomainRecycling,
        ingredients: List<DomainWriteOffTable>,
        todayIso: String
    ): Long {
        val readyDate = recycling.readyYear?.let { year ->
            LocalDate.of(year, recycling.readyMonth ?: 1, recycling.readyDay ?: 1)
        }
        val completeNow = readyDate == null || !readyDate.isAfter(LocalDate.parse(todayIso))
        return recyclingDao.insertRecycling(
            recycling = recycling.copy(isCompleted = false, addId = null).toRoomMap(),
            ingredients = ingredients.map { it.toRoomMap() },
            completeNow = completeNow
        )
    }

    override suspend fun completeRecycling(id: Long, day: Int, month: Int, year: Int) =
        recyclingDao.completeRecycling(id, day, month, year)

    override suspend fun completeDueRecycling(todayIso: String) =
        recyclingDao.completeDueRecycling(todayIso)

    override suspend fun deleteRecycling(id: Long) = recyclingDao.deleteRecycling(id)
}
