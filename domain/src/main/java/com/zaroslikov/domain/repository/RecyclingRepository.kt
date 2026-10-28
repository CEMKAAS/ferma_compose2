package com.zaroslikov.domain.repository

import com.zaroslikov.domain.models.dto.shared.DomainTitleCountSuffix
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

    /**
     * Готовая продукция, полученная переработкой сырья [name] за период (`yyyy-MM-dd`, по дате
     * переработки), — сумма по названию и единице, для анализа продукции.
     */
    fun getAnalysisRecyclingProducts(
        idPT: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<DomainTitleCountSuffix>>

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

    /**
     * Удаляет готовую продукцию переработки по строке склада [addId]. Если это был единственный
     * товар партии, её сырьё становится обычным списанием в утиль (пустое примечание — [note]),
     * а партия удаляется; иначе удаляется только этот товар. Возвращает списанное в утиль сырьё
     * (пусто — сырьё не трогалось или товар не из переработки).
     */
    suspend fun deleteRecycledProduct(addId: Long, note: String): List<DomainWriteOffTable>

    /**
     * Удаляет товар переработки [productId] (в том числе ещё не готовый) — правило то же, что
     * у [deleteRecycledProduct]: последний товар партии уводит её сырьё в утиль.
     */
    suspend fun deleteRecyclingProduct(productId: Long, note: String): List<DomainWriteOffTable>

    /** Получен ли товар склада [addId] переработкой. */
    suspend fun isRecycledProduct(addId: Long): Boolean
}
