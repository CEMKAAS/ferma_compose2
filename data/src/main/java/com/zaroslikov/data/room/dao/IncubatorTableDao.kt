package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.incubator.IncubatorTable
import kotlinx.coroutines.flow.Flow

@Dao
interface IncubatorTableDao {
    @Query("SELECT * From incubator_table")
    fun getAllIncubator(): Flow<List<IncubatorTable>>

    @Query("SELECT * FROM incubator_table WHERE id=:id")
    fun getIncubator(id: Long): Flow<IncubatorTable>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertIncubator(incubatorTable: IncubatorTable)

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertIncubatorLong(incubatorTable: IncubatorTable): Long

    @Update
    suspend fun updateIncubator(incubatorTable: IncubatorTable)

    @Delete
    suspend fun deleteIncubator(incubatorTable: IncubatorTable)
}