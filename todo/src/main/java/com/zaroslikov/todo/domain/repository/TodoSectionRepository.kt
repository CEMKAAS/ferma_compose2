package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoSection
import kotlinx.coroutines.flow.Flow

interface TodoSectionRepository {

    /** Подсписки списка по позиции. */
    fun getSections(listId: Long): Flow<List<DomainTodoSection>>

    /** Новый подсписок в конец списка; название обрезается, пустое — ошибка. Возвращает id. */
    suspend fun add(listId: Long, title: String): Long

    /** Пустое после обрезки название игнорируется. */
    suspend fun rename(id: Long, title: String)

    /** Задачи подсписка остаются в списке «без подсписка» (`ON DELETE SET NULL`). */
    suspend fun delete(id: Long)

    suspend fun setCollapsed(id: Long, collapsed: Boolean)

    /**
     * Порядок подсписков списка [listId] в одной транзакции: позиции по порядку [orderedIds]
     * подряд с нуля; подсписки, которых нет в [orderedIds], идут следом в прежнем порядке,
     * подсписки чужого списка игнорируются.
     */
    suspend fun reorderSections(listId: Long, orderedIds: List<Long>)
}
