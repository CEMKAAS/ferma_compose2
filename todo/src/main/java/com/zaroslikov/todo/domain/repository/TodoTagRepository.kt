package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoTag
import kotlinx.coroutines.flow.Flow

interface TodoTagRepository {

    /** Теги проекта по названию. */
    fun getTags(idPT: Long): Flow<List<DomainTodoTag>>

    /**
     * `id = 0` — новый тег; если тег с таким названием в проекте уже есть, возвращается его id.
     * Переименование в занятое название бросает
     * [com.zaroslikov.todo.domain.model.TodoDuplicateTagException]. Название обрезается.
     */
    suspend fun upsert(tag: DomainTodoTag): Long

    /** Тег снимается со всех задач каскадом. */
    suspend fun delete(id: Long)
}
