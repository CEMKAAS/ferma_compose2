package com.zaroslikov.todo.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.todo.data.room.dao.TodoStatusDao
import com.zaroslikov.todo.data.room.dao.TodoTaskDao
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.mapper.toTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.repository.TodoStatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoStatusRepositoryImpl @Inject constructor(
    private val database: TodoDatabase,
    private val todoStatusDao: TodoStatusDao,
    private val todoTaskDao: TodoTaskDao
) : TodoStatusRepository {

    override suspend fun ensureDefaults(idPT: Long) {
        database.withTransaction { todoStatusDao.insertMissingDefaults(idPT) }
    }

    override fun getStatuses(idPT: Long): Flow<List<DomainTodoStatus>> {
        return todoStatusDao.getStatuses(idPT).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun upsert(status: DomainTodoStatus): Long = database.withTransaction {
        if (status.id == 0L) {
            val position = (todoStatusDao.getMaxPosition(status.idPT) ?: -1) + 1
            // Новый статус всегда пользовательский: дефолтные создаёт только ensureDefaults
            return@withTransaction todoStatusDao.insertStatus(
                status.toTable().copy(title = status.title.trim(), position = position, defaultCode = null)
            )
        }
        val existing = todoStatusDao.getStatus(status.id) ?: return@withTransaction status.id
        // У дефолтного статуса меняется только цвет
        if (existing.defaultCode != null)
            todoStatusDao.updateColor(existing.id, status.colorIndex)
        else
            todoStatusDao.updateStatus(existing.id, status.title.trim(), status.colorIndex, status.isDone)
        existing.id
    }

    override suspend fun delete(status: DomainTodoStatus): Boolean = database.withTransaction {
        val existing = todoStatusDao.getStatus(status.id) ?: return@withTransaction true
        if (existing.defaultCode != null) return@withTransaction false
        todoStatusDao.insertMissingDefaults(existing.idPT)
        val planned = todoStatusDao.getDefault(existing.idPT, TodoDefaultStatus.PLANNED)
            ?: error("Нет статуса «В планах»")
        todoTaskDao.moveToStatus(existing.id, planned.id)
        todoStatusDao.deleteStatus(existing.id)
        true
    }
}

/**
 * Создаёт недостающие дефолтные статусы проекта. Вызывать внутри транзакции; повторный
 * вызов ничего не меняет, а уникальность `(idPT, default_code)` страхует от гонки.
 */
internal suspend fun TodoStatusDao.insertMissingDefaults(idPT: Long) {
    val existing = getDefaultCodes(idPT).toSet()
    val missing = TodoDefaultStatus.entries.filter { it !in existing }
    if (missing.isNotEmpty()) insertIgnore(missing.map { todoDefaultStatusRow(idPT, it) })
}

// Название — запасное: экран показывает строку из ресурсов по default_code.
// Цвета — индексы todoColorPalette: синий, оранжевый, зелёный
internal fun todoDefaultStatusRow(idPT: Long, status: TodoDefaultStatus) = TodoStatusTable(
    idPT = idPT,
    title = when (status) {
        TodoDefaultStatus.PLANNED -> "В планах"
        TodoDefaultStatus.IN_PROGRESS -> "В работе"
        TodoDefaultStatus.DONE -> "Завершённые"
    },
    colorIndex = when (status) {
        TodoDefaultStatus.PLANNED -> 1
        TodoDefaultStatus.IN_PROGRESS -> 3
        TodoDefaultStatus.DONE -> 2
    },
    position = status.position,
    defaultCode = status,
    isDone = status.isDone
)
