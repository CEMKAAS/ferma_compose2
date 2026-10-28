package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.shared.TitleCountSuffixDto
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
                " printf('%04d-%02d-%02dT%02d:%02d', ready_year, ready_month, ready_day," +
                " COALESCE(ready_hour, 0), COALESCE(ready_minute, 0)) ASC," +
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
                " AND printf('%04d-%02d-%02dT%02d:%02d', ready_year, ready_month, ready_day," +
                " COALESCE(ready_hour, 0), COALESCE(ready_minute, 0)) <= :nowIso"
    )
    suspend fun getDueRecyclingProductIds(nowIso: String): List<Long>

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

    @Update
    suspend fun updateRecyclingRow(item: RecyclingTable)

    @Query("DELETE FROM recycling_product_table WHERE _id = :id")
    suspend fun deleteRecyclingProductRow(id: Long)

    @Insert
    suspend fun insertProduct(item: AddTable): Long

    @Query("SELECT * FROM add_table WHERE _id = :addId AND idPT = :idPT")
    suspend fun getProductOnce(addId: Long, idPT: Long): AddTable?

    @Update
    suspend fun updateProduct(item: AddTable)

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
                    product.readyYear ?: recycling.year,
                    product.readyHour,
                    product.readyMinute
                )
        }
        return id
    }

    /**
     * Правка партии: строка партии, сырьё заменяется целиком, продукция сверяется по id.
     * [products] — продукция (id = 0 у новой) и признак «поступает в учёт сразу».
     * Убранная продукция удаляется вместе со своей строкой add_table. Уже поступившая остаётся
     * поступившей, если срок по-прежнему не задан или наступил, — её строка add_table получает новые
     * название, количество, единицу и дату готовности; если же ей задали будущий срок, она уходит
     * со склада обратно в переработку. Остальная (и новая) поступает сразу или ждёт по [products].
     */
    @Transaction
    suspend fun updateRecycling(
        recycling: RecyclingTable,
        products: List<Pair<RecyclingProductTable, Boolean>>,
        ingredients: List<WriteOffTable>
    ) {
        val old = getRecyclingOnce(recycling.id) ?: return
        val idPT = old.idPT
        updateRecyclingRow(recycling.copy(idPT = idPT))
        deleteIngredients(old.id)
        insertIngredients(ingredients.map { it.copy(id = 0, recyclingId = old.id, idPT = idPT) })

        val existing = getRecyclingProducts(old.id).associateBy { it.id }
        val keptIds = products.map { it.first.id }.toSet()
        existing.values.filter { it.id !in keptIds }.forEach { product ->
            product.addId?.let { deleteProduct(it, idPT) }
            deleteRecyclingProductRow(product.id)
        }

        products.forEach { (product, completeNow) ->
            val current = existing[product.id]
            val readyDay = product.readyDay ?: recycling.day
            val readyMonth = product.readyMonth ?: recycling.month
            val readyYear = product.readyYear ?: recycling.year
            if (current != null && current.isCompleted && completeNow) {
                updateRecyclingProductRow(
                    product.copy(
                        recyclingId = old.id,
                        category = current.category,
                        readyDay = readyDay,
                        readyMonth = readyMonth,
                        readyYear = readyYear,
                        isCompleted = true,
                        addId = current.addId,
                        idPT = idPT
                    )
                )
                current.addId?.let { getProductOnce(it, idPT) }?.let { add ->
                    updateProduct(
                        add.copy(
                            title = product.title,
                            count = product.count,
                            countSuffix = product.countSuffix,
                            day = readyDay,
                            month = readyMonth,
                            year = readyYear,
                            note = recycling.note
                        )
                    )
                }
                return@forEach
            }
            // Поступившая продукция с новым будущим сроком снова ждёт: убираем её со склада
            if (current?.isCompleted == true) current.addId?.let { deleteProduct(it, idPT) }
            val pending = product.copy(
                recyclingId = old.id,
                category = current?.category ?: product.category,
                isCompleted = false,
                addId = null,
                idPT = idPT
            )
            val productId = if (current != null) {
                updateRecyclingProductRow(pending)
                current.id
            } else insertRecyclingProductRow(pending.copy(id = 0))
            if (completeNow)
                completeRecyclingProduct(
                    productId, readyDay, readyMonth, readyYear, product.readyHour, product.readyMinute
                )
        }
    }

    /**
     * Продукция поступает в add_table датой готовности; повторный вызов ничего не делает.
     * [hour]/[minute] — время готовности (null — начало дня).
     * Возвращает true, если продукция завершена именно этим вызовом.
     */
    @Transaction
    suspend fun completeRecyclingProduct(
        productId: Long,
        day: Int,
        month: Int,
        year: Int,
        hour: Int?,
        minute: Int?
    ): Boolean {
        val product = getRecyclingProductOnce(productId) ?: return false
        if (product.isCompleted) return false
        val recycling = getRecyclingOnce(product.recyclingId) ?: return false
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
                readyYear = year,
                readyHour = hour,
                readyMinute = minute
            )
        )
        return true
    }

    @Transaction
    suspend fun completeDueRecycling(nowIso: String) {
        getDueRecyclingProductIds(nowIso).forEach { id ->
            val product = getRecyclingProductOnce(id) ?: return@forEach
            completeRecyclingProduct(
                id,
                product.readyDay ?: return@forEach,
                product.readyMonth ?: return@forEach,
                product.readyYear ?: return@forEach,
                product.readyHour,
                product.readyMinute
            )
        }
    }

    @Query("SELECT * FROM write_off_table WHERE recycling_id = :recyclingId")
    suspend fun getIngredients(recyclingId: Long): List<WriteOffTable>

    /** Сырьё партии становится обычным списанием в утиль; пустое примечание — [note]. */
    @Query(
        "UPDATE write_off_table SET recycling_id = NULL, status = 0," +
                " note = CASE WHEN TRIM(note) = '' THEN :note ELSE note END" +
                " WHERE recycling_id = :recyclingId"
    )
    suspend fun disposeIngredients(recyclingId: Long, note: String)

    @Query("DELETE FROM recycling_product_table WHERE recycling_id = :recyclingId AND is_completed = 0")
    suspend fun deleteIncompleteProducts(recyclingId: Long)

    /**
     * Переработка не удалась: сырьё партии списывается в утиль обычными записями (выходит из
     * переработки), неготовая продукция отменяется — на склад она не поступит. Готовая продукция
     * остаётся; если её нет, партия удаляется. Возвращает списанное сырьё (пусто — нечего списывать).
     */
    @Transaction
    suspend fun disposeRecycling(id: Long, note: String): List<WriteOffTable> {
        if (getRecyclingProducts(id).none { !it.isCompleted }) return emptyList()
        val ingredients = getIngredients(id)
        disposeIngredients(id, note)
        deleteIncompleteProducts(id)
        if (getRecyclingProducts(id).isEmpty()) deleteRecyclingRow(id)
        return ingredients
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

    /**
     * Готовая продукция, полученная переработкой сырья [name], за период по дате переработки:
     * сумма по названию и единице. Партия с несколькими видами сырья учитывается у каждого.
     */
    @Query(
        "SELECT p.title AS title, SUM(p.count) AS count, p.count_suffix AS suffix" +
                " FROM recycling_product_table p" +
                " JOIN recycling_table r ON r._id = p.recycling_id" +
                " WHERE p.idPT = :idPT AND p.is_completed = 1" +
                " AND EXISTS (SELECT 1 FROM write_off_table w" +
                " WHERE w.recycling_id = r._id AND w.title = :name)" +
                " AND DATE(printf('%04d-%02d-%02d', r.year, r.month, r.day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY p.title, p.count_suffix" +
                " ORDER BY count DESC"
    )
    fun getAnalysisRecyclingProducts(
        idPT: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<TitleCountSuffixDto>>

    @Query("SELECT * FROM recycling_product_table WHERE add_id = :addId LIMIT 1")
    suspend fun getRecyclingProductByAddId(addId: Long): RecyclingProductTable?

    /**
     * Удаление готовой продукции из «Продукции». Единственный товар партии — сырьё уходит
     * в утиль обычными списаниями, партия удаляется; товаров несколько — удаляется только этот.
     */
    @Transaction
    suspend fun deleteRecycledProduct(addId: Long, note: String): List<WriteOffTable> {
        val product = getRecyclingProductByAddId(addId) ?: return emptyList()
        return deleteRecyclingProduct(product.id, note)
    }

    /**
     * Удаляет товар партии [productId] (готовый — вместе со строкой в учёте). Единственный товар
     * партии — сырьё уходит в утиль обычными списаниями, партия удаляется.
     */
    @Transaction
    suspend fun deleteRecyclingProduct(productId: Long, note: String): List<WriteOffTable> {
        val product = getRecyclingProductOnce(productId) ?: return emptyList()
        product.addId?.let { deleteProduct(it, product.idPT) }
        deleteRecyclingProductRow(product.id)
        if (getRecyclingProducts(product.recyclingId).isNotEmpty()) return emptyList()
        val ingredients = getIngredients(product.recyclingId)
        disposeIngredients(product.recyclingId, note)
        deleteRecyclingRow(product.recyclingId)
        return ingredients
    }
}
