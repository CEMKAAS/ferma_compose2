package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.todo.data.room.table.TodoAssigneeTable
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoAssigneeDao {
    @Query("SELECT name FROM todo_assignee_table WHERE idPT = :idPT ORDER BY name_key")
    fun getNames(idPT: Long): Flow<List<String>>

    @Query("SELECT * FROM todo_assignee_table WHERE idPT = :idPT ORDER BY name_key")
    fun getProfiles(idPT: Long): Flow<List<TodoAssigneeTable>>

    @Query("SELECT name FROM todo_assignee_table WHERE idPT = :idPT AND name_key = :nameKey")
    suspend fun getName(idPT: Long, nameKey: String): String?

    @Query("SELECT name_key FROM todo_assignee_table WHERE idPT = :idPT")
    suspend fun getNameKeys(idPT: Long): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertName(assignee: TodoAssigneeTable): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNames(names: List<TodoAssigneeTable>)

    @Query("""
        UPDATE OR IGNORE todo_assignee_table
        SET name = :name, name_key = :newKey, profile = :profile
        WHERE idPT = :idPT AND name_key = :oldKey
    """)
    suspend fun rename(
        idPT: Long,
        oldKey: String,
        newKey: String,
        name: String,
        profile: TodoAssigneeProfile
    ): Int

    @Query("DELETE FROM todo_assignee_table WHERE idPT = :idPT AND name_key = :nameKey")
    suspend fun deleteName(idPT: Long, nameKey: String)

    @Query("SELECT * FROM todo_assignee_table ORDER BY idPT, name_key")
    suspend fun getAllForExport(): List<TodoAssigneeTable>

    @Query("DELETE FROM todo_assignee_table WHERE idPT = :idPT")
    suspend fun deleteForProject(idPT: Long)

    @Query("DELETE FROM todo_assignee_table")
    suspend fun clear()
}
