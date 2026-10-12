package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import kotlinx.coroutines.flow.Flow

interface RecyclingRepository {

    fun getAllRecyclingForExport(): Flow<List<DomainRecycling>>

    suspend fun clearAndInsertRecyclingForImport(list: List<DomainRecycling>)

    /** Переработки проекта: сначала незавершённые (по дате готовности), затем завершённые. */
    fun getRecyclingList(idPT: Long): Flow<List<DomainRecycling>>

    /**
     * Сохраняет переработку и списание сырья одной транзакцией. Без даты готовности
     * (или если она уже наступила к [todayIso], `yyyy-MM-dd`) продукция сразу попадает в учёт.
     */
    suspend fun insertRecycling(
        recycling: DomainRecycling,
        ingredients: List<DomainWriteOffTable>,
        todayIso: String
    ): Long

    /** Завершает переработку досрочно: продукция поступает датой [day].[month].[year]. */
    suspend fun completeRecycling(id: Long, day: Int, month: Int, year: Int)

    /** Завершает все переработки, чья дата готовности наступила к [todayIso] (`yyyy-MM-dd`). */
    suspend fun completeDueRecycling(todayIso: String)

    /** Удаляет переработку вместе со списанием сырья и полученной продукцией. */
    suspend fun deleteRecycling(id: Long)
}
