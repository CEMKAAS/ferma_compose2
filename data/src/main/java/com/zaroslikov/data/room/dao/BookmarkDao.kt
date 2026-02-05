package com.zaroslikov.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.data.room.table.incubator.BookmarkTable
import com.zaroslikov.domain.models.enums.TypeEgg
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark_incubator WHERE id = :id")
    fun getBookmark(id: Long): Flow<BookmarkTable>

    @Query("SELECT * FROM bookmark_incubator WHERE idPT =:id")
    fun getAllBookmark(id: Long): Flow<List<BookmarkTable>>

    @Query("SELECT breed FROM bookmark_incubator WHERE type =:type GROUP BY breed")
    fun getBreedBookmark(type: TypeEgg): Flow<List<String>>
    @Query("SELECT * FROM bookmark_incubator WHERE type =:type and is_activity_bookmark = 0")
    fun getBookmarkList(type: TypeEgg): Flow<List<BookmarkTable>>

    @Query("SELECT * FROM bookmark_incubator WHERE is_activity_bookmark = 1 and idPT =:idPT" )
    fun getActivityBookmark(idPT: Long): Flow<BookmarkTable?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: BookmarkTable): Long

    @Update
    suspend fun update(item: BookmarkTable)

    @Query("DELETE FROM bookmark_incubator WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Delete
    suspend fun deleteBookmark(item: BookmarkTable)
}