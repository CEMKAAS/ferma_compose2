package com.zaroslikov.todo.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.todo.data.room.dao.TodoSectionDao
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.table.TodoSectionTable
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.todoOrdered
import com.zaroslikov.todo.domain.repository.TodoSectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoSectionRepositoryImpl @Inject constructor(
    private val database: TodoDatabase,
    private val todoSectionDao: TodoSectionDao
) : TodoSectionRepository {

    override fun getSections(listId: Long): Flow<List<DomainTodoSection>> =
        todoSectionDao.getSections(listId).map { list -> list.map { it.toDomain() } }

    override suspend fun add(listId: Long, title: String): Long = database.withTransaction {
        val text = title.trim()
        require(text.isNotEmpty()) { "Пустое название подсписка" }
        val position = (todoSectionDao.getMaxPosition(listId) ?: -1) + 1
        todoSectionDao.insertSection(TodoSectionTable(listId = listId, title = text, position = position))
    }

    override suspend fun rename(id: Long, title: String) {
        val text = title.trim()
        if (text.isNotEmpty()) todoSectionDao.updateTitle(id, text)
    }

    override suspend fun delete(id: Long) {
        todoSectionDao.deleteSection(id)
    }

    override suspend fun setCollapsed(id: Long, collapsed: Boolean) {
        todoSectionDao.updateCollapsed(id, collapsed)
    }

    override suspend fun reorderSections(listId: Long, orderedIds: List<Long>) {
        database.withTransaction {
            val ids = todoSectionDao.getSectionIds(listId)
            todoOrdered(ids, orderedIds).forEachIndexed { position, id -> todoSectionDao.updatePosition(id, position) }
        }
    }
}
