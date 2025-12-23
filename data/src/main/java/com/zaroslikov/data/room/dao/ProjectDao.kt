package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.ferma.Incubator
import com.zaroslikov.data.room.table.project.ProjectTable
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * from МyINCUBATOR ORDER BY ARHIVE ASC")
    fun getAllProject(): Flow<List<ProjectTable>>

    @Query("SELECT * from МyINCUBATOR Where _id=:id")
    fun getProject(id: Long): Flow<ProjectTable>

    @Query("SELECT * from МyINCUBATOR Where TYPE =:type and mode = 0 and ARHIVE = 1")
    fun getIncubatorListArh6(type: String): Flow<List<ProjectTable>>

    @Query("SELECT * from МyINCUBATOR Where mode = 1 and ARHIVE = 0")
    fun getProjectListAct(): Flow<List<ProjectTable>>

    @Query("SELECT COUNT(*) AS row_count from МyINCUBATOR Where mode = 0")
    fun getCountRowIncubator(): Flow<Int>

    @Query("SELECT COUNT(*) AS row_count from МyINCUBATOR Where mode = 1")
    fun getCountRowProject(): Flow<Int>

    @Query("SELECT _id from МyINCUBATOR ORDER BY _id DESC Limit 1")
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