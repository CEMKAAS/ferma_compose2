package com.zaroslikov.data.room.dao.template

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.add.AddTemplateDto
import com.zaroslikov.data.room.table.ferma.templateOne.AddTemplateTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AddTemplateDao {

    @Query("SELECT * from add_template_table")
    fun getAllAddTemplateTableForExport(): Flow<List<AddTemplateTable>>

    @Upsert
    suspend fun insertAllAddTemplateTable(addTable: List<AddTemplateTable>)

    @Query("DELETE FROM add_template_table")
    suspend fun deleteAllAddTemplateTable()

    @Transaction
    suspend fun clearAndInsertAddTemplateTableForImport(addTable: List<AddTemplateTable>) {
        deleteAllAddTemplateTable()
        insertAllAddTemplateTable(addTable)
    }

    @Query("SELECT * FROM add_template_table WHERE _id = :id")
    fun getAddTemplateItem(id: Long): Flow<AddTemplateTable>

    @Query(
        "SELECT " +
                " a._id," +
                " a.name_template, " +
                " a.title," +
                " a.count," +
                " a.count_suffix," +
                " a.price," +
                " a.category," +
                " a.animal_id," +
                " at.name AS animal_name," +
                " a.note," +
                " a.idPT " +
                " FROM add_template_table a" +
                " LEFT JOIN animal_table at ON at.id = a.animal_id" +
                " WHERE a.idPT = :id"
    )
    fun getAllAddTemplateItems(id: Long): Flow<List<AddTemplateDto>>


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(item: AddTemplateTable)

    @Update
    suspend fun update(item: AddTemplateTable)

    @Query("DELETE FROM add_template_table WHERE _id = :id")
    suspend fun deleteAddTemplateItemById(id: Long)

}