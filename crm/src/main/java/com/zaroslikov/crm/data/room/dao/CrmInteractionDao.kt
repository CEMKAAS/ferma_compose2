package com.zaroslikov.crm.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zaroslikov.crm.data.room.table.CrmInteractionTable
import kotlinx.coroutines.flow.Flow

@Dao
interface CrmInteractionDao {

    @Query("SELECT * FROM crm_interaction_table WHERE client_id = :clientId ORDER BY date DESC, id DESC")
    fun getForClient(clientId: Long): Flow<List<CrmInteractionTable>>

    @Query(
        "SELECT i.* FROM crm_interaction_table i " +
                "INNER JOIN crm_client_table c ON i.client_id = c.id " +
                "WHERE c.idPT = :idPT AND c.is_archived = 0 " +
                "ORDER BY i.date DESC, i.id DESC LIMIT :limit"
    )
    fun getRecentForProject(idPT: Long, limit: Int): Flow<List<CrmInteractionTable>>

    @Insert
    suspend fun insertInteraction(interaction: CrmInteractionTable): Long

    @Delete
    suspend fun deleteInteraction(interaction: CrmInteractionTable)

    @Query("SELECT * FROM crm_interaction_table")
    suspend fun getAllForExport(): List<CrmInteractionTable>

    @Insert
    suspend fun insertAllForImport(interactions: List<CrmInteractionTable>)

    @Query("DELETE FROM crm_interaction_table")
    suspend fun clear()
}
