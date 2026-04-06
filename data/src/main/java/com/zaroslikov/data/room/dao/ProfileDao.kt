package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.table.profile.ProfileTable
import com.zaroslikov.domain.models.enums.Suffix
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * from profile_table")
    fun getAllProfileTableForExport(): Flow<List<ProfileTable>>

    @Upsert
    suspend fun insertAllProfileTable(data: List<ProfileTable>)

    @Query("DELETE FROM profile_table")
    suspend fun deleteAllProfileTable()

    @Transaction
    suspend fun clearAndInsertAllProfileTable(profileTable: List<ProfileTable>) {
        deleteAllProfileTable()
        insertAllProfileTable(profileTable)
    }

    @Update
    suspend fun updateProfile(profileTable: ProfileTable)

    @Query("SELECT * FROM profile_table WHERE id= 1")
    fun getProfile(): Flow<ProfileTable>

    @Query(
        "SELECT currency  " +
                " FROM (" +
                " SELECT currency_suffix AS currency FROM settings_table" +
                " UNION ALL" +
                " SELECT currency_suffix FROM incubator_table" +
                " )" +
                " GROUP BY currency" +
                " ORDER BY COUNT(*) DESC"
    )
    fun getAllCurrenciesSorted(): Flow<List<Suffix>>
}