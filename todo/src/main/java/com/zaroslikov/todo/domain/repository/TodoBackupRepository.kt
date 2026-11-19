package com.zaroslikov.todo.domain.repository

import com.zaroslikov.todo.domain.model.DomainTodoBackup

interface TodoBackupRepository {

    /** Все таблицы `todo_database`. */
    suspend fun export(): DomainTodoBackup

    /**
     * Очищает таблицы задач и записывает [backup] в одной транзакции. Берутся только строки
     * проектов из [projectIds] (проекты того же бэкапа); дубликаты и висячие ссылки
     * отбрасываются, задачам с несуществующим статусом ставится «В планах» их проекта.
     */
    suspend fun import(backup: DomainTodoBackup, projectIds: Set<Long>)

    /** Удаляет списки, статусы и теги проекта; задачи и всё связанное уходят каскадом. */
    suspend fun deleteForProject(idPT: Long)

    suspend fun clearAll()
}
