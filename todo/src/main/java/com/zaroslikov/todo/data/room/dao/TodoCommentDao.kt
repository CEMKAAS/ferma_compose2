package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.todo.data.room.table.TodoCommentTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoCommentDao {

    @Query("SELECT * FROM todo_comment_table WHERE task_id = :taskId ORDER BY created_at, id")
    fun getComments(taskId: Long): Flow<List<TodoCommentTable>>

    @Query(
        """
        SELECT c.*
        FROM todo_task_table t
        JOIN todo_comment_table c ON c.task_id = t.id
        WHERE t.list_id = :listId
        ORDER BY c.id
        """
    )
    fun getListComments(listId: Long): Flow<List<TodoCommentTable>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertComment(comment: TodoCommentTable): Long

    @Query("UPDATE todo_comment_table SET text = :text, edited_at = :editedAt WHERE id = :id")
    suspend fun updateText(id: Long, text: String, editedAt: String)

    @Query("DELETE FROM todo_comment_table WHERE id = :id")
    suspend fun deleteComment(id: Long)

    @Query("SELECT * FROM todo_comment_table")
    suspend fun getAllForExport(): List<TodoCommentTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(comments: List<TodoCommentTable>)

    @Query("DELETE FROM todo_comment_table")
    suspend fun clear()
}
