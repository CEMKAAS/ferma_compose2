package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.add.TitleAndSuffixDto
import com.zaroslikov.data.room.dto.animal.AnimalExpensesDto
import com.zaroslikov.data.room.dto.expenses.BrieflyExpensesDto
import com.zaroslikov.data.room.dto.shared.CategoryPriceDto
import com.zaroslikov.data.room.dto.shared.TitleSuffixPriceDto
import com.zaroslikov.data.room.table.ferma.ExpensesTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpensesDao {
    @Query(
        "SELECT * FROM expenses_table " +
                "Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC"
    )
    fun getAllExpensesItems(id: Long): Flow<List<ExpensesTable>>

    @Query("SELECT * FROM expenses_table WHERE _id=:id")
    fun getItemExpenses(id: Long): Flow<ExpensesTable>

    @Query("SELECT * FROM expenses_table WHERE animal_count_id=:id")
    fun getItemExpensesIdAnimalCount(id: Long): Flow<ExpensesTable>

    @Query("SELECT * FROM expenses_table WHERE animal_vaccination_id=:id")
    fun getItemExpensesForVaccination(id: Long): Flow<ExpensesTable>

    @Query(
        "SELECT title, SUM(count) AS count," +
                " SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END) AS price," +
                " count_suffix AS suffix" +
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

    @Query("SELECT category FROM expenses_table WHERE idPT=:id GROUP BY category")
    fun getItemsCategoryExpensesList(id: Long): Flow<List<String>>


    @Query(
        "SELECT a.id, a.name as name, a.foodDay as foodDay, a.food_day_suffix as foodDaySuffix, t.count as countAnimal," +
                " case when e._id NOT NULL Then e._id  else 0 end as idExpensesAnimal," +
                " case when e.idAnimal NOT NULL  Then 1 else 0 end as ps," +
                " case when  e.percentExpenses NOT NULL Then e.percentExpenses else 0 end as presentException " +
                " from animal_table a JOIN (" +
                "    SELECT idAnimal, count" +
                "    FROM animalcounttable" +
                "    WHERE id IN (" +
                "        SELECT MAX(id)" +
                "        FROM animalcounttable " +
                "    GROUP by idAnimal)" +
                ") t ON a.id = t.idAnimal Left Join ExpensesAnimalTable e On e.idAnimal = a.id and e.idExpenses =:idExpenses Where a.idPT=:id ORDER By ps Desc"
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

    @Query("SELECT COALESCE(SUM(count), 0) AS ResultCount FROM expenses_table WHERE idPT=:id")
    fun getExpenses(id: Long): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(count), 0) AS ResultCount FROM expenses_table" +
                " WHERE idPT =:id and month =:mount and year =:year"
    )//0 = price
    fun getExpensesMountFin(
        id: Long,
        mount: Int,
        year: Int
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(count), 0) AS ResultCount" +
                " FROM expenses_table" +
                " WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )//0 = price
    fun getExpensesMount(id: Long, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query(
        "SELECT title, count_suffix as suffix, COALESCE(SUM(price), 0.0) AS price" +
                " FROM expenses_table WHERE idPT =:id GROUP BY title"
    )
    fun getExpensesAllList(id: Long): Flow<List<TitleSuffixPriceDto>> //maybe

    @Query(
        "SELECT category, COALESCE(SUM(price), 0.0) AS price FROM expenses_table" +
                " WHERE idPT =:id group by category"
    )//0 = price
    fun getExpensesCategoryAllList(id: Long): Flow<List<CategoryPriceDto>>

    @Query(
        "SELECT category, COALESCE(SUM(price), 0.0) AS price" +
                " FROM expenses_table" +
                " Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) GROUP BY category "
    )//0 = price
//    ORDER BY countEXPENSES DESC
    fun getCategoryExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CategoryPriceDto>> //maybe

    @Query(
        "SELECT title, count_suffix as suffix, COALESCE(SUM(count), 0.0) AS price FROM expenses_table" +
                " Where idPT=:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and category=:category" +
                " GROUP BY title ORDER BY count DESC"
    )
    fun getProductLisCategoryExpensesCurrentMonth(
        id: Long,
        dateBegin: String,
        dateEnd: String,
        category: String
    ): Flow<List<TitleSuffixPriceDto>> //maybe


    @Query("SELECT * FROM expenses_table WHERE idPT =:id AND is_show_food = 1")
    fun getCurrentFoodWarehouse(id: Long): Flow<List<ExpensesTable>>

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

