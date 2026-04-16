package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.project.TimeNotificationProjectDto
import com.zaroslikov.data.room.table.project.TimeNotificationProjectTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeNotificationProjectDao {
    @Query("SELECT * from time_notification_project_table")
    fun getAllTimeNotificationTableForExport(): Flow<List<TimeNotificationProjectTable>>

    @Upsert
    suspend fun insertAllTimeNotificationTableForImport(data: List<TimeNotificationProjectTable>)

    @Query("DELETE FROM time_notification_project_table")
    suspend fun deleteAllTimeNotificationTable()

    suspend fun clearAndInsertTimeNotificationTableForImport(timeNotificationProjectTables: List<TimeNotificationProjectTable>) {
        deleteAllTimeNotificationTable()
        insertAllTimeNotificationTableForImport(timeNotificationProjectTables)
    }

    @Query("SELECT * FROM time_notification_project_table WHERE project_id=:projectId")
    fun getTimeNotificationByProjectId(projectId: Long): Flow<List<TimeNotificationProjectTable>>

    @Query(
        "SELECT " +
                " p.title as name_project," +
                " t.time," +
                " t.note," +
                " t.project_id" +
                " FROM time_notification_project_table t" +
                "    INNER JOIN project_table p ON p.id = t.project_id" +
                "    WHERE p.archive = 0"
    )
    fun getTimeNotificationInAllActiveProject(): Flow<List<TimeNotificationProjectDto>>

    @Insert
    suspend fun insertTimeNotification(timeNotificationDao: TimeNotificationProjectTable): Long

    @Update
    suspend fun updateTimeNotification(timeNotificationDao: TimeNotificationProjectTable)

    @Delete
    suspend fun deleteTimeNotification(timeNotificationDao: TimeNotificationProjectTable)

    @Query("DELETE FROM time_notification_project_table WHERE id =:id")
    suspend fun deleteTimeNotificationById(id: Long)
}