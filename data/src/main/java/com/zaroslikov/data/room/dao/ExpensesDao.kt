package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.add.TitleAndSuffixDto
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDto
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDto
import com.zaroslikov.data.room.dto.shared.CategoryPriceDto
import com.zaroslikov.data.room.dto.shared.TitleSuffixPriceDto
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesDao {

    @Query("SELECT * from expenses_table")
    fun getAllExpensesTableForExport(): Flow<List<ExpensesTable>>

    @Upsert
    suspend fun insertAllExpensesTable(data: List<ExpensesTable>)

    @Query("DELETE FROM expenses_table")
    suspend fun deleteAllExpensesTable()

    @Transaction
    suspend fun clearAndInsertExpensesTableForImport(expensesTable: List<ExpensesTable>) {
        deleteAllExpensesTable()
        insertAllExpensesTable(expensesTable)
    }

    @Query(
        "SELECT * FROM expenses_table " +
                "WHERE idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC"
    )
    fun getAllExpensesItems(id: Long): Flow<List<ExpensesTable>>

    @Query("SELECT * FROM expenses_table WHERE _id=:id")
    fun getItemExpenses(id: Long): Flow<ExpensesTable>

    @Query("SELECT * FROM expenses_table WHERE animal_count_id=:id")
    fun getItemExpensesIdAnimalCount(id: Long): Flow<ExpensesTable>

    @Query("SELECT * FROM expenses_table WHERE animal_vaccination_id=:id")
    fun getItemExpensesForVaccination(id: Long): Flow<ExpensesTable?>

    @Query(
        "SELECT title, SUM(count) AS count," +
                " SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END) AS price," +
                " count_suffix AS suffix," +
                " COUNT(*) AS row_count" +
                " FROM expenses_table" +
                " WHERE idPT=:id" +
                " GROUP BY title" +
                " ORDER BY price DESC"
    )
    fun getBrieflyItemExpenses(id: Long): Flow<List<BrieflyExpensesDto>>

    @Query("SELECT * FROM expenses_table WHERE idPT=:id AND title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemExpenses(id: Long, name: String): Flow<List<ExpensesTable>>

    @Query(
        "SELECT title, count_suffix AS suffix FROM expenses_table" +
                " WHERE idPT=:id AND animalId IS NULL AND animal_vaccination_id IS NULL AND animal_count_id IS NULL " +
                " GROUP BY title" +
                " ORDER BY MAX(_id) DESC"
    )
    fun getItemsTitleExpensesList(id: Long): Flow<List<TitleAndSuffixDto>>

    @Query("SELECT category FROM expenses_table WHERE idPT=:id AND category IS NOT NULL GROUP BY category")
    fun getItemsCategoryExpensesList(id: Long): Flow<List<String>>

    @Query(
        "SELECT " +
                " a.id," +
                " a.name AS name," +
                " a.type as type," +
                " a.food_day AS foodDay," +
                " a.food_day_suffix AS foodDaySuffix," +
                " t.calculatedCount AS countAnimal," +
                " case when e._id NOT NULL Then e._id  else 0 end as idExpensesAnimal," +
                " case when e.idAnimal NOT NULL Then 1 else 0 end as ps," +
                " case when  e.percentExpenses NOT NULL Then e.percentExpenses else 0 end as presentException " +
                " from animal_table a LEFT JOIN (" +
                "        SELECT" +
                "            animal_id," +
                "" +
                "            CASE" +
                "                WHEN MAX(version) IS NULL THEN" +
                "                    COALESCE(MAX(count), 0)" +
                "" +
                "                ELSE" +
                "                    COALESCE(SUM(" +
                "                        CASE" +
                "                            WHEN version IN (1,4,5) THEN count" +
                "                            WHEN version IN (0,2,3) THEN -count" +
                "                            ELSE 0" +
                "                        END" +
                "                    ), 0)" +
                "            END AS calculatedCount" +
                "" +
                "        FROM animal_count_table" +
                "        GROUP BY animal_id" +
                "" +
                "    ) t ON a.id = t.animal_id Left Join ExpensesAnimalTable e On e.idAnimal = a.id and e.idExpenses =:idExpenses Where a.idPT=:id ORDER By ps Desc"
    )
    fun getItemsAnimalExpensesList2(
        id: Long,
        idExpenses: Long
    ): Flow<List<AnimalExpensesDto>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertExpenses(item: ExpensesTable): Long

    @Update
    suspend fun updateExpenses(item: ExpensesTable)

    @Delete
    suspend fun deleteExpenses(item: ExpensesTable)

    @Query("DELETE FROM expenses_table WHERE _id = :id")
    suspend fun deleteExpensesById(id: Long)

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0) AS ResultCount" +
                " FROM expenses_table WHERE idPT=:id"
    )
    fun getExpenses(id: Long): Flow<Double>

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0)" +
                " FROM expenses_table s" +
                " INNER JOIN project_table p ON p.id = s.idPT" +
                " INNER JOIN settings_table st ON st.idPT = p.id" +
                " WHERE st.currency_suffix = :currencySuffix"
    )
    fun getExpensesAllProject(currencySuffix: Suffix): Flow<Double>

    @Query(
        "SELECT title," +
                " count_suffix as suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 1 AS category" +
                " FROM expenses_table" +
                " WHERE idPT =:id GROUP BY title ORDER BY price DESC"
    )
    fun getExpensesAllList(id: Long): Flow<List<TitleSuffixPriceDto>> //maybe

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM expenses_table" +
                " WHERE idPT =:id GROUP BY category ORDER BY price DESC"
    )
    fun getExpensesCategoryAllList(id: Long): Flow<List<CategoryPriceDto>>

    // Фигня пока не используется
    @Query(
        "SELECT COALESCE(SUM(count), 0) AS ResultCount FROM expenses_table" +
                " WHERE idPT =:id and month =:mount and year =:year"
    )//0 = price
    fun getExpensesMountFin(
        id: Long,
        mount: Int,
        year: Int
    ): Flow<Double>

    //Важно!
    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0) AS ResultCount" +
                " FROM expenses_table" +
                " WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getExpensesMount(id: Long, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM expenses_table" +
                " WHERE idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY category ORDER BY price DESC "
    )
    fun getCategoryExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CategoryPriceDto>> //maybe

    @Query(
        "SELECT title," +
                " count_suffix as suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 1 AS category" +
                " FROM expenses_table" +
                " WHERE idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY title ORDER BY price DESC"
    )
    fun getProductLisCategoryExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String,
    ): Flow<List<TitleSuffixPriceDto>> //maybe


    @Query("SELECT * FROM expenses_table WHERE idPT =:id AND is_show_food = 1 AND is_food = 1")
    fun getCurrentFoodWarehouse(id: Long): Flow<List<ExpensesTable>>

    @Query("UPDATE expenses_table SET is_show_food = 0 WHERE _id = :id")
    suspend fun updateFoodOnWriteOffWarehouse(id: Long)

    @Query(
        "SELECT COALESCE(SUM(price),0) AS price" +
                " FROM expenses_table" +
                " WHERE idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day))" +
                "BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )//0 = price
    fun getAnalysisExpensesNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    /* @Query(
         "SELECT title As buyer, COALESCE(SUM(price), 0) AS resultPrice,  COALESCE(SUM(count),0) AS resultCount, count_suffix as suffix" +
                 " from expenses_table Where idPT=:id and DATE(printf('%04d-%02d-%02d', year, month, day)) BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY buyer ORDER BY resultPrice DESC Limit 3"
     )
     fun getAnalysisExpensesProductNewYearProject(
         id: Long,
         dateBegin: String,
         dateEnd: String
     ): Flow<List<AnalysisSaleBuyerAllTime>> // TODO Byer*/

    /* @Query(
         "SELECT COALESCE(SUM(price),0) AS price" +
                 " FROM expenses_table" +
                 " WHERE DATE(printf('%04d-%02d-%02d', year, month, day))" +
                 " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
     )//0 = price
     fun getAnalysisExpensesNewYear(
         dateBegin: String,
         dateEnd: String
     ): Flow<Double>*/

    /*@Query("SELECT title As buyer," +
            " COALESCE(SUM(price), 0) AS resultPrice," +
            " COALESCE(SUM(count),0) AS resultCount," +
            " count_suffix as suffix" +
            " from expenses_table" +
            " Where DATE(printf('%04d-%02d-%02d', year, month, day))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
            " GROUP BY buyer ORDER BY resultPrice DESC Limit 3")
    fun getAnalysisExpensesProductNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<List<AnalysisSaleBuyerAllTime>> // TODO Byer*/

}

