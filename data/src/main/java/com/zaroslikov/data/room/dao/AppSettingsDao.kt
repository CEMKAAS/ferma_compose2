package com.zaroslikov.data.room.dao

import androidx.room.Dao

import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.app.AppSettingsTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Query("SELECT * from app_settings_table")
    fun getAllAppSettingsTableForExport(): Flow<List<AppSettingsTable>>

    @Upsert
    suspend fun insertAllAppSettingsTable(appSettingsTable: List<AppSettingsTable>)

    @Query("DELETE FROM app_settings_table")
    suspend fun deleteAllAppSettingsTable()

    @Transaction
    suspend fun clearAndInsertAppSettingsTableForImport(appSettingsTable: List<AppSettingsTable>) {
        deleteAllAppSettingsTable()
        insertAllAppSettingsTable(appSettingsTable)
    }

    @Update
    suspend fun updateAppSettings(appSettingsTable: AppSettingsTable)

    @Query("SELECT * FROM app_settings_table WHERE id= 1")
    fun getAppSettings(): Flow<AppSettingsTable>
}