package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.todo.data.room.table.TodoTagTable
import kotlinx.coroutines.flow.Flow

/** REPLACE запрещён: удаление строки каскадом снимает тег со всех задач. */
@Dao
interface TodoTagDao {

    @Query("SELECT * FROM todo_tag_table WHERE idPT = :idPT ORDER BY title COLLATE NOCASE, id")
    fun getTags(idPT: Long): Flow<List<TodoTagTable>>

    @Query("SELECT * FROM todo_tag_table WHERE idPT = :idPT AND title = :title LIMIT 1")
    suspend fun findByTitle(idPT: Long, title: String): TodoTagTable?

    /** Те из [ids], что есть в проекте [idPT]. */
    @Query("SELECT id FROM todo_tag_table WHERE idPT = :idPT AND id IN (:ids)")
    suspend fun getExistingIds(idPT: Long, ids: Collection<Long>): List<Long>

    @Query("SELECT * FROM todo_tag_table WHERE id = :id")
    suspend fun getTag(id: Long): TodoTagTable?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTag(tag: TodoTagTable): Long

    @Query("UPDATE todo_tag_table SET title = :title, color_index = :colorIndex WHERE id = :id")
    suspend fun updateTag(id: Long, title: String, colorIndex: Int)

    @Query("DELETE FROM todo_tag_table WHERE id = :id")
    suspend fun deleteTag(id: Long)

    @Query("DELETE FROM todo_tag_table WHERE idPT = :idPT")
    suspend fun deleteForProject(idPT: Long)

    @Query("SELECT * FROM todo_tag_table")
    suspend fun getAllForExport(): List<TodoTagTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(tags: List<TodoTagTable>)

    @Query("DELETE FROM todo_tag_table")
    suspend fun clear()
}
