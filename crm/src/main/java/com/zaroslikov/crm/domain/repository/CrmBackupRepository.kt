package com.zaroslikov.crm.domain.repository

import com.zaroslikov.crm.domain.model.DomainCrmBackup

interface CrmBackupRepository {

    /** Все четыре таблицы `crm_database`. */
    suspend fun export(): DomainCrmBackup

    /**
     * Очищает CRM-таблицы и записывает [backup] в одной транзакции. Берутся только строки
     * проектов из [projectIds] (проекты того же бэкапа); дубликаты и висячие ссылки отбрасываются.
     */
    suspend fun import(backup: DomainCrmBackup, projectIds: Set<Long>)

    /** Удаляет клиентов и задачи проекта; контакты и взаимодействия уходят каскадом. */
    suspend fun deleteForProject(idPT: Long)

    suspend fun clearAll()
}
