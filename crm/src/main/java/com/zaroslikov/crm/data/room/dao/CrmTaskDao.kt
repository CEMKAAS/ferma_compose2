package com.zaroslikov.crm.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.zaroslikov.crm.data.room.table.CrmTaskTable
import kotlinx.coroutines.flow.Flow

@Dao
interface CrmTaskDao {

    @Query("SELECT * FROM crm_task_table WHERE idPT = :idPT AND is_done = :isDone ORDER BY due_date, id")
    fun getTasks(idPT: Long, isDone: Boolean): Flow<List<CrmTaskTable>>

    @Query("SELECT * FROM crm_task_table WHERE idPT = :idPT AND is_done = 0 ORDER BY due_date, id LIMIT :limit")
    fun getUpcoming(idPT: Long, limit: Int): Flow<List<CrmTaskTable>>

    @Query("SELECT * FROM crm_task_table WHERE client_id = :clientId ORDER BY is_done, due_date, id")
    fun getForClient(clientId: Long): Flow<List<CrmTaskTable>>

    @Upsert
    suspend fun upsertTask(task: CrmTaskTable): Long

    @Query("UPDATE crm_task_table SET is_done = :isDone WHERE id = :id")
    suspend fun setDone(id: Long, isDone: Boolean)

    @Delete
    suspend fun deleteTask(task: CrmTaskTable)

    @Query("DELETE FROM crm_task_table WHERE idPT = :idPT")
    suspend fun deleteForProject(idPT: Long)

    @Query("SELECT * FROM crm_task_table")
    suspend fun getAllForExport(): List<CrmTaskTable>

    @Insert
    suspend fun insertAllForImport(tasks: List<CrmTaskTable>)

    @Query("DELETE FROM crm_task_table")
    suspend fun clear()
}
