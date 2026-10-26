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
     * готовности (или если момент готовности уже наступил к [nowIso], `yyyy-MM-dd'T'HH:mm`)
     * сразу попадает в учёт.
     */
    suspend fun insertRecycling(
        recycling: DomainRecycling,
        products: List<DomainRecyclingProduct>,
        ingredients: List<DomainWriteOffTable>,
        nowIso: String
    ): Long

    /**
     * Правит партию [recycling] одной транзакцией: дату, время и примечание, сырьё (заменяется целиком)
     * и продукцию (сверяется по id, у новой id = 0; убранная удаляется вместе со строкой в учёте).
     * Уже поступившая продукция остаётся в учёте с новыми данными; остальная — по правилу [insertRecycling].
     */
    suspend fun updateRecycling(
        recycling: DomainRecycling,
        products: List<DomainRecyclingProduct>,
        ingredients: List<DomainWriteOffTable>,
        nowIso: String
    )

    /**
     * Завершает продукцию [productId] досрочно: она поступает датой [day].[month].[year],
     * временем готовности становится [hour]:[minute].
     * Возвращает true, если продукция завершена именно этим вызовом (false — уже была завершена).
     */
    suspend fun completeRecyclingProduct(
        productId: Long,
        day: Int,
        month: Int,
        year: Int,
        hour: Int?,
        minute: Int?
    ): Boolean

    /** Завершает всю продукцию, чей момент готовности наступил к [nowIso] (`yyyy-MM-dd'T'HH:mm`). */
    suspend fun completeDueRecycling(nowIso: String)

    /**
     * Переработка не удалась: сырьё партии [id] списывается в утиль обычными записями
     * (пустое примечание — [note]), неготовая продукция отменяется. Готовая не трогается.
     * Возвращает списанное сырьё (пусто — нечего было списывать).
     */
    suspend fun disposeRecycling(id: Long, note: String): List<DomainWriteOffTable>

    /** Удаляет партию вместе со списанием сырья и всей полученной продукцией. */
    suspend fun deleteRecycling(id: Long)
}
