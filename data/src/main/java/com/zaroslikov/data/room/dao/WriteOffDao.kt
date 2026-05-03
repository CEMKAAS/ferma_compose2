package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.sale.CountSuffixPriceDateDto
import com.zaroslikov.data.room.dto.shared.CategoryPriceDto
import com.zaroslikov.data.room.dto.shared.CountSuffixDto
import com.zaroslikov.data.room.dto.shared.TitleSuffixCategoryDto
import com.zaroslikov.data.room.dto.shared.TitleSuffixPriceDto
import com.zaroslikov.data.room.dto.write_off.BrieflyWriteOffDto
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow

@Dao
interface WriteOffDao {

    @Query("SELECT * from write_off_table")
    fun getAllWriteOffTableForExport(): Flow<List<WriteOffTable>>

    @Upsert
    suspend fun insertAllWriteOffTable(data: List<WriteOffTable>)

    @Query("DELETE FROM write_off_table")
    suspend fun deleteAllWriteOffTable()

    @Transaction
    suspend fun clearAndInsertWriteOffTableForImport(writeOffTables: List<WriteOffTable>) {
        deleteAllWriteOffTable()
        insertAllWriteOffTable(writeOffTables)
    }

    @Query("SELECT * from write_off_table Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC")
    fun getAllWriteOffItems(id: Long): Flow<List<WriteOffTable>>

    @Query("SELECT * from write_off_table Where _id=:id ")
    fun getItemWriteOff(id: Long): Flow<WriteOffTable>

    @Query("SELECT * from write_off_table Where animal_count_id=:id ")
    fun getItemWriteOffIdAnimalCount(id: Long): Flow<WriteOffTable>

    @Query(
        "SELECT title," +
                " SUM(count) AS count," +
                " count_suffix AS suffix," +
                " SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END) AS price," +
                " COUNT(*) AS row_count" +
                " FROM write_off_table" +
                " WHERE idPT=:id GROUP BY title ORDER BY count DESC"
    )
    fun getBrieflyItemWriteOff(id: Long): Flow<List<BrieflyWriteOffDto>>

    @Query("SELECT * from write_off_table Where idPT=:id and title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemWriteOff(id: Long, name: String): Flow<List<WriteOffTable>>

    @Query(
        "SELECT * FROM write_off_table" +
                " WHERE idPT=:id AND title =:name AND status = 0 AND price IS NOT NULL" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC"
    )
    fun getBrieflyDetailsItemWriteOffOwnNeed(id: Long, name: String): Flow<List<WriteOffTable>>

    @Query(
        "SELECT * FROM write_off_table" +
                " WHERE idPT=:id AND title =:name AND status = 1 AND price IS NOT NULL" +
                " ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC"
    )
    fun getBrieflyDetailsItemWriteOffScrap(id: Long, name: String): Flow<List<WriteOffTable>>

    /*   @Query(
           "SELECT title, count_suffix AS suffix, 0 AS category from add_table Where idPT=:id group by title, count_suffix" +
                   " UNION ALL" +
                   " SELECT title,  count_suffix AS suffix, 1 AS category from expenses_table Where idPT=:id group by title, count_suffix"
       )
       fun getItemsWriteOffList(id: Long): Flow<List<TitleSuffixCategoryDto>>*/
    @Query(
        "SELECT DISTINCT a.title, a.count_suffix AS suffix, 0 AS category FROM add_table a WHERE a.idPT=:id  " +

                " UNION ALL " +

                " SELECT DISTINCT e.title, e.count_suffix AS suffix, 1 AS category " +
                " FROM expenses_table e" +
                " WHERE e.idPT=:id and e.is_food = 0 and e.animalId IS NULL and e.animal_vaccination_id IS NULL and e.animal_count_id IS NULL "
    )
    fun getItemsWriteOffList(id: Long): Flow<List<TitleSuffixCategoryDto>>

    @Query("SELECT category from write_off_table Where idPT=:id group by category")
    fun getItemsCategoryWriteOffList(id: Long): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertWriteOff(item: WriteOffTable)

    @Update
    suspend fun updateWriteOff(item: WriteOffTable)

    @Query("DELETE FROM write_off_table WHERE _id = :id")
    suspend fun deleteWriteOff(id: Long)

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND status= 0 AND price IS NOT NULL"
    )
    fun getOwnNeed(id: Long): Flow<Double> //Write off Maybe

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM write_off_table s" +
                " INNER JOIN project_table p ON p.id = s.idPT" +
                " INNER JOIN settings_table st ON st.idPT = p.id" +
                " WHERE st.currency_suffix = :currencySuffix"
    )
    fun getOwnNeedAllProject(currencySuffix: Suffix): Flow<Double> //Write off Maybe

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM write_off_table" +
                " WHERE idPT =:id and status = 1 AND price IS NOT NULL"
    )
    fun getScrap(id: Long): Flow<Double> // WriteOff Maybe

    @Query(
        "SELECT COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM write_off_table s" +
                " INNER JOIN project_table p ON p.id = s.idPT" +
                " INNER JOIN settings_table st ON st.idPT = p.id" +
                " WHERE st.currency_suffix = :currencySuffix"
    )
    fun getScrapAllProject(currencySuffix: Suffix): Flow<Double> // WriteOff Maybe

    @Query(
        "SELECT title," +
                " count_suffix AS suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 2 AS category" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND status = 0 AND price IS NOT NULL" +
                " GROUP BY title ORDER BY price DESC"
    )
    fun getOwnNeedAllList(id: Long): Flow<List<TitleSuffixPriceDto>> //maybe

    @Query(
        "SELECT title," +
                " count_suffix AS suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 3 AS category" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND status = 1  AND price IS NOT NULL" +
                " GROUP BY title ORDER BY price DESC"
    )
    fun getScrapAllList(id: Long): Flow<List<TitleSuffixPriceDto>> //maybe

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND status = 0 AND price IS NOT NULL" +
                " GROUP BY category ORDER BY price DESC"
    )
    fun getOwnNeedAllCategoryAllList(id: Long): Flow<List<CategoryPriceDto>>

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT =:id and status = 1 AND price IS NOT NULL" +
                " GROUP BY category ORDER BY price DESC"
    )
    fun getScrapAllCategoryAllList(id: Long): Flow<List<CategoryPriceDto>>

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) AND status=0 AND price IS NOT NULL"
    )
    fun getOwnNeedMonth(id: Long, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query(
        "SELECT" +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS ResultCount" +
                " FROM write_off_table" +
                " WHERE idPT =:id" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) AND status=1 AND price IS NOT NULL"
    )
    fun getScrapMonth(id: Long, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query(
        "SELECT title," +
                " count_suffix AS suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 2 AS category" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND status = 0 AND price IS NOT NULL" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY title ORDER BY price DESC"
    )
    fun getOwnNeedProductPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<TitleSuffixPriceDto>> //maybe

    @Query(
        "SELECT title," +
                " count_suffix AS suffix," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price," +
                " 3 AS category" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND status = 1  AND price IS NOT NULL" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY title ORDER BY price DESC"
    )
    fun getScrapProductPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<TitleSuffixPriceDto>> //maybe

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT =:id AND status = 0 AND price IS NOT NULL" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY category ORDER BY price DESC"
    )
    fun getOwnNeedCategoryPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CategoryPriceDto>>

    @Query(
        "SELECT category," +
                " COALESCE(SUM(CASE WHEN price_all IS NULL THEN price ELSE price_all END), 0.0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT =:id and status = 1 AND price IS NOT NULL" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)" +
                " GROUP BY category ORDER BY price DESC"
    )
    fun getScrapCategoryPeriodList(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CategoryPriceDto>>

    @Query(
        "SELECT  COALESCE(SUM(count), 0) AS count," +
                " count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id AND title=:name"
    )
    fun getAnalysisWriteOffAllTime(id: Long, name: String): Flow<CountSuffixDto>

    @Query(
        "SELECT COALESCE(SUM(count), 0) AS count," +
                " count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id and title=:name and status=0"
    )
    fun getAnalysisWriteOffOwnNeedsAllTime(id: Long, name: String): Flow<CountSuffixDto>

    @Query(
        "SELECT  COALESCE(SUM(count), 0) AS count," +
                " count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id AND title=:name AND status=1"
    )
    fun getAnalysisWriteOffScrapAllTime(id: Long, name: String): Flow<CountSuffixDto>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " Where idPT=:id AND title=:name AND status=0"
    )
    fun getAnalysisWriteOffOwnNeedsMoneyAllTime(id: Long, name: String): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " Where idPT=:id and title=:name and status=1"
    )
    fun getAnalysisWriteOffScrapMoneyAllTime(id: Long, name: String): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(count), 0) AS count," +
                " count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id AND title=:name" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<CountSuffixDto>

    @Query(
        "SELECT count," +
                " count_suffix AS suffix," +
                " COALESCE(price_all, price, 0.0) AS price," +
                " printf('%04d-%02d-%02d', year, month, day) AS date" +
                " FROM write_off_table" +
                " WHERE idPT=:id and title=:name AND status=:status" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisOwnNeedsScrapRangeList(
        id: Long,
        name: String,
        status: Boolean,
        dateBegin: String,
        dateEnd: String
    ): Flow<List<CountSuffixPriceDateDto>>

    @Query(
        "SELECT COALESCE(SUM(count), 0) AS count," +
                "  count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id AND title=:name AND status=1" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffScrapAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<CountSuffixDto>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT=:id AND title=:name AND status=0" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffOwnNeedsMoneyAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT=:id AND title=:name AND status=1" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffScrapMoneyAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT=:id AND status=0" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffOwnNeedsNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " WHERE idPT=:id and status=1" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffScrapNewYearProject(
        id: Long,
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " WHERE status=0 AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffOwnNeedsNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

    @Query(
        "SELECT COALESCE(SUM(price), 0) AS price" +
                " FROM write_off_table" +
                " Where status=1 AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffScrapNewYear(
        dateBegin: String,
        dateEnd: String
    ): Flow<Double>

}