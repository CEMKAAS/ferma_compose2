package com.zaroslikov.calendar.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "calendar_series", indices = [Index("projectId")])
internal data class CalendarSeriesRow(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val projectId: Long,
    val title: String,
    val date: String,
    val time: String?,
    val note: String,
    val repeatCode: String,
    val repeatUntil: String?,
)

@Entity(
    tableName = "calendar_exception",
    primaryKeys = ["seriesId", "originalDate"],
    foreignKeys = [
        ForeignKey(
            entity = CalendarSeriesRow::class,
            parentColumns = ["id"],
            childColumns = ["seriesId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
internal data class CalendarExceptionRow(
    val seriesId: Long,
    val originalDate: String,
    val title: String?,
    val date: String?,
    val time: String?,
    val note: String?,
)

@Entity(tableName = "calendar_disabled_category", primaryKeys = ["projectId", "categoryCode"])
internal data class CalendarDisabledCategoryRow(val projectId: Long, val categoryCode: String)

internal data class CalendarSeriesWithExceptions(
    @Embedded val series: CalendarSeriesRow,
    @Relation(parentColumn = "id", entityColumn = "seriesId")
    val exceptions: List<CalendarExceptionRow>,
)

@Dao
internal interface CalendarDao {
    @Transaction
    @Query("SELECT * FROM calendar_series WHERE projectId = :projectId ORDER BY id")
    fun observeSeries(projectId: Long): Flow<List<CalendarSeriesWithExceptions>>

    @Query("SELECT categoryCode FROM calendar_disabled_category WHERE projectId = :projectId")
    fun observeDisabledCategories(projectId: Long): Flow<List<String>>

    @Query("SELECT * FROM calendar_series WHERE projectId = :projectId AND id = :seriesId")
    suspend fun getSeries(projectId: Long, seriesId: Long): CalendarSeriesRow?

    @Query("SELECT * FROM calendar_exception WHERE seriesId = :seriesId")
    suspend fun getExceptions(seriesId: Long): List<CalendarExceptionRow>

    @Insert
    suspend fun insertSeries(row: CalendarSeriesRow)

    @Update
    suspend fun updateSeries(row: CalendarSeriesRow)

    @Query("DELETE FROM calendar_series WHERE projectId = :projectId AND id = :seriesId")
    suspend fun deleteSeries(projectId: Long, seriesId: Long)

    @Upsert
    suspend fun putException(row: CalendarExceptionRow)

    @Query("DELETE FROM calendar_exception WHERE seriesId = :seriesId AND originalDate = :originalDate")
    suspend fun deleteException(seriesId: Long, originalDate: String)

    @Query("DELETE FROM calendar_exception WHERE seriesId = :seriesId")
    suspend fun deleteExceptions(seriesId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun setDisabled(row: CalendarDisabledCategoryRow)

    @Query("DELETE FROM calendar_disabled_category WHERE projectId = :projectId AND categoryCode = :categoryCode")
    suspend fun setEnabled(projectId: Long, categoryCode: String)

    @Query("SELECT * FROM calendar_series ORDER BY id")
    suspend fun exportSeries(): List<CalendarSeriesRow>

    @Query("SELECT * FROM calendar_exception ORDER BY seriesId, originalDate")
    suspend fun exportExceptions(): List<CalendarExceptionRow>

    @Query("SELECT * FROM calendar_disabled_category ORDER BY projectId, categoryCode")
    suspend fun exportDisabledCategories(): List<CalendarDisabledCategoryRow>

    @Insert
    suspend fun importSeries(rows: List<CalendarSeriesRow>)

    @Insert
    suspend fun importExceptions(rows: List<CalendarExceptionRow>)

    @Insert
    suspend fun importDisabledCategories(rows: List<CalendarDisabledCategoryRow>)

    @Query("DELETE FROM calendar_series WHERE projectId = :projectId")
    suspend fun deleteProjectSeries(projectId: Long)

    @Query("DELETE FROM calendar_disabled_category WHERE projectId = :projectId")
    suspend fun deleteProjectCategories(projectId: Long)

    @Query("DELETE FROM calendar_series")
    suspend fun clearSeries()

    @Query("DELETE FROM calendar_disabled_category")
    suspend fun clearCategories()
}

@Database(
    entities = [CalendarSeriesRow::class, CalendarExceptionRow::class, CalendarDisabledCategoryRow::class],
    version = 1,
    exportSchema = true,
)
internal abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
}
