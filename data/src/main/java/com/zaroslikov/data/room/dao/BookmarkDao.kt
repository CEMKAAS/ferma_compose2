package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.incubator.CountRejectedCountDto
import com.zaroslikov.data.room.dto.incubator.FinanceAllIncubator
import com.zaroslikov.data.room.dto.incubator.FinanceIncubatorHistoryDto
import com.zaroslikov.data.room.dto.incubator.FinanceIncubatorMainDto
import com.zaroslikov.data.room.dto.incubator.TitleCountDto
import com.zaroslikov.data.room.dto.incubator.TypeEggCountDto
import com.zaroslikov.data.room.table.incubator.BookmarkTable
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * from bookmark_incubator")
    fun getAllBookmarkIncubatorTableForExport(): Flow<List<BookmarkTable>>

    @Upsert
    suspend fun insertAllBookmarkTable(data: List<BookmarkTable>)

    @Query("DELETE FROM bookmark_incubator")
    suspend fun deleteAllBookmarkTable()

    @Transaction
    suspend fun clearAndInsertBookmarkTableForImport(bookmarkTable: List<BookmarkTable>) {
        deleteAllBookmarkTable()
        insertAllBookmarkTable(bookmarkTable)
    }

    @Query("SELECT * FROM bookmark_incubator WHERE id = :id")
    fun getBookmark(id: Long): Flow<BookmarkTable>

    @Query("SELECT * FROM bookmark_incubator WHERE idPT =:id")
    fun getAllBookmark(id: Long): Flow<List<BookmarkTable>>

    @Query("SELECT breed FROM bookmark_incubator WHERE type =:type GROUP BY breed")
    fun getBreedBookmark(type: TypeEgg): Flow<List<String>>

    @Query(
        "SELECT" +
                " breed AS title," +
                " SUM(count) as count " +
                " FROM bookmark_incubator" +
                " WHERE is_activity_bookmark = 0 and idPT =:idPT and type =:type" +
                " GROUP BY breed"
    )
    fun getBreedStatisticList(type: TypeEgg, idPT: Long): Flow<List<TitleCountDto>>

    @Query(
        "SELECT" +
                " type AS type_egg," +
                " SUM(count) as count" +
                " FROM bookmark_incubator" +
                " WHERE is_activity_bookmark = 0 and idPT =:idPT" +
                " GROUP BY type"
    )
    fun getTypeStatisticList(idPT: Long): Flow<List<TypeEggCountDto>>

    @Query("SELECT SUM(count) as count, SUM(rejected_count) as rejected_count FROM bookmark_incubator WHERE is_activity_bookmark = 0 and idPT =:idPT")
    fun getCountAndRejectedCountAll(idPT: Long): Flow<CountRejectedCountDto>

    @Query("SELECT * FROM bookmark_incubator WHERE type =:type and is_activity_bookmark = 0 and idPT =:idPT")
    fun getBookmarkList(type: TypeEgg, idPT: Long): Flow<List<BookmarkTable>>

    @Query(
        "SELECT * FROM bookmark_incubator WHERE is_activity_bookmark = 0 and idPT =:idPT " +
                " ORDER BY strftime('%Y-%m-%d', substr(end_date, 7, 4) || '-' || substr(end_date, 4, 2) || '-' || substr(end_date, 1, 2)) DESC, id DESC"
    )
    fun getBookmarkListByIdPT(idPT: Long): Flow<List<BookmarkTable>>

    @Query(
        "SELECT " +
                " SUM((COALESCE(chick_price, 0.0) * (count - rejected_count)) - (count * COALESCE(price_all, b.price, 0.0))) - COALESCE(i.price, 0.0) AS profit, " +
                " SUM(COALESCE(chick_price, 0.0) * (count - rejected_count)) AS income, " +
                " SUM(count - rejected_count) AS chicks, " +
                " SUM(count * COALESCE(price_all, b.price, 0.0)) + COALESCE(i.price, 0.0) AS expenses, " +
                " COALESCE(i.price, 0.0) AS incubator, " +
                " SUM(COALESCE(price_all, b.price, 0.0) * count) AS eggs_price, " +
                " SUM(COALESCE(price_all, b.price, 0.0) * (count - rejected_count)) AS posted_price, " +
                " SUM(COALESCE(price_all, b.price, 0.0) * rejected_count) AS losses_price, " +
                " SUM(count - rejected_count) AS posted_egg, " +
                " SUM(rejected_count) AS losses_egg, " +
                " CASE WHEN SUM(count) = 0 THEN 0.0 " +
                "      ELSE SUM(COALESCE(price_all, b.price, 0.0) * count) / SUM(count) " +
                " END AS average_egg_price, " +
                " CASE WHEN SUM(count - rejected_count) = 0 THEN 0.0 " +
                "      ELSE SUM(COALESCE(chick_price, 0.0) * (count - rejected_count)) / SUM(count - rejected_count) " +
                " END AS average_chicks_price, " +
                " CASE WHEN SUM(count - rejected_count) = 0 THEN 0.0 " +
                "      ELSE (SUM(count * COALESCE(price_all, b.price, 0.0)) + COALESCE(i.price, 0.0)) / SUM(count - rejected_count)" +
                " END AS cost_chicks_price" +
                " FROM bookmark_incubator b " +
                " JOIN incubator_table i ON i.id = :idPT " +
                " WHERE b.is_activity_bookmark = 0 AND b.idPT = :idPT"
    )
    fun getFinanceIncubator(idPT: Long): Flow<FinanceIncubatorMainDto?>

    @Query(
        "SELECT " +
                " title," +
                " type," +
                " breed," +
                " count,  " +
                " (COALESCE(chick_price, 0.0) * (count - rejected_count)) - (count * COALESCE(price_all, price, 0.0)) AS profit," +
                " COALESCE(chick_price, 0.0) * (count - rejected_count) AS income, " +
                " count - rejected_count AS chicks, " +
                " count * COALESCE(price_all, price, 0.0) AS expenses, " +
                " COALESCE(CASE WHEN price_all IS NULL THEN price/count ELSE price_all END, 0.0) AS price_one_egg," +
                " COALESCE(price_all, price, 0.0) * (count - rejected_count) AS posted_price, " +
                " COALESCE(price_all, price, 0.0) * rejected_count AS losses_price, " +
                " count - rejected_count AS posted_egg, " +
                " rejected_count AS losses_egg " +
                " FROM bookmark_incubator " +
                " WHERE is_activity_bookmark = 0 AND idPT = :idPT " +
                " ORDER BY strftime('%Y-%m-%d', substr(end_date, 7, 4) || '-' || substr(end_date, 4, 2) || '-' || substr(end_date, 1, 2)) DESC, id DESC"
    )
    fun getFinanceIncubatorList(idPT: Long): Flow<List<FinanceIncubatorHistoryDto>>


    @Query(
        "SELECT " +
                " SUM(COALESCE(chick_price, 0.0) * (count - rejected_count)) AS income, " +
                " SUM(count * COALESCE(price_all, b.price, 0.0)) + COALESCE(i.price, 0.0) AS expenses, " +
                " SUM(count - rejected_count) AS posted_egg, " +
                " SUM(rejected_count) AS losses_egg " +
                " FROM bookmark_incubator b " +
                " JOIN incubator_table i ON i.id = b.idPT " +
                " WHERE b.is_activity_bookmark = 0 AND i.currency_suffix = :currencySuffix"
    )
    fun getFinanceIncubatorAllProject(currencySuffix: Suffix): Flow<FinanceAllIncubator>

    @Query("SELECT * FROM bookmark_incubator WHERE is_activity_bookmark = 1 and idPT =:idPT")
    fun getActivityBookmark(idPT: Long): Flow<BookmarkTable?>

    @Query("SELECT b.* FROM bookmark_incubator b" +
            "    INNER JOIN incubator_table i ON b.idPT = i.id" +
            "    WHERE b.is_activity_bookmark = 1" +
            "    AND i.idPT = :idPT")
    fun getActivityBookmarkByIdPT(idPT: Long): Flow<BookmarkTable?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BookmarkTable): Long

    @Update
    suspend fun update(item: BookmarkTable)

    @Query("DELETE FROM bookmark_incubator WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Delete
    suspend fun deleteBookmark(item: BookmarkTable)
}