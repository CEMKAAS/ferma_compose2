package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.RecyclingProductTable
import com.zaroslikov.data.room.table.ferma.RecyclingTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow

@Dao
interface RecyclingDao {

    @Query("SELECT * FROM recycling_table")
    fun getAllRecyclingForExport(): Flow<List<RecyclingTable>>

    @Query("SELECT * FROM recycling_product_table")
    fun getAllRecyclingProductsForExport(): Flow<List<RecyclingProductTable>>

    @Upsert
    suspend fun insertAllRecycling(data: List<RecyclingTable>)

    @Upsert
    suspend fun insertAllRecyclingProducts(data: List<RecyclingProductTable>)

    @Query("DELETE FROM recycling_table")
    suspend fun deleteAllRecycling()

    @Query("DELETE FROM recycling_product_table")
    suspend fun deleteAllRecyclingProducts()

    @Transaction
    suspend fun clearAndInsertRecyclingForImport(
        data: List<RecyclingTable>,
        products: List<RecyclingProductTable>
    ) {
        deleteAllRecyclingProducts()
        deleteAllRecycling()
        insertAllRecycling(data)
        insertAllRecyclingProducts(products)
    }

    @Query(
        "SELECT * FROM recycling_table WHERE idPT = :idPT" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC, _id DESC"
    )
    fun getRecyclingList(idPT: Long): Flow<List<RecyclingTable>>

    @Query(
        "SELECT * FROM recycling_product_table WHERE idPT = :idPT" +
                " ORDER BY is_completed ASC," +
                " DATE(printf('%04d-%02d-%02d', ready_year, ready_month, ready_day)) ASC," +
                " _id ASC"
    )
    fun getRecyclingProductList(idPT: Long): Flow<List<RecyclingProductTable>>

    @Query("SELECT * FROM write_off_table WHERE idPT = :idPT AND recycling_id IS NOT NULL")
    fun getIngredientList(idPT: Long): Flow<List<WriteOffTable>>

    @Query("SELECT * FROM recycling_table WHERE _id = :id")
    suspend fun getRecyclingOnce(id: Long): RecyclingTable?

    @Query("SELECT * FROM recycling_product_table WHERE _id = :id")
    suspend fun getRecyclingProductOnce(id: Long): RecyclingProductTable?

    @Query("SELECT * FROM recycling_product_table WHERE recycling_id = :recyclingId")
    suspend fun getRecyclingProducts(recyclingId: Long): List<RecyclingProductTable>

    @Query(
        "SELECT _id FROM recycling_product_table" +
                " WHERE is_completed = 0 AND ready_year IS NOT NULL" +
                " AND DATE(printf('%04d-%02d-%02d', ready_year, ready_month, ready_day)) <= DATE(:todayIso)"
    )
    suspend fun getDueRecyclingProductIds(todayIso: String): List<Long>

    @Query("SELECT currency_suffix FROM settings_table WHERE idPT = :idPT LIMIT 1")
    suspend fun getCurrencySuffix(idPT: Long): Suffix?

    @Insert
    suspend fun insertRecyclingRow(item: RecyclingTable): Long

    @Insert
    suspend fun insertRecyclingProductRow(item: RecyclingProductTable): Long

    @Update
    suspend fun updateRecyclingProductRow(item: RecyclingProductTable)

    @Insert
    suspend fun insertIngredients(items: List<WriteOffTable>)

    @Insert
    suspend fun insertProduct(item: AddTable): Long

    @Query("DELETE FROM add_table WHERE _id = :addId AND idPT = :idPT")
    suspend fun deleteProduct(addId: Long, idPT: Long)

    @Query("DELETE FROM write_off_table WHERE recycling_id = :recyclingId")
    suspend fun deleteIngredients(recyclingId: Long)

    @Query("DELETE FROM recycling_product_table WHERE recycling_id = :recyclingId")
    suspend fun deleteRecyclingProducts(recyclingId: Long)

    @Query("DELETE FROM recycling_table WHERE _id = :id")
    suspend fun deleteRecyclingRow(id: Long)

    /** [products] — продукция и признак «поступает в учёт сразу». */
    @Transaction
    suspend fun insertRecycling(
        recycling: RecyclingTable,
        products: List<Pair<RecyclingProductTable, Boolean>>,
        ingredients: List<WriteOffTable>
    ): Long {
        val id = insertRecyclingRow(recycling.copy(id = 0))
        insertIngredients(ingredients.map { it.copy(id = 0, recyclingId = id, idPT = recycling.idPT) })
        products.forEach { (product, completeNow) ->
            val productId = insertRecyclingProductRow(
                product.copy(
                    id = 0,
                    recyclingId = id,
                    isCompleted = false,
                    addId = null,
                    idPT = recycling.idPT
                )
            )
            if (completeNow)
                completeRecyclingProduct(
                    productId,
                    product.readyDay ?: recycling.day,
                    product.readyMonth ?: recycling.month,
                    product.readyYear ?: recycling.year
                )
        }
        return id
    }

    /** Продукция поступает в add_table датой готовности; повторный вызов ничего не делает. */
    @Transaction
    suspend fun completeRecyclingProduct(productId: Long, day: Int, month: Int, year: Int) {
        val product = getRecyclingProductOnce(productId) ?: return
        if (product.isCompleted) return
        val recycling = getRecyclingOnce(product.recyclingId) ?: return
        val addId = insertProduct(
            AddTable(
                title = product.title,
                count = product.count,
                day = day,
                month = month,
                year = year,
                price = 0.0,
                countSuffix = product.countSuffix,
                priceSuffix = getCurrencySuffix(product.idPT) ?: Suffix.RUBLE,
                category = product.category,
                animalId = null,
                note = recycling.note,
                idPT = product.idPT
            )
        )
        updateRecyclingProductRow(
            product.copy(
                isCompleted = true,
                addId = addId,
                readyDay = day,
                readyMonth = month,
                readyYear = year
            )
        )
    }

    @Transaction
    suspend fun completeDueRecycling(todayIso: String) {
        getDueRecyclingProductIds(todayIso).forEach { id ->
            val product = getRecyclingProductOnce(id) ?: return@forEach
            completeRecyclingProduct(
                id,
                product.readyDay ?: return@forEach,
                product.readyMonth ?: return@forEach,
                product.readyYear ?: return@forEach
            )
        }
    }

    @Transaction
    suspend fun deleteRecycling(id: Long) {
        val recycling = getRecyclingOnce(id) ?: return
        getRecyclingProducts(id).forEach { product ->
            product.addId?.let { deleteProduct(it, recycling.idPT) }
        }
        deleteIngredients(id)
        deleteRecyclingProducts(id)
        deleteRecyclingRow(id)
    }
}
