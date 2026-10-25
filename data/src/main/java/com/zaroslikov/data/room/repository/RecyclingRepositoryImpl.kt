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
import java.time.LocalDate
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
        todayIso: String
    ): Long {
        val today = LocalDate.parse(todayIso)
        return recyclingDao.insertRecycling(
            recycling = recycling.toRoomMap(),
            products = products.map { product ->
                val readyDate = product.readyYear?.let { year ->
                    LocalDate.of(year, product.readyMonth ?: 1, product.readyDay ?: 1)
                }
                product.toRoomMap() to (readyDate == null || !readyDate.isAfter(today))
            },
            ingredients = ingredients.map { it.toRoomMap() }
        )
    }

    override suspend fun completeRecyclingProduct(productId: Long, day: Int, month: Int, year: Int) =
        recyclingDao.completeRecyclingProduct(productId, day, month, year)

    override suspend fun completeDueRecycling(todayIso: String) =
        recyclingDao.completeDueRecycling(todayIso)

    override suspend fun deleteRecycling(id: Long) = recyclingDao.deleteRecycling(id)
}
