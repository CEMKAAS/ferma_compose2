package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.todo.data.room.table.TodoSectionTable
import kotlinx.coroutines.flow.Flow

/** REPLACE и `@Upsert` запрещены: REPLACE удалил бы строку и сбросил подсписок у её задач. */
@Dao
interface TodoSectionDao {

    @Query("SELECT * FROM todo_section_table WHERE list_id = :listId ORDER BY position, id")
    fun getSections(listId: Long): Flow<List<TodoSectionTable>>

    @Query("SELECT * FROM todo_section_table WHERE id = :id")
    suspend fun getSection(id: Long): TodoSectionTable?

    @Query("SELECT MAX(position) FROM todo_section_table WHERE list_id = :listId")
    suspend fun getMaxPosition(listId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSection(section: TodoSectionTable): Long

    @Query("UPDATE todo_section_table SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("UPDATE todo_section_table SET is_collapsed = :collapsed WHERE id = :id")
    suspend fun updateCollapsed(id: Long, collapsed: Boolean)

    @Query("SELECT id FROM todo_section_table WHERE list_id = :listId ORDER BY position, id")
    suspend fun getSectionIds(listId: Long): List<Long>

    @Query("UPDATE todo_section_table SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)

    /** `section_id` задач подсписка обнуляется внешним ключом (`ON DELETE SET NULL`). */
    @Query("DELETE FROM todo_section_table WHERE id = :id")
    suspend fun deleteSection(id: Long)

    @Query("SELECT * FROM todo_section_table")
    suspend fun getAllForExport(): List<TodoSectionTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(sections: List<TodoSectionTable>)

    @Query("DELETE FROM todo_section_table")
    suspend fun clear()
}
