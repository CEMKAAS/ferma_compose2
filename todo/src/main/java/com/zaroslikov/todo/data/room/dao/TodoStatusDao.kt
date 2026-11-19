package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoStatusDao {

    @Query("SELECT * FROM todo_status_table WHERE idPT = :idPT ORDER BY is_done, position, id")
    fun getStatuses(idPT: Long): Flow<List<TodoStatusTable>>

    @Query("SELECT * FROM todo_status_table WHERE id = :id")
    suspend fun getStatus(id: Long): TodoStatusTable?

    @Query("SELECT * FROM todo_status_table WHERE idPT = :idPT AND default_code = :code LIMIT 1")
    suspend fun getDefault(idPT: Long, code: TodoDefaultStatus): TodoStatusTable?

    @Query("SELECT default_code FROM todo_status_table WHERE idPT = :idPT AND default_code IS NOT NULL")
    suspend fun getDefaultCodes(idPT: Long): List<TodoDefaultStatus>

    @Query("SELECT MAX(position) FROM todo_status_table WHERE idPT = :idPT")
    suspend fun getMaxPosition(idPT: Long): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStatus(status: TodoStatusTable): Long

    /** Дефолтный статус уже есть — строка пропускается (уникальность `(idPT, default_code)`). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(statuses: List<TodoStatusTable>)

    @Query("UPDATE todo_status_table SET title = :title, color_index = :colorIndex, is_done = :isDone WHERE id = :id")
    suspend fun updateStatus(id: Long, title: String, colorIndex: Int, isDone: Boolean)

    @Query("UPDATE todo_status_table SET color_index = :colorIndex WHERE id = :id")
    suspend fun updateColor(id: Long, colorIndex: Int)

    @Query("DELETE FROM todo_status_table WHERE id = :id")
    suspend fun deleteStatus(id: Long)

    @Query("DELETE FROM todo_status_table WHERE idPT = :idPT")
    suspend fun deleteForProject(idPT: Long)

    @Query("SELECT * FROM todo_status_table")
    suspend fun getAllForExport(): List<TodoStatusTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(statuses: List<TodoStatusTable>)

    @Query("DELETE FROM todo_status_table")
    suspend fun clear()
}
