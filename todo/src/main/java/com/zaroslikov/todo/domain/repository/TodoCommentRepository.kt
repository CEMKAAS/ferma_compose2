package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoComment
import kotlinx.coroutines.flow.Flow

interface TodoCommentRepository {

    /** Комментарии задачи от старых к новым. */
    fun getComments(taskId: Long): Flow<List<DomainTodoComment>>

    /** Комментарии всех задач и подзадач списка. */
    fun getListComments(listId: Long): Flow<List<DomainTodoComment>>

    /** Дата создания — сейчас. Возвращает id комментария. */
    suspend fun add(taskId: Long, text: String): Long

    /** Меняет текст и ставит дату изменения. */
    suspend fun update(id: Long, text: String)

    suspend fun delete(id: Long)
}
