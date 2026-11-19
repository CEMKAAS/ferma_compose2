package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.TodoLinkType
import kotlinx.coroutines.flow.Flow

interface TodoLinkRepository {

    /** Связи задачи в обе стороны; [DomainTodoLinkedTask.isOutgoing] — с какой она стороны. */
    fun getLinks(taskId: Long): Flow<List<DomainTodoLinkedTask>>

    /** Связи с точки зрения каждой задачи списка. Внутренняя связь видна с обеих сторон. */
    fun getListLinks(listId: Long): Flow<List<DomainTodoLinkedTask>>

    /**
     * Связь создаётся, только если задачи разные, из одного проекта и ещё не связаны ни в
     * одну сторону. Возвращает `true`, если связь создана.
     */
    suspend fun addLink(taskId: Long, linkedTaskId: Long, type: TodoLinkType): Boolean

    suspend fun deleteLink(id: Long)
}
