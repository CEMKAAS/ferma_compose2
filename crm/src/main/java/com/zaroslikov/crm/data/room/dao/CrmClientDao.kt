package com.zaroslikov.crm.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.crm.data.room.table.CrmClientTable
import kotlinx.coroutines.flow.Flow

/**
 * REPLACE и `@Upsert` для клиентов запрещены: REPLACE удаляет строку и каскадом
 * сносит контакты и взаимодействия клиента.
 */
@Dao
interface CrmClientDao {

    @Query("SELECT * FROM crm_client_table WHERE idPT = :idPT AND is_archived = 0 ORDER BY name COLLATE NOCASE")
    fun getClients(idPT: Long): Flow<List<CrmClientTable>>

    @Query("SELECT * FROM crm_client_table WHERE idPT = :idPT AND is_archived = 1 ORDER BY name COLLATE NOCASE")
    fun getArchivedClients(idPT: Long): Flow<List<CrmClientTable>>

    @Query("SELECT * FROM crm_client_table WHERE id = :id")
    fun getClient(id: Long): Flow<CrmClientTable?>

    @Query("SELECT EXISTS(SELECT 1 FROM crm_client_table WHERE idPT = :idPT AND sales_key = :salesKey)")
    suspend fun existsBySalesKey(idPT: Long, salesKey: String): Boolean

    @Query("SELECT * FROM crm_client_table WHERE idPT = :idPT AND sales_key = :salesKey LIMIT 1")
    suspend fun findBySalesKey(idPT: Long, salesKey: String): CrmClientTable?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertClient(client: CrmClientTable): Long

    /** Обновляет только редактируемые поля: `sales_key`, `source` и `created_date` неизменяемы. */
    @Query(
        "UPDATE crm_client_table SET name = :name, is_vip = :isVip, note = :note, " +
                "is_favorite = :isFavorite WHERE id = :id"
    )
    suspend fun updateClient(id: Long, name: String, isVip: Boolean, note: String, isFavorite: Boolean)

    /** Синхронизация с продажами: существующие `(idPT, sales_key)` пропускаются. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(clients: List<CrmClientTable>)

    @Query("UPDATE crm_client_table SET is_archived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: Long, isArchived: Boolean)

    @Query("DELETE FROM crm_client_table WHERE id = :id")
    suspend fun deleteClient(id: Long)

    /** Контакты и взаимодействия удаляются каскадом по внешнему ключу. */
    @Query("DELETE FROM crm_client_table WHERE idPT = :idPT")
    suspend fun deleteForProject(idPT: Long)

    @Query("SELECT * FROM crm_client_table")
    suspend fun getAllForExport(): List<CrmClientTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(clients: List<CrmClientTable>)

    @Query("DELETE FROM crm_client_table")
    suspend fun clear()
}
