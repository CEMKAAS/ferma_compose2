package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
import com.zaroslikov.domain.models.table.DomainRecyclingWithProducts
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import kotlinx.coroutines.flow.Flow

interface RecyclingRepository {

    fun getAllRecyclingForExport(): Flow<List<DomainRecycling>>

    fun getAllRecyclingProductsForExport(): Flow<List<DomainRecyclingProduct>>

    suspend fun clearAndInsertRecyclingForImport(
        list: List<DomainRecycling>,
        products: List<DomainRecyclingProduct>
    )

    /** Партии переработки проекта с продукцией (незавершённая — первой, по дате готовности) и сырьём. */
    fun getRecyclingList(idPT: Long): Flow<List<DomainRecyclingWithProducts>>

    /**
     * Сохраняет партию, её продукцию и списание сырья одной транзакцией. Продукция без даты
     * готовности (или если она уже наступила к [todayIso], `yyyy-MM-dd`) сразу попадает в учёт.
     */
    suspend fun insertRecycling(
        recycling: DomainRecycling,
        products: List<DomainRecyclingProduct>,
        ingredients: List<DomainWriteOffTable>,
        todayIso: String
    ): Long

    /** Завершает продукцию [productId] досрочно: она поступает датой [day].[month].[year]. */
    suspend fun completeRecyclingProduct(productId: Long, day: Int, month: Int, year: Int)

    /** Завершает всю продукцию, чья дата готовности наступила к [todayIso] (`yyyy-MM-dd`). */
    suspend fun completeDueRecycling(todayIso: String)

    /** Удаляет партию вместе со списанием сырья и всей полученной продукцией. */
    suspend fun deleteRecycling(id: Long)
}
