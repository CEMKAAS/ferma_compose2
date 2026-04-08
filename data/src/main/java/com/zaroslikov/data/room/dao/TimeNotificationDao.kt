package com.zaroslikov.data.room.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.incubator.TimeNotificationTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeNotificationDao {
    @Query("SELECT * from time_notification_table")
    fun getAllTimeNotificationTableForExport(): Flow<List<TimeNotificationTable>>

    @Upsert
    suspend fun insertAllTimeNotificationTableForImport(data: List<TimeNotificationTable>)

    @Query("DELETE FROM project_table")
    suspend fun deleteAllTimeNotificationTable()

    suspend fun clearAndInsertTimeNotificationTableForImport(domainTimeNotification: List<TimeNotificationTable>) {
        deleteAllTimeNotificationTable()
        insertAllTimeNotificationTableForImport(domainTimeNotification)
    }

    @Query("SELECT * FROM time_notification_table WHERE bookmark_id=:bookmarkId")
    fun getTimeNotificationByBookmarkId(bookmarkId: Long): Flow<List<TimeNotificationTable>>

    @Insert
    suspend fun insertTimeNotification(timeNotificationDao: TimeNotificationTable): Long

    @Update
    suspend fun updateTimeNotification(timeNotificationDao: TimeNotificationTable)

    @Delete
    suspend fun deleteTimeNotification(timeNotificationDao: TimeNotificationTable)

    @Query("DELETE FROM time_notification_table WHERE id =:id")
    suspend fun deleteTimeNotificationById(id: Long)
}