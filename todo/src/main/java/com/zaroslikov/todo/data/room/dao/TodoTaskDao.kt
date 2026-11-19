package com.zaroslikov.todo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zaroslikov.todo.data.room.dto.TodoProjectTaskItemDto
import com.zaroslikov.todo.data.room.dto.TodoTaskAssigneesDto
import com.zaroslikov.todo.data.room.dto.TodoTaskItemDto
import com.zaroslikov.todo.data.room.dto.TodoTaskTagDto
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.data.room.table.TodoTaskTagTable
import kotlinx.coroutines.flow.Flow

// Задача со статусом и всеми счётчиками одним запросом — без N+1 на каждую карточку
private const val TASK_ITEM_COLUMNS = """
    t.*,
        st.id AS st_id, st.idPT AS st_idPT, st.title AS st_title, st.color_index AS st_color_index,
        st.position AS st_position, st.default_code AS st_default_code, st.is_done AS st_is_done,
        (SELECT COUNT(*) FROM todo_task_table s WHERE s.parent_id = t.id) AS subtask_count,
        (SELECT COUNT(*) FROM todo_task_table s
            JOIN todo_status_table ss ON ss.id = s.status_id
            WHERE s.parent_id = t.id AND ss.is_done = 1) AS subtask_done_count,
        (SELECT COUNT(*) FROM todo_comment_table c WHERE c.task_id = t.id) AS comment_count,
        (SELECT COUNT(*) FROM todo_task_link_table k
            WHERE k.task_id = t.id OR k.linked_task_id = t.id) AS link_count,
        (SELECT COUNT(*) FROM todo_task_link_table k
            WHERE k.task_id = t.id AND k.type = 'blocks') AS blocking_count,
        (SELECT COUNT(*) FROM todo_task_link_table k
            WHERE k.linked_task_id = t.id AND k.type = 'blocks') AS blocked_by_count
"""

private const val TASK_ITEM_SELECT = "SELECT " + TASK_ITEM_COLUMNS + """
    FROM todo_task_table t
    LEFT JOIN todo_status_table st ON st.id = t.status_id
"""

private const val TASK_TAG_SELECT = """
    SELECT tt.task_id AS task_id, g.*
    FROM todo_task_tag_table tt
    JOIN todo_tag_table g ON g.id = tt.tag_id
    JOIN todo_task_table t ON t.id = tt.task_id
"""

/**
 * REPLACE и `@Upsert` для задач запрещены: REPLACE удаляет строку и каскадом сносит
 * подзадачи, теги, связи и комментарии. Существующая задача меняется через `@Update`.
 */
@Dao
interface TodoTaskDao {

    @Query("UPDATE todo_task_table SET assignees = :assignees WHERE id = :taskId")
    suspend fun updateAssignees(taskId: Long, assignees: List<String>)

    @Query("""
        SELECT t.id, t.assignees FROM todo_task_table t
        JOIN todo_list_table l ON l.id = t.list_id
        WHERE l.idPT = :idPT AND t.assignees != '[]'
    """)
    suspend fun getProjectAssignments(idPT: Long): List<TodoTaskAssigneesDto>

    @Query(TASK_ITEM_SELECT + "WHERE t.list_id = :listId AND t.parent_id IS NULL ORDER BY t.position, t.id")
    fun getTaskItems(listId: Long): Flow<List<TodoTaskItemDto>>

    /** Все задачи списка вместе с подзадачами; дерево собирает UI. */
    @Query(TASK_ITEM_SELECT + "WHERE t.list_id = :listId ORDER BY t.position, t.id")
    fun getAllTaskItems(listId: Long): Flow<List<TodoTaskItemDto>>

    @Query(TASK_ITEM_SELECT + "WHERE t.parent_id = :parentId ORDER BY t.position, t.id")
    fun getSubtaskItems(parentId: Long): Flow<List<TodoTaskItemDto>>

    @Query(TASK_ITEM_SELECT + "WHERE t.id = :id")
    fun getTaskItem(id: Long): Flow<TodoTaskItemDto?>

    /**
     * Все задачи и подзадачи проекта со счётчиками, названием, цветом и значком списка и названием
     * родителя — для поиска, плиток и умных списков главного экрана. По позиции списка, затем задачи.
     */
    @Query(
        "SELECT " + TASK_ITEM_COLUMNS + """,
            l.title AS list_title, l.color_index AS list_color_index, l.icon AS list_icon,
            p.title AS parent_title
        FROM todo_task_table t
        JOIN todo_list_table l ON l.id = t.list_id
        LEFT JOIN todo_task_table p ON p.id = t.parent_id
        LEFT JOIN todo_status_table st ON st.id = t.status_id
        WHERE l.idPT = :idPT
        ORDER BY l.position, l.id, t.position, t.id
        """
    )
    fun getProjectTaskItems(idPT: Long): Flow<List<TodoProjectTaskItemDto>>

    @Query(
        TASK_TAG_SELECT + """
        JOIN todo_list_table l ON l.id = t.list_id
        WHERE l.idPT = :idPT ORDER BY g.title COLLATE NOCASE
        """
    )
    fun getTagsForProject(idPT: Long): Flow<List<TodoTaskTagDto>>

    @Query(TASK_TAG_SELECT + "WHERE t.list_id = :listId AND t.parent_id IS NULL ORDER BY g.title COLLATE NOCASE")
    fun getTagsForList(listId: Long): Flow<List<TodoTaskTagDto>>

    @Query(TASK_TAG_SELECT + "WHERE t.list_id = :listId ORDER BY g.title COLLATE NOCASE")
    fun getAllTagsForList(listId: Long): Flow<List<TodoTaskTagDto>>

    @Query(TASK_TAG_SELECT + "WHERE t.parent_id = :parentId ORDER BY g.title COLLATE NOCASE")
    fun getTagsForSubtasks(parentId: Long): Flow<List<TodoTaskTagDto>>

    @Query(TASK_TAG_SELECT + "WHERE t.id = :id ORDER BY g.title COLLATE NOCASE")
    fun getTagsForTask(id: Long): Flow<List<TodoTaskTagDto>>

    @Query("SELECT tag_id FROM todo_task_tag_table WHERE task_id = :taskId")
    suspend fun getTagIds(taskId: Long): List<Long>

    @Query("SELECT * FROM todo_task_table WHERE id = :id")
    suspend fun getTask(id: Long): TodoTaskTable?

    /** Компонента связей обоих типов и направлений; UNION останавливает обход циклов. */
    @Query(
        """
        WITH RECURSIVE connected(id) AS (
            SELECT :taskId
            UNION
            SELECT t.id FROM todo_task_link_table k
            JOIN connected c ON k.task_id = c.id OR k.linked_task_id = c.id
            JOIN todo_task_table t ON t.id = CASE WHEN k.task_id = c.id THEN k.linked_task_id ELSE k.task_id END
            JOIN todo_list_table l ON l.id = t.list_id
            WHERE l.idPT = :idPT
        )
        SELECT t.* FROM todo_task_table t
        JOIN connected c ON c.id = t.id
        WHERE t.id != :taskId
        """
    )
    suspend fun getConnectedTasks(taskId: Long, idPT: Long): List<TodoTaskTable>

    @Query("SELECT * FROM todo_task_table WHERE id = :id OR parent_id = :id")
    suspend fun getDeletionTasks(id: Long): List<TodoTaskTable>

    @Query("SELECT * FROM todo_task_tag_table WHERE task_id IN (:ids)")
    suspend fun getDeletionTags(ids: List<Long>): List<TodoTaskTagTable>

    @Query("SELECT * FROM todo_comment_table WHERE task_id IN (:ids)")
    suspend fun getDeletionComments(ids: List<Long>): List<com.zaroslikov.todo.data.room.table.TodoCommentTable>

    @Query("SELECT * FROM todo_task_link_table WHERE task_id IN (:ids) OR linked_task_id IN (:ids)")
    suspend fun getDeletionLinks(ids: List<Long>): List<com.zaroslikov.todo.data.room.table.TodoTaskLinkTable>

    /** `IS` сравнивает и NULL: для задач верхнего уровня [parentId] = null. */
    @Query("SELECT MAX(position) FROM todo_task_table WHERE list_id = :listId AND parent_id IS :parentId")
    suspend fun getMaxPosition(listId: Long, parentId: Long?): Int?

    /** Проект задачи — через её список. */
    @Query("SELECT l.idPT FROM todo_task_table t JOIN todo_list_table l ON l.id = t.list_id WHERE t.id = :taskId")
    suspend fun getProjectId(taskId: Long): Long?

    @Query(
        """
        SELECT t.* FROM todo_task_table t
        JOIN todo_list_table l ON l.id = t.list_id
        WHERE l.idPT = :idPT
        ORDER BY l.position, l.id, t.position, t.id
        """
    )
    fun getProjectTasks(idPT: Long): Flow<List<TodoTaskTable>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: TodoTaskTable): Long

    @Update
    suspend fun updateTask(task: TodoTaskTable)

    @Query("UPDATE todo_task_table SET status_id = :statusId, completed_at = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, statusId: Long, completedAt: String?)

    @Query("UPDATE todo_task_table SET section_id = :sectionId, position = :position WHERE id = :id")
    suspend fun updateSection(id: Long, sectionId: Long?, position: Int)

    @Query("UPDATE todo_task_table SET list_id = :listId, section_id = NULL WHERE id = :taskId OR parent_id = :taskId")
    suspend fun updateListWithSubtasks(taskId: Long, listId: Long)

    @Query("UPDATE todo_task_table SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)

    /** Задачи верхнего уровня подсписка по порядку; `IS` сравнивает и NULL («без подсписка»). */
    @Query(
        """
        SELECT id FROM todo_task_table
        WHERE list_id = :listId AND parent_id IS NULL AND section_id IS :sectionId
        ORDER BY position, id
        """
    )
    suspend fun getTopLevelIds(listId: Long, sectionId: Long?): List<Long>

    @Query("SELECT id FROM todo_task_table WHERE parent_id = :parentId ORDER BY position, id")
    suspend fun getSubtaskIds(parentId: Long): List<Long>

    @Query("UPDATE todo_task_table SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String)

    @Query("UPDATE todo_task_table SET title = :title, note = :note WHERE id = :id")
    suspend fun updateText(id: Long, title: String, note: String)

    /** Завершённые задачи верхнего уровня списка; их подзадачи удаляются каскадом. */
    @Query(
        """
        DELETE FROM todo_task_table
        WHERE list_id = :listId AND parent_id IS NULL
            AND status_id IN (SELECT id FROM todo_status_table WHERE is_done = 1)
        """
    )
    suspend fun deleteCompletedTasks(listId: Long)

    /** Завершённые подзадачи оставшихся в списке задач — вызывается после [deleteCompletedTasks]. */
    @Query(
        """
        DELETE FROM todo_task_table
        WHERE list_id = :listId AND parent_id IS NOT NULL
            AND status_id IN (SELECT id FROM todo_status_table WHERE is_done = 1)
        """
    )
    suspend fun deleteCompletedSubtasks(listId: Long)

    /** Задачи удалённого статуса переходят в [toStatusId]; он незавершающий — `completed_at` сбрасывается. */
    @Query("UPDATE todo_task_table SET status_id = :toStatusId, completed_at = NULL WHERE status_id = :fromStatusId")
    suspend fun moveToStatus(fromStatusId: Long, toStatusId: Long)

    @Query("DELETE FROM todo_task_table WHERE id = :id")
    suspend fun deleteTask(id: Long)

    @Query("DELETE FROM todo_task_tag_table WHERE task_id = :taskId")
    suspend fun deleteTaskTags(taskId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTags(taskTags: List<TodoTaskTagTable>)

    @Query("SELECT * FROM todo_task_table")
    suspend fun getAllForExport(): List<TodoTaskTable>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAllForImport(tasks: List<TodoTaskTable>)

    @Query("DELETE FROM todo_task_table")
    suspend fun clear()

    @Query("SELECT * FROM todo_task_tag_table")
    suspend fun getAllTaskTagsForExport(): List<TodoTaskTagTable>

    @Query("DELETE FROM todo_task_tag_table")
    suspend fun clearTaskTags()
}
