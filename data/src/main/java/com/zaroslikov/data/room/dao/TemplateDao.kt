package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.zaroslikov.data.room.dto.template.AddTemplateDto
import com.zaroslikov.data.room.dto.template.ExpensesTemplateDto
import com.zaroslikov.data.room.dto.template.SaleTemplateDto
import com.zaroslikov.data.room.dto.template.WriteOffTemplateDto
import com.zaroslikov.data.room.table.ferma.TemplateTable
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
                " WHERE (is_multi_project_template = 1 or a.idPT = :id) and a.template_type = 0 " +
                " ORDER BY a.is_pinned DESC, a._id DESC"
    )
    fun getAllAddTemplateItems(id: Long): Flow<List<AddTemplateDto>>

    @Query(
        "SELECT " +
                " a._id," +
                " a.name_template, " +
                " a.title," +
                " a.count," +
                " a.count_suffix," +
                " a.price," +
                " a.price_all," +
                " a.price_suffix," +
                " a.category," +
                " a.buyer," +
                " a.note," +
                " a.idPT, " +
                " a.is_pinned, " +
                " a.is_multi_project_template" +
                " FROM template_table a" +
                " WHERE (is_multi_project_template = 1 or a.idPT = :id) and a.template_type = 1 " +
                " ORDER BY a.is_pinned DESC, a._id DESC"
    )
    fun getAllSaleTemplateItems(id: Long): Flow<List<SaleTemplateDto>>

    @Query(
        "SELECT " +
                " a._id," +
                " a.name_template, " +
                " a.title," +
                " a.count," +
                " a.count_suffix," +
                " a.price," +
                " a.price_all," +
                " a.price_suffix," +
                " a.category," +
                " a.write_off_status," +
                " a.note," +
                " a.idPT," +
                " a.is_pinned," +
                " a.is_multi_project_template" +
                " FROM template_table a" +
                " WHERE (is_multi_project_template = 1 or a.idPT = :id) and a.template_type = 2" +
                " ORDER BY a.is_pinned DESC, a._id DESC"
    )
    fun getAllWriteOffTemplateItems(id: Long): Flow<List<WriteOffTemplateDto>>

    @Query(
        "SELECT " +
                " a._id," +
                " a.name_template, " +
                " a.title," +
                " a.count," +
                " a.count_suffix," +
                " a.price," +
                " a.price_all," +
                " a.price_suffix," +
                " a.category," +
                " a.note," +
                " a.idPT," +
                " a.is_pinned," +
                " a.is_multi_project_template" +
                " FROM template_table a" +
                " WHERE (is_multi_project_template = 1 or a.idPT = :id) and a.template_type = 3" +
                " ORDER BY a.is_pinned DESC, a._id DESC"
    )
    fun getAllExpensesTemplateItems(id: Long): Flow<List<ExpensesTemplateDto>>


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(item: TemplateTable)

    @Update
    suspend fun update(item: TemplateTable)

    @Query("DELETE FROM template_table WHERE _id = :id")
    suspend fun deleteTemplateItemById(id: Long)

}