package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.project.SettingsTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * from settings_table Where idPT=:idPT")
    fun getSettings(idPT: Long): Flow<SettingsTable>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertSettings(settingsTable: SettingsTable)

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertSettingsLong(settingsTable: SettingsTable): Long

    @Update
    suspend fun updateSettings(settingsTable: SettingsTable)

    @Delete
    suspend fun deleteSettings(settingsTable: SettingsTable)
}