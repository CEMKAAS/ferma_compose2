package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("""
        UPDATE settings_table
        SET currency_suffix = :currencySuffix, weight_suffix = :weightSuffix,
            volume_suffix = :volumeSuffix, linear_suffix = :linearSuffix, idPT = :idPT
        WHERE id = :id
    """)
    suspend fun updateSettings(
        id: Long,
        currencySuffix: Suffix,
        weightSuffix: Suffix,
        volumeSuffix: Suffix,
        linearSuffix: Suffix,
        idPT: Long,
    )

    @Query("UPDATE settings_table SET warehouse_card_codes = :codesJson WHERE idPT = :idPT")
    suspend fun updateWarehouseCards(idPT: Long, codesJson: String): Int

    @Delete
    suspend fun deleteSettings(settingsTable: SettingsTable)

    @Query(
        " SELECT currency_suffix " +
                " FROM settings_table" +
                " GROUP BY currency_suffix ORDER BY COUNT(*) DESC"
    )
    fun getCurrencySuffixList(): Flow<List<Suffix>>
}
