package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.dto.add.BrieflyAddDto
import com.zaroslikov.data.room.dto.add.TitleAndSuffixDto
import com.zaroslikov.data.room.table.ferma.AddTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AddDao {
    @Query("SELECT * from add_table WHERE _id = :id")
    fun getItem(id: Int): Flow<AddTable>

    @Query("SELECT * from add_table Where idPT=:id ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC, _id DESC")
    fun getAllItems(id: Int): Flow<List<AddTable>>

    @Query("SELECT * from add_table Where _id=:id")
    fun getItemAdd(id: Long): Flow<AddTable>

    @Query(
        "SELECT title AS title, SUM(count) as count, count_suffix AS suffix FROM add_table " +
                " WHERE idPT=:id" +
                " GROUP BY title ORDER BY count DESC"
    )
    fun getBrieflyItemAdd(id: Int): Flow<List<BrieflyAddDto>>

    @Query("SELECT * from add_table Where idPT=:id and title =:name ORDER BY DATE(printf('%04d-%02d-%02d', year, month, day)) DESC")
    fun getBrieflyDetailsItemAdd(id: Long, name: String): Flow<List<AddTable>>

    @Query(
        "SELECT title, count_suffix AS suffix" +
                " FROM add_table" +
                " WHERE idPT=:id" +
                " GROUP BY title" +
                " ORDER BY MAX(_id) DESC"
    )
    fun getItemsTitleAddList(id: Long): Flow<List<TitleAndSuffixDto>>

    @Query("SELECT category from add_table Where idPT=:id group by category ")
    fun getItemsCategoryAddList(id: Long): Flow<List<String>>

    @Query("SELECT name FROM animal_table WHERE id=:id")
    fun getAnimalById(id: Long): Flow<String>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(item: AddTable)

    @Update
    suspend fun update(item: AddTable)

    @Query("DELETE FROM add_table WHERE _id = :id")
    suspend fun deleteAddById(id: Long)
}