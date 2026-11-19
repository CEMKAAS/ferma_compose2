package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoStatus
import kotlinx.coroutines.flow.Flow

interface TodoStatusRepository {

    /** Создаёт недостающие дефолтные статусы проекта; повторный вызов ничего не меняет. */
    suspend fun ensureDefaults(idPT: Long)

    /** Статусы проекта: сначала незавершающие, дальше по позиции. */
    fun getStatuses(idPT: Long): Flow<List<DomainTodoStatus>>

    /**
     * `id = 0` — новый пользовательский статус в конец; у существующего пользовательского
     * меняются название, цвет и признак завершения, у дефолтного — только цвет. Возвращает id.
     */
    suspend fun upsert(status: DomainTodoStatus): Long

    /**
     * Удаляет пользовательский статус, его задачи переходят в «В планах». Дефолтный не
     * удаляется — возвращается `false`.
     */
    suspend fun delete(status: DomainTodoStatus): Boolean
}
