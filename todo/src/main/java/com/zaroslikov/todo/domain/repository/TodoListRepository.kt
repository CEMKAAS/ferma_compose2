package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoListSummary
import com.zaroslikov.todo.domain.model.TodoListViewMode
import kotlinx.coroutines.flow.Flow

interface TodoListRepository {

    /** Списки проекта по позиции со счётчиками задач верхнего уровня; просрочка пересчитывается раз в минуту. */
    fun getLists(idPT: Long): Flow<List<DomainTodoListSummary>>

    fun getList(id: Long): Flow<DomainTodoList?>

    /**
     * `id = 0` — новый список в конец (пустой `createdAt` = сейчас); иначе меняются только
     * название, цвет и значок (закрепление, позиция и вид остаются). Возвращает id списка.
     */
    suspend fun upsert(list: DomainTodoList): Long

    /** Закрепить список плиткой над «Моими списками» или открепить. */
    suspend fun setPinned(id: Long, pinned: Boolean)

    /**
     * Новый порядок списков проекта: позиции подряд с нуля по [orderedIds], не упомянутые —
     * следом в прежнем порядке (`todoOrdered`).
     */
    suspend fun reorderLists(idPT: Long, orderedIds: List<Long>)

    /** Вид экрана списка: строки или столбцы подсписков. */
    suspend fun setViewMode(id: Long, viewMode: TodoListViewMode)

    /** Задачи списка, их теги, связи и комментарии удаляются каскадом. */
    suspend fun delete(id: Long)
}
