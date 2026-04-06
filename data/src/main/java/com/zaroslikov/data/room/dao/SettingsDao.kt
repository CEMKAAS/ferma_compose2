package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.project.SettingsTable
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * from settings_table")
    fun getAllSettingsTableForExport(): Flow<List<SettingsTable>>

    @Upsert
    suspend fun insertAllSettingsTable(data: List<SettingsTable>)

    @Query("DELETE FROM settings_table")
    suspend fun deleteAllSettingsTable()

    @Transaction
    suspend fun clearAndInsertAllSettingsTableForImport(settingsTable: List<SettingsTable>) {
        deleteAllSettingsTable()
        insertAllSettingsTable(settingsTable)
    }

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

    @Query(
        " SELECT currency_suffix " +
                " FROM settings_table" +
                " GROUP BY currency_suffix ORDER BY COUNT(*) DESC"
    )
    fun getCurrencySuffixList(): Flow<List<Suffix>>
}