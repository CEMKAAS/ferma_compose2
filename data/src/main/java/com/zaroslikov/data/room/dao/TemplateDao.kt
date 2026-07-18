package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.template.AddTemplateDto
import com.zaroslikov.data.room.table.ferma.templateOne.TemplateTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {

    @Query("SELECT * from template_table")
    fun getAllAddTemplateTableForExport(): Flow<List<TemplateTable>>

    @Upsert
    suspend fun insertAllAddTemplateTable(addTable: List<TemplateTable>)

    @Query("DELETE FROM template_table")
    suspend fun deleteAllAddTemplateTable()

    @Transaction
    suspend fun clearAndInsertAddTemplateTableForImport(addTable: List<TemplateTable>) {
        deleteAllAddTemplateTable()
        insertAllAddTemplateTable(addTable)
    }

    @Query("SELECT * FROM template_table WHERE _id = :id")
    fun getAddTemplateItem(id: Long): Flow<TemplateTable?>

    @Query("UPDATE template_table SET is_pinned =:pin  WHERE _id = :id")
    suspend fun setPinById(pin: Boolean, id: Long)

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
                " a.idPT, " +
                " a.is_pinned, " +
                " a.is_multi_project_template" +
                " FROM template_table a" +
                " LEFT JOIN animal_table at ON at.id = a.animal_id" +
                " WHERE is_multi_project_template = 1 or a.idPT = :id and a.template_type = 0 " +
                " ORDER BY a.is_pinned DESC, a._id DESC"
    )
    fun getAllAddTemplateItems(id: Long): Flow<List<AddTemplateDto>>


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(item: TemplateTable)

    @Update
    suspend fun update(item: TemplateTable)

    @Query("DELETE FROM template_table WHERE _id = :id")
    suspend fun deleteTemplateItemById(id: Long)

}