package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zaroslikov.todo.data.room.dto.TodoLinkedTaskDto
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoLinkDao {

    /** Обе стороны связи: другая задача — та, что не равна [taskId]. */
    @Query(
        """
        SELECT k.id AS link_id, k.task_id AS link_task_id, k.linked_task_id AS link_linked_task_id,
            k.type AS link_type,
            t.*,
            st.id AS st_id, st.idPT AS st_idPT, st.title AS st_title, st.color_index AS st_color_index,
            st.position AS st_position, st.default_code AS st_default_code, st.is_done AS st_is_done,
            l.title AS list_title, l.icon AS list_icon,
            CASE WHEN k.task_id = :taskId THEN 1 ELSE 0 END AS is_outgoing
        FROM todo_task_link_table k
        JOIN todo_task_table t
            ON t.id = CASE WHEN k.task_id = :taskId THEN k.linked_task_id ELSE k.task_id END
        JOIN todo_list_table l ON l.id = t.list_id
        LEFT JOIN todo_status_table st ON st.id = t.status_id
        WHERE k.task_id = :taskId OR k.linked_task_id = :taskId
        ORDER BY k.id
        """
    )
    fun getLinks(taskId: Long): Flow<List<TodoLinkedTaskDto>>

    @Query(
        """
        SELECT k.id AS link_id, k.task_id AS link_task_id, k.linked_task_id AS link_linked_task_id,
            k.type AS link_type,
            t.*,
            st.id AS st_id, st.idPT AS st_idPT, st.title AS st_title, st.color_index AS st_color_index,
            st.position AS st_position, st.default_code AS st_default_code, st.is_done AS st_is_done,
            l.title AS list_title, l.icon AS list_icon,
            CASE WHEN k.task_id = owner.id THEN 1 ELSE 0 END AS is_outgoing
        FROM todo_task_table owner
        JOIN todo_task_link_table k ON k.task_id = owner.id OR k.linked_task_id = owner.id
        JOIN todo_task_table t
            ON t.id = CASE WHEN k.task_id = owner.id THEN k.linked_task_id ELSE k.task_id END
        JOIN todo_list_table l ON l.id = t.list_id
        LEFT JOIN todo_status_table st ON st.id = t.status_id
        WHERE owner.list_id = :listId
        ORDER BY k.id, is_outgoing DESC
        """
    )
    fun getListLinks(listId: Long): Flow<List<TodoLinkedTaskDto>>

    /** Есть ли связь пары в любую сторону. */
    @Query(
        """
        SELECT EXISTS(SELECT 1 FROM todo_task_link_table
            WHERE (task_id = :firstId AND linked_task_id = :secondId)
                OR (task_id = :secondId AND linked_task_id = :firstId))
        """
    )
    suspend fun exists(firstId: Long, secondId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(link: TodoTaskLinkTable): Long

    @Query("DELETE FROM todo_task_link_table WHERE id = :id")
    suspend fun deleteLink(id: Long)

    @Query("SELECT * FROM todo_task_link_table")
    suspend fun getAllForExport(): List<TodoTaskLinkTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(links: List<TodoTaskLinkTable>)

    @Query("DELETE FROM todo_task_link_table")
    suspend fun clear()
}
