package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.ferma.AddTable
import com.zaroslikov.data.room.table.ferma.RecyclingTable
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow

@Dao
interface RecyclingDao {

    @Query("SELECT * FROM recycling_table")
    fun getAllRecyclingForExport(): Flow<List<RecyclingTable>>

    @Upsert
    suspend fun insertAllRecycling(data: List<RecyclingTable>)

    @Query("DELETE FROM recycling_table")
    suspend fun deleteAllRecycling()

    @Transaction
    suspend fun clearAndInsertRecyclingForImport(data: List<RecyclingTable>) {
        deleteAllRecycling()
        insertAllRecycling(data)
    }

    @Query(
        "SELECT * FROM recycling_table WHERE idPT = :idPT" +
                " ORDER BY is_completed ASC," +
                " DATE(printf('%04d-%02d-%02d', COALESCE(ready_year, year), COALESCE(ready_month, month), COALESCE(ready_day, day))) ASC," +
                " _id DESC"
    )
    fun getRecyclingList(idPT: Long): Flow<List<RecyclingTable>>

    @Query("SELECT * FROM recycling_table WHERE _id = :id")
    suspend fun getRecyclingOnce(id: Long): RecyclingTable?

    /** Доска продукции: партии, в которых продукт `title` был сырьём. */
    @Query(
        "SELECT * FROM recycling_table WHERE idPT = :idPT AND _id IN (" +
                " SELECT recycling_id FROM write_off_table" +
                " WHERE idPT = :idPT AND title = :title AND recycling_id IS NOT NULL" +
                " AND animal_count_id IS NULL AND product_origin = 0)" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) ASC, _id ASC"
    )
    fun getRecyclingUsingProduct(idPT: Long, title: String): Flow<List<RecyclingTable>>

    /** Доска продукции: партии, результат которых — продукт `title`. */
    @Query(
        "SELECT * FROM recycling_table WHERE idPT = :idPT AND title = :title" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) ASC, _id ASC"
    )
    fun getRecyclingProducingProduct(idPT: Long, title: String): Flow<List<RecyclingTable>>

    /** Доска продукции: сырьё партий, результат которых — продукт `title`. */
    @Query(
        "SELECT * FROM write_off_table WHERE recycling_id IN (" +
                " SELECT _id FROM recycling_table WHERE idPT = :idPT AND title = :title)" +
                " ORDER BY recycling_id ASC, _id ASC"
    )
    fun getIngredientsForProduct(idPT: Long, title: String): Flow<List<WriteOffTable>>

    @Query(
        "SELECT _id FROM recycling_table" +
                " WHERE is_completed = 0 AND ready_year IS NOT NULL" +
                " AND DATE(printf('%04d-%02d-%02d', ready_year, ready_month, ready_day)) <= DATE(:todayIso)"
    )
    suspend fun getDueRecyclingIds(todayIso: String): List<Long>

    @Query("SELECT currency_suffix FROM settings_table WHERE idPT = :idPT LIMIT 1")
    suspend fun getCurrencySuffix(idPT: Long): Suffix?

    @Insert
    suspend fun insertRecyclingRow(item: RecyclingTable): Long

    @Update
    suspend fun updateRecyclingRow(item: RecyclingTable)

    @Insert
    suspend fun insertIngredients(items: List<WriteOffTable>)

    @Insert
    suspend fun insertProduct(item: AddTable): Long

    @Query("DELETE FROM add_table WHERE _id = :addId AND idPT = :idPT")
    suspend fun deleteProduct(addId: Long, idPT: Long)

    @Query("DELETE FROM write_off_table WHERE recycling_id = :recyclingId")
    suspend fun deleteIngredients(recyclingId: Long)

    @Query("DELETE FROM recycling_table WHERE _id = :id")
    suspend fun deleteRecyclingRow(id: Long)

    @Transaction
    suspend fun insertRecycling(
        recycling: RecyclingTable,
        ingredients: List<WriteOffTable>,
        completeNow: Boolean
    ): Long {
        val id = insertRecyclingRow(recycling.copy(id = 0))
        insertIngredients(ingredients.map { it.copy(id = 0, recyclingId = id, idPT = recycling.idPT) })
        if (completeNow)
            completeRecycling(
                id,
                recycling.readyDay ?: recycling.day,
                recycling.readyMonth ?: recycling.month,
                recycling.readyYear ?: recycling.year
            )
        return id
    }

    /** Продукция поступает в add_table датой готовности; повторный вызов ничего не делает. */
    @Transaction
    suspend fun completeRecycling(id: Long, day: Int, month: Int, year: Int) {
        val recycling = getRecyclingOnce(id) ?: return
        if (recycling.isCompleted) return
        val addId = insertProduct(
            AddTable(
                title = recycling.title,
                count = recycling.count,
                day = day,
                month = month,
                year = year,
                price = 0.0,
                countSuffix = recycling.countSuffix,
                priceSuffix = getCurrencySuffix(recycling.idPT) ?: Suffix.RUBLE,
                category = recycling.category,
                animalId = null,
                note = recycling.note,
                idPT = recycling.idPT
            )
        )
        updateRecyclingRow(
            recycling.copy(
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
        getDueRecyclingIds(todayIso).forEach { id ->
            val recycling = getRecyclingOnce(id) ?: return@forEach
            completeRecycling(
                id,
                recycling.readyDay ?: recycling.day,
                recycling.readyMonth ?: recycling.month,
                recycling.readyYear ?: recycling.year
            )
        }
    }

    @Transaction
    suspend fun deleteRecycling(id: Long) {
        val recycling = getRecyclingOnce(id) ?: return
        recycling.addId?.let { deleteProduct(it, recycling.idPT) }
        deleteIngredients(id)
        deleteRecyclingRow(id)
    }
}
