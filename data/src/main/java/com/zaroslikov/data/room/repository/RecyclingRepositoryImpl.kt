package com.zaroslikov.data.room.repository

import com.zaroslikov.data.room.dao.RecyclingDao
import com.zaroslikov.data.room.mapper.table.toDomainMap
import com.zaroslikov.data.room.mapper.table.toRoomMap
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
import com.zaroslikov.domain.models.table.DomainRecyclingWithProducts
import com.zaroslikov.domain.models.table.DomainWriteOffTable
import com.zaroslikov.domain.repository.RecyclingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class RecyclingRepositoryImpl @Inject constructor(
    private val recyclingDao: RecyclingDao
) : RecyclingRepository {

    override fun getAllRecyclingForExport(): Flow<List<DomainRecycling>> =
        recyclingDao.getAllRecyclingForExport().map { list -> list.map { it.toDomainMap() } }

    override fun getAllRecyclingProductsForExport(): Flow<List<DomainRecyclingProduct>> =
        recyclingDao.getAllRecyclingProductsForExport().map { list -> list.map { it.toDomainMap() } }

    override suspend fun clearAndInsertRecyclingForImport(
        list: List<DomainRecycling>,
        products: List<DomainRecyclingProduct>
    ) = recyclingDao.clearAndInsertRecyclingForImport(
        list.map { it.toRoomMap() },
        products.map { it.toRoomMap() }
    )

    override fun getRecyclingList(idPT: Long): Flow<List<DomainRecyclingWithProducts>> =
        combine(
            recyclingDao.getRecyclingList(idPT),
            recyclingDao.getRecyclingProductList(idPT),
            recyclingDao.getIngredientList(idPT)
        ) { recyclingList, productList, ingredientList ->
            val products = productList.groupBy { it.recyclingId }
            val ingredients = ingredientList.groupBy { it.recyclingId }
            recyclingList.map { recycling ->
                DomainRecyclingWithProducts(
                    recycling = recycling.toDomainMap(),
                    products = products[recycling.id].orEmpty().map { it.toDomainMap() },
                    ingredients = ingredients[recycling.id].orEmpty().map { it.toDomainMap() }
                )
            }
        }

    override suspend fun insertRecycling(
        recycling: DomainRecycling,
        products: List<DomainRecyclingProduct>,
        ingredients: List<DomainWriteOffTable>,
        nowIso: String
    ): Long = recyclingDao.insertRecycling(
        recycling = recycling.toRoomMap(),
        products = products.withCompleteNow(nowIso),
        ingredients = ingredients.map { it.toRoomMap() }
    )

    override suspend fun updateRecycling(
        recycling: DomainRecycling,
        products: List<DomainRecyclingProduct>,
        ingredients: List<DomainWriteOffTable>,
        nowIso: String
    ) = recyclingDao.updateRecycling(
        recycling = recycling.toRoomMap(),
        products = products.withCompleteNow(nowIso),
        ingredients = ingredients.map { it.toRoomMap() }
    )

    /** Продукция без даты готовности или с уже наступившим моментом поступает в учёт сразу. */
    private fun List<DomainRecyclingProduct>.withCompleteNow(nowIso: String) =
        LocalDateTime.parse(nowIso).let { now ->
            map { product ->
                val ready = product.readyYear?.let { year ->
                    LocalDateTime.of(
                        year, product.readyMonth ?: 1, product.readyDay ?: 1,
                        product.readyHour ?: 0, product.readyMinute ?: 0
                    )
                }
                product.toRoomMap() to (ready == null || !ready.isAfter(now))
            }
        }

    override suspend fun completeRecyclingProduct(
        productId: Long,
        day: Int,
        month: Int,
        year: Int,
        hour: Int?,
        minute: Int?
    ): Boolean = recyclingDao.completeRecyclingProduct(productId, day, month, year, hour, minute)

    override suspend fun completeDueRecycling(nowIso: String) =
        recyclingDao.completeDueRecycling(nowIso)

    override suspend fun disposeRecycling(id: Long, note: String): List<DomainWriteOffTable> =
        recyclingDao.disposeRecycling(id, note).map { it.toDomainMap() }

    override suspend fun deleteRecycling(id: Long) = recyclingDao.deleteRecycling(id)

    override suspend fun deleteRecycledProduct(addId: Long, note: String): List<DomainWriteOffTable> =
        recyclingDao.deleteRecycledProduct(addId, note).map { it.toDomainMap() }

    override suspend fun isRecycledProduct(addId: Long): Boolean =
        recyclingDao.getRecyclingProductByAddId(addId) != null
}
