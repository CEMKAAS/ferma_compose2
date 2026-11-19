package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.todo.data.room.dto.TodoListSummaryDto
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.domain.model.TodoListViewMode
import kotlinx.coroutines.flow.Flow

/**
 * REPLACE и `@Upsert` для списков запрещены: REPLACE удаляет строку и каскадом
 * сносит все задачи списка.
 */
@Dao
interface TodoListDao {

    /**
     * Счётчики — только по задачам верхнего уровня. Просрочка считается так же, как
     * `isTodoOverdue`: [today] и [nowTime] передаёт репозиторий, перезапуская запрос раз в минуту.
     */
    @Query(
        """
        SELECT l.*,
            (SELECT COUNT(*) FROM todo_task_table t
                WHERE t.list_id = l.id AND t.parent_id IS NULL) AS total_count,
            (SELECT COUNT(*) FROM todo_task_table t
                JOIN todo_status_table s ON s.id = t.status_id
                WHERE t.list_id = l.id AND t.parent_id IS NULL AND s.is_done = 1) AS done_count,
            (SELECT COUNT(*) FROM todo_task_table t
                LEFT JOIN todo_status_table s ON s.id = t.status_id
                WHERE t.list_id = l.id AND t.parent_id IS NULL AND COALESCE(s.is_done, 0) = 0
                    AND t.due_date IS NOT NULL
                    AND (t.due_date < :today
                        OR (t.due_date = :today AND t.due_time IS NOT NULL AND t.due_time < :nowTime))
            ) AS overdue_count
        FROM todo_list_table l
        WHERE l.idPT = :idPT
        ORDER BY l.position, l.id
        """
    )
    fun getSummaries(idPT: Long, today: String, nowTime: String): Flow<List<TodoListSummaryDto>>

    @Query("SELECT * FROM todo_list_table WHERE id = :id")
    fun getList(id: Long): Flow<TodoListTable?>

    @Query("SELECT idPT FROM todo_list_table WHERE id = :id")
    suspend fun getProjectId(id: Long): Long?

    @Query("SELECT MAX(position) FROM todo_list_table WHERE idPT = :idPT")
    suspend fun getMaxPosition(idPT: Long): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertList(list: TodoListTable): Long

    /** Правка из шторки: закрепление, позиция и вид не меняются. */
    @Query("UPDATE todo_list_table SET title = :title, color_index = :colorIndex, icon = :icon WHERE id = :id")
    suspend fun updateList(id: Long, title: String, colorIndex: Int, icon: TodoListIcon)

    @Query("UPDATE todo_list_table SET is_pinned = :pinned WHERE id = :id")
    suspend fun updatePinned(id: Long, pinned: Boolean)

    @Query("SELECT id FROM todo_list_table WHERE idPT = :idPT ORDER BY position, id")
    suspend fun getListIds(idPT: Long): List<Long>

    @Query(
        """
        SELECT l.id FROM todo_list_table l WHERE l.idPT = :idPT
        ORDER BY l.is_last_task_destination DESC,
            (SELECT MAX(t.id) FROM todo_task_table t WHERE t.list_id = l.id) DESC,
            l.position, l.id
        LIMIT 1
        """
    )
    suspend fun getQuickAddDestination(idPT: Long): Long?

    @Query(
        """
        UPDATE todo_list_table SET is_last_task_destination = (id = :listId)
        WHERE idPT = :idPT AND (is_last_task_destination != 0 OR id = :listId)
        """
    )
    suspend fun setLastTaskDestination(idPT: Long, listId: Long)

    @Query("UPDATE todo_list_table SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)

    @Query("UPDATE todo_list_table SET view_mode = :viewMode WHERE id = :id")
    suspend fun updateViewMode(id: Long, viewMode: TodoListViewMode)

    @Query("DELETE FROM todo_list_table WHERE id = :id")
    suspend fun deleteList(id: Long)

    /** Задачи удаляются каскадом по внешнему ключу. */
    @Query("DELETE FROM todo_list_table WHERE idPT = :idPT")
    suspend fun deleteForProject(idPT: Long)

    @Query("SELECT * FROM todo_list_table")
    suspend fun getAllForExport(): List<TodoListTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(lists: List<TodoListTable>)

    @Query("DELETE FROM todo_list_table")
    suspend fun clear()
}
