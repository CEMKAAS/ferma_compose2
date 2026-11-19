package com.zaroslikov.todo.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.todo.data.room.dao.TodoLinkDao
import com.zaroslikov.todo.data.room.dao.TodoTaskDao
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.repository.TodoLinkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoLinkRepositoryImpl @Inject constructor(
    private val database: TodoDatabase,
    private val todoLinkDao: TodoLinkDao,
    private val todoTaskDao: TodoTaskDao
) : TodoLinkRepository {

    override fun getLinks(taskId: Long): Flow<List<DomainTodoLinkedTask>> {
        return todoLinkDao.getLinks(taskId).map { list -> list.map { it.toDomain() } }
    }

    override fun getListLinks(listId: Long): Flow<List<DomainTodoLinkedTask>> =
        todoLinkDao.getListLinks(listId)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun addLink(taskId: Long, linkedTaskId: Long, type: TodoLinkType): Boolean =
        database.withTransaction {
            if (taskId == linkedTaskId) return@withTransaction false
            val project = todoTaskDao.getProjectId(taskId) ?: return@withTransaction false
            if (todoTaskDao.getProjectId(linkedTaskId) != project) return@withTransaction false
            // Уникальный индекс ловит только прямую пару — обратную проверяем сами
            if (todoLinkDao.exists(taskId, linkedTaskId)) return@withTransaction false
            todoLinkDao.insertLink(
                TodoTaskLinkTable(taskId = taskId, linkedTaskId = linkedTaskId, type = type)
            ) != -1L
        }

    override suspend fun deleteLink(id: Long) {
        todoLinkDao.deleteLink(id)
    }
}
