package com.zaroslikov.todo.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.todo.data.room.dao.TodoTagDao
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.mapper.toTable
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.TodoDuplicateTagException
import com.zaroslikov.todo.domain.repository.TodoTagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoTagRepositoryImpl @Inject constructor(
    private val database: TodoDatabase,
    private val todoTagDao: TodoTagDao
) : TodoTagRepository {

    override fun getTags(idPT: Long): Flow<List<DomainTodoTag>> {
        return todoTagDao.getTags(idPT).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsert(tag: DomainTodoTag): Long = database.withTransaction {
        val title = tag.title.trim()
        require(title.isNotEmpty()) { "Пустое название тега" }
        val sameTitle = todoTagDao.findByTitle(tag.idPT, title)
        if (tag.id == 0L) {
            // «Новый тег» с уже существующим названием — просто выбираем существующий
            sameTitle?.id ?: todoTagDao.insertTag(tag.toTable().copy(title = title))
        } else {
            if (sameTitle != null && sameTitle.id != tag.id) throw TodoDuplicateTagException(sameTitle.id)
            todoTagDao.updateTag(tag.id, title, tag.colorIndex)
            tag.id
        }
    }

    override suspend fun delete(id: Long) {
        todoTagDao.deleteTag(id)
    }
}
