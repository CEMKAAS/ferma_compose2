package com.zaroslikov.crm.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.zaroslikov.crm.data.room.table.CrmContactTable
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CrmContactDao {

    @Query("SELECT * FROM crm_contact_table WHERE client_id = :clientId ORDER BY is_primary DESC, id")
    abstract fun getContacts(clientId: Long): Flow<List<CrmContactTable>>

    @Query(
        "SELECT ct.* FROM crm_contact_table ct " +
                "INNER JOIN crm_client_table c ON ct.client_id = c.id " +
                "WHERE c.idPT = :idPT ORDER BY ct.is_primary DESC, ct.id"
    )
    abstract fun getContactsForProject(idPT: Long): Flow<List<CrmContactTable>>

    @Query("DELETE FROM crm_contact_table WHERE client_id = :clientId")
    abstract suspend fun deleteForClient(clientId: Long)

    @Insert
    abstract suspend fun insertAll(contacts: List<CrmContactTable>)

    @Transaction
    open suspend fun replaceForClient(clientId: Long, contacts: List<CrmContactTable>) {
        deleteForClient(clientId)
        insertAll(contacts.map { it.copy(id = 0, clientId = clientId) })
    }

    @Query("SELECT * FROM crm_contact_table")
    abstract suspend fun getAllForExport(): List<CrmContactTable>

    @Query("DELETE FROM crm_contact_table")
    abstract suspend fun clear()
}
