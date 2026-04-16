package com.zaroslikov.data.room.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.incubator.TimeNotificationDto
import com.zaroslikov.data.room.table.incubator.TimeNotificationIncubatorTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeNotificationIncubatorDao {
    @Query("SELECT * from time_notification_incubator_table")
    fun getAllTimeNotificationTableForExport(): Flow<List<TimeNotificationIncubatorTable>>

    @Upsert
    suspend fun insertAllTimeNotificationTableForImport(data: List<TimeNotificationIncubatorTable>)

    @Query("DELETE FROM time_notification_incubator_table")
    suspend fun deleteAllTimeNotificationTable()

    suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotification: List<TimeNotificationIncubatorTable>) {
        deleteAllTimeNotificationTable()
        insertAllTimeNotificationTableForImport(domainTimeNotification)
    }

    @Query("SELECT * FROM time_notification_incubator_table WHERE bookmark_id=:bookmarkId")
    fun getTimeNotificationByBookmarkId(bookmarkId: Long): Flow<List<TimeNotificationIncubatorTable>>

    @Query(
        "SELECT " +
                "b.title as name_bookmark, " +
                "t.time, " +
                "t.note, " +
                "t.bookmark_id, " +
                "i.idPT as project_id " +
                " FROM time_notification_incubator_table t" +
                "    INNER JOIN bookmark_incubator b ON b.id = t.bookmark_id" +
                " INNER JOIN incubator_table i ON i.id = b.idPT" +
                "    WHERE b.is_activity_bookmark = 1"
    )
    fun getTimeNotificationInAllActiveBookmark(): Flow<List<TimeNotificationDto>>

    @Insert
    suspend fun insertTimeNotification(timeNotificationDao: TimeNotificationIncubatorTable): Long

    @Update
    suspend fun updateTimeNotification(timeNotificationDao: TimeNotificationIncubatorTable)

    @Delete
    suspend fun deleteTimeNotification(timeNotificationDao: TimeNotificationIncubatorTable)

    @Query("DELETE FROM time_notification_incubator_table WHERE id =:id")
    suspend fun deleteTimeNotificationById(id: Long)
}