package com.zaroslikov.todo.data.room.repository

import com.zaroslikov.todo.data.room.dao.TodoCommentDao
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.table.TodoCommentTable
import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.todoNowIso
import com.zaroslikov.todo.domain.repository.TodoCommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoCommentRepositoryImpl @Inject constructor(
    private val todoCommentDao: TodoCommentDao
) : TodoCommentRepository {

    override fun getComments(taskId: Long): Flow<List<DomainTodoComment>> {
        return todoCommentDao.getComments(taskId).map { list -> list.map { it.toDomain() } }
    }

    override fun getListComments(listId: Long): Flow<List<DomainTodoComment>> =
        todoCommentDao.getListComments(listId)
            .distinctUntilChanged()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun add(taskId: Long, text: String): Long {
        return todoCommentDao.insertComment(
            TodoCommentTable(taskId = taskId, text = text.trim(), createdAt = todoNowIso())
        )
    }

    override suspend fun update(id: Long, text: String) {
        todoCommentDao.updateText(id, text.trim(), todoNowIso())
    }

    override suspend fun delete(id: Long) {
        todoCommentDao.deleteComment(id)
    }
}
