package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.shared.CountSuffixDto
import com.zaroslikov.data.room.dto.shared.TitleSuffixCategoryDto
import com.zaroslikov.data.room.dto.write_off.BrieflyWriteOffDto
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WriteOffDao {
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
        "SELECT title, count_suffix AS suffix, 0 AS category from add_table Where idPT=:id group by title, count_suffix" +
                " UNION ALL" +
                " SELECT title,  count_suffix AS suffix, 1 AS category from expenses_table Where idPT=:id and is_show_warehouse = 1 group by title, count_suffix"
    )
    fun getItemsWriteOffList(id: Long): Flow<List<TitleSuffixCategoryDto>>


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertWriteOff(item: WriteOffTable)

    @Update
    suspend fun updateWriteOff(item: WriteOffTable)

    @Query("DELETE FROM write_off_table WHERE _id = :id")
    suspend fun deleteWriteOff(id: Long)

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount" +
            " FROM write_off_table" +
            " WHERE write_off_table.idPT =:id and status=0")
    fun getOwnNeed(id: Long): Flow<Double> //Write off Maybe

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount" +
            " FROM write_off_table" +
            " WHERE write_off_table.idPT =:id and status=1")
    fun getScrap(id: Long): Flow<Double> // WriteOff Maybe

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount" +
            " FROM write_off_table" +
            " WHERE write_off_table.idPT =:id" +
            " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and status=0")
    fun getOwnNeedMonth(id: Long, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query("SELECT COALESCE(SUM(price), 0.0) AS ResultCount" +
            " FROM write_off_table" +
            " WHERE write_off_table.idPT =:id" +
            " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
            " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd) and status=1")
    fun getScrapMonth(id: Long, dateBegin: String, dateEnd: String): Flow<Double> //maybe

    @Query("SELECT  COALESCE(SUM(count), 0) AS count," +
            " count_suffix AS suffix" +
            " FROM write_off_table" +
            " WHERE idPT=:id AND title=:name")
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
        "SELECT COALESCE(SUM(count), 0) AS count," +
                " count_suffix AS suffix" +
                " FROM write_off_table" +
                " WHERE idPT=:id and title=:name AND status=0" +
                " AND DATE(printf('%04d-%02d-%02d', year, month, day))" +
                " BETWEEN DATE(:dateBegin) AND DATE(:dateEnd)"
    )
    fun getAnalysisWriteOffOwnNeedsAllTimeRange(
        id: Long,
        name: String,
        dateBegin: String,
        dateEnd: String
    ): Flow<CountSuffixDto>

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