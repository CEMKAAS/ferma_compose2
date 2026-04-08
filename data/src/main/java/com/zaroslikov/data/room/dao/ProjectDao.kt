package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.project.ProjectTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * from project_table")
    fun getAllProjectTableForExport(): Flow<List<ProjectTable>>

    @Upsert
    suspend fun insertAllProjectTable(data: List<ProjectTable>)

    @Query("DELETE FROM project_table")
    suspend fun deleteAllProjectTable()

    @Transaction
    suspend fun clearAndInsertAllProjectTableForImport(projectTable: List<ProjectTable>) {
        deleteAllProjectTable()
        insertAllProjectTable(projectTable)
    }

    @Query("SELECT archive FROM project_table WHERE id=:id ")
    fun getIsArchiveProject(id: Long): Flow<Boolean>

    @Query("SELECT * from project_table")
    fun getAllProject(): Flow<List<ProjectTable>>

    @Query("SELECT * from project_table Where id=:id")
    fun getProject(id: Long): Flow<ProjectTable>

    /*  @Query("SELECT * from project_table Where TYPE =:type and mode = 0 and archive = 1")
      fun getIncubatorListArh6(type: String): Flow<List<ProjectTable>>
  */
    @Query("SELECT * from project_table Where mode = 0 and archive = 0")
    fun getProjectListAct(): Flow<List<ProjectTable>>

    @Query("SELECT COUNT(*) AS row_count from project_table Where mode = 0")
    fun getCountRowIncubator(): Flow<Int>

    @Query("SELECT COUNT(*) AS row_count from project_table Where mode = 1")
    fun getCountRowProject(): Flow<Int>

    @Query("SELECT id from project_table ORDER BY id DESC Limit 1")
    fun getLastProject(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertProject(projectTable: ProjectTable)

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertProjectLong(projectTable: ProjectTable): Long

    @Update
    suspend fun updateProject(item: ProjectTable)

    @Delete
    suspend fun deleteProject(item: ProjectTable)
}