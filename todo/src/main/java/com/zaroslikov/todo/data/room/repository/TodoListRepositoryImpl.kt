package com.zaroslikov.todo.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.todo.data.room.dao.TodoListDao
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.mapper.toTable
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoListSummary
import com.zaroslikov.todo.domain.model.TodoListViewMode
import com.zaroslikov.todo.domain.model.todoMinuteTicker
import com.zaroslikov.todo.domain.model.todoNowIso
import com.zaroslikov.todo.domain.model.todoNowTime
import com.zaroslikov.todo.domain.model.todoOrdered
import com.zaroslikov.todo.domain.model.todoToday
import com.zaroslikov.todo.domain.repository.TodoListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoListRepositoryImpl @Inject constructor(
    private val database: TodoDatabase,
    private val todoListDao: TodoListDao
) : TodoListRepository {

    // Просрочка зависит от текущей минуты: запрос перезапускается раз в минуту, иначе счётчик замерзает
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getLists(idPT: Long): Flow<List<DomainTodoListSummary>> = todoMinuteTicker()
        .flatMapLatest { now -> todoListDao.getSummaries(idPT, todoToday(now), todoNowTime(now)) }
        .map { list -> list.map { it.toDomain() } }

    override fun getList(id: Long): Flow<DomainTodoList?> {
        return todoListDao.getList(id).map { it?.toDomain() }
    }

    override suspend fun upsert(list: DomainTodoList): Long = database.withTransaction {
        val title = list.title.trim()
        if (list.id == 0L) {
            val position = (todoListDao.getMaxPosition(list.idPT) ?: -1) + 1
            todoListDao.insertList(
                list.toTable().copy(
                    title = title,
                    position = position,
                    createdAt = list.createdAt.ifBlank { todoNowIso() }
                )
            )
        } else {
            todoListDao.updateList(list.id, title, list.colorIndex, list.icon)
            list.id
        }
    }

    override suspend fun setPinned(id: Long, pinned: Boolean) {
        todoListDao.updatePinned(id, pinned)
    }

    override suspend fun reorderLists(idPT: Long, orderedIds: List<Long>) {
        database.withTransaction {
            val ids = todoListDao.getListIds(idPT)
            todoOrdered(ids, orderedIds).forEachIndexed { position, id -> todoListDao.updatePosition(id, position) }
        }
    }

    override suspend fun setViewMode(id: Long, viewMode: TodoListViewMode) {
        todoListDao.updateViewMode(id, viewMode)
    }

    override suspend fun delete(id: Long) {
        todoListDao.deleteList(id)
    }
}
