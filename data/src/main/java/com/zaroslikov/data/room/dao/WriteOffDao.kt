package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.write_off.BrieflyWriteOffDto
import com.zaroslikov.data.room.dto.write_off.TitleWriteOffDto
import com.zaroslikov.data.room.table.ferma.WriteOffTable
import kotlinx.coroutines.flow.Flow

@Dao
interface WriteOffDao {
    @Query("SELECT * from write_off_table Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day) ) DESC, _id DESC")
    fun getAllWriteOffItems(id: Long): Flow<List<WriteOffTable>>

    @Query("SELECT * from write_off_table Where _id=:id ")
    fun getItemWriteOff(id: Long): Flow<WriteOffTable>

    @Query("SELECT * from write_off_table Where animal_count_id=:id ")
    fun getItemWriteOffIdAnimalCount(id: Long): Flow<WriteOffTable>

    @Query(
        "SELECT title, SUM(count) as count, count_suffix as suffix from write_off_table" +
                " Where idPT=:id group by title ORDER BY count DESC"
    )
    fun getBrieflyItemWriteOff(id: Long): Flow<List<BrieflyWriteOffDto>>

    @Query("SELECT * from write_off_table Where idPT=:id and title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemWriteOff(id: Long, name: String): Flow<List<WriteOffTable>>

    @Query(
        "SELECT title, count_suffix AS suffix, 0 AS category from add_table Where idPT=:id group by title, count_suffix" +
                " UNION ALL" +
                " SELECT title,  count_suffix AS suffix, 1 AS category from expenses_table Where idPT=:id and is_show_warehouse = 1 group by title, count_suffix"
    )
    fun getItemsWriteOffList(id: Long): Flow<List<TitleWriteOffDto>>


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertWriteOff(item: WriteOffTable)

    @Update
    suspend fun updateWriteOff(item: WriteOffTable)

    @Query("DELETE FROM write_off_table WHERE _id = :id")
    suspend fun deleteWriteOff(id: Long)

}