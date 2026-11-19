package com.zaroslikov.todo.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.mapper.toTable
import com.zaroslikov.todo.domain.model.DomainTodoBackup
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.normalizeRepeat
import com.zaroslikov.todo.domain.model.normalizeTodoAssignees
import com.zaroslikov.todo.domain.model.trimmed
import com.zaroslikov.todo.domain.repository.TodoBackupRepository
import javax.inject.Inject

class TodoBackupRepositoryImpl @Inject constructor(
    private val database: TodoDatabase
) : TodoBackupRepository {

    override suspend fun export(): DomainTodoBackup = database.withTransaction {
        DomainTodoBackup(
            assignees = database.todoAssigneeDao().getAllForExport().map { it.toDomain() },
            lists = database.todoListDao().getAllForExport().map { it.toDomain() },
            sections = database.todoSectionDao().getAllForExport().map { it.toDomain() },
            statuses = database.todoStatusDao().getAllForExport().map { it.toDomain() },
            tasks = database.todoTaskDao().getAllForExport().map { it.toDomain() },
            tags = database.todoTagDao().getAllForExport().map { it.toDomain() },
            taskTags = database.todoTaskDao().getAllTaskTagsForExport().map { it.toDomain() },
            links = database.todoLinkDao().getAllForExport().map { it.toDomain() },
            comments = database.todoCommentDao().getAllForExport().map { it.toDomain() }
        )
    }

    /**
     * Бэкап — внешний файл, ему не доверяем (см. [sanitizedForImport]). Задачам, чей статус
     * не уцелел или из другого проекта, ставится «В планах» их проекта; недостающие
     * дефолтные статусы создаются здесь же.
     */
    override suspend fun import(backup: DomainTodoBackup, projectIds: Set<Long>) {
        val clean = backup.sanitizedForImport(projectIds)
        val listProject = clean.lists.associate { it.id to it.idPT }
        val statusProject = clean.statuses.associate { it.id to it.idPT }
        val markedProjects = clean.lists.filter { it.isLastTaskDestination }.map { it.idPT }.toSet()
        // Старые бэкапы не содержат метку. Берём последний сохранившийся id задачи проекта.
        val inferredDestinations = clean.tasks.groupingBy { listProject.getValue(it.listId) }
            .reduce { _, latest, task -> if (task.id > latest.id) task else latest }
        database.withTransaction {
            clearTables()
            database.todoListDao().insertAllForImport(clean.lists.map { list ->
                list.toTable().copy(
                    isLastTaskDestination = list.isLastTaskDestination ||
                        (list.idPT !in markedProjects && inferredDestinations[list.idPT]?.listId == list.id)
                )
            })
            // Подсписки раньше задач — внешний ключ section_id
            database.todoSectionDao().insertAllForImport(clean.sections.map { it.toTable() })
            database.todoStatusDao().insertAllForImport(clean.statuses.map { it.toTable() })

            val projectsWithTasks = clean.tasks.map { listProject.getValue(it.listId) }.toSet()
            val plannedByProject = projectsWithTasks.associateWith { idPT ->
                database.todoStatusDao().insertMissingDefaults(idPT)
                database.todoStatusDao().getDefault(idPT, TodoDefaultStatus.PLANNED)?.id
                    ?: error("Нет статуса «В планах»")
            }
            val fixStatus: (DomainTodoTask) -> DomainTodoTask = { task ->
                val idPT = listProject.getValue(task.listId)
                if (statusProject[task.statusId] == idPT) task
                else task.copy(statusId = plannedByProject.getValue(idPT), completedAt = null)
            }
            // Родители раньше подзадач — внешний ключ parent_id
            val (topLevel, subtasks) = clean.tasks.partition { it.parentId == null }
            database.todoTaskDao().insertAllForImport(topLevel.map { fixStatus(it).toTable() })
            database.todoTaskDao().insertAllForImport(subtasks.map { fixStatus(it).toTable() })

            database.todoTagDao().insertAllForImport(clean.tags.map { it.toTable() })
            database.todoTaskDao().insertTaskTags(clean.taskTags.map { it.toTable() })
            database.todoLinkDao().insertAllForImport(clean.links.map { it.toTable() })
            database.todoCommentDao().insertAllForImport(clean.comments.map { it.toTable() })
            database.todoAssigneeDao().insertNames(clean.assignees.map { it.toTable() })
        }
    }

    override suspend fun deleteForProject(idPT: Long) {
        database.withTransaction {
            database.todoListDao().deleteForProject(idPT)
            database.todoStatusDao().deleteForProject(idPT)
            database.todoTagDao().deleteForProject(idPT)
            database.todoAssigneeDao().deleteForProject(idPT)
        }
    }

    override suspend fun clearAll() {
        database.withTransaction { clearTables() }
    }

    // Порядок: сначала дочерние таблицы, потом задачи, подсписки, теги, статусы и списки
    private suspend fun clearTables() {
        database.todoAssigneeDao().clear()
        database.todoCommentDao().clear()
        database.todoLinkDao().clear()
        database.todoTaskDao().clearTaskTags()
        database.todoTaskDao().clear()
        database.todoSectionDao().clear()
        database.todoTagDao().clear()
        database.todoStatusDao().clear()
        database.todoListDao().clear()
    }
}

/**
 * Чистка недоверенного бэкапа: строки чужих проектов, дубликаты id и уникальных ключей,
 * висячие ссылки отбрасываются, а не валят импорт по ограничениям БД.
 * - строки с `id <= 0` отбрасываются: при вставке Room выдал бы им новый id и ссылки разошлись бы;
 * - статусы: не больше одного дефолтного каждого вида на проект, `isDone` дефолтного — по коду;
 * - подсписки: только уцелевших списков;
 * - задачи: только из уцелевших списков; подзадача — только у задачи верхнего уровня того же списка;
 *   подсписок задачи — только уцелевший подсписок её же списка, у подзадачи — никакого;
 *   `completedAt` — по `isDone` статуса (null у незавершённых, `completedAt ?: createdAt` у завершённых);
 * - теги: пустые названия отбрасываются, дубликаты `(idPT, title)` сливаются в первый тег;
 * - теги задач, связи: обе стороны уцелели и из одного проекта; связь с собой и повтор пары
 *   в любую сторону отбрасываются.
 * Чужой или отсутствующий статус задачи здесь не исправляется — это делает импорт, ему нужна БД.
 */
internal fun DomainTodoBackup.sanitizedForImport(projectIds: Set<Long>): DomainTodoBackup {
    val markedProjects = mutableSetOf<Long>()
    val lists = lists.filter { it.id > 0 && it.idPT in projectIds }.distinctBy { it.id }.map { list ->
        if (list.isLastTaskDestination && !markedProjects.add(list.idPT)) list.copy(isLastTaskDestination = false)
        else list
    }
    val listProject = lists.associate { it.id to it.idPT }

    val sections = sections.filter { it.id > 0 && it.listId in listProject }.distinctBy { it.id }
    val sectionList = sections.associate { it.id to it.listId }

    val statuses = statuses
        .filter { it.id > 0 && it.idPT in projectIds }
        .distinctBy { it.id }
        .distinctBy { status -> status.defaultCode?.let { "default:${status.idPT}:${it.code}" } ?: "id:${status.id}" }
        .map { status -> status.defaultCode?.let { status.copy(isDone = it.isDone) } ?: status }
    val doneStatusIds = statuses.filter { it.isDone }.map { it.id }.toSet()

    val candidates = tasks.filter { it.id > 0 && it.listId in listProject }.distinctBy { it.id }
    val topLevel = candidates.filter { it.parentId == null }
    val topListById = topLevel.associate { it.id to it.listId }
    val subtasks = candidates.filter { it.parentId != null && topListById[it.parentId] == it.listId }
    val tasks = (topLevel + subtasks).map { original ->
        val task = original.normalizeRepeat()
        task.copy(
            assignees = normalizeTodoAssignees(task.assignees),
            sectionId = task.sectionId?.takeIf { task.parentId == null && sectionList[it] == task.listId },
            completedAt = if (task.statusId in doneStatusIds) task.completedAt ?: task.createdAt else null
        )
    }
    val taskProject = tasks.associate { it.id to listProject.getValue(it.listId) }
    // Старые бэкапы не содержат справочника — восстанавливаем его из назначений задач.
    val assignees = (assignees.filter { it.idPT in projectIds }.map {
        it.copy(name = it.name.trim(), profile = it.profile.trimmed())
    } +
        tasks.flatMap { task -> task.assignees.map { DomainTodoAssignee(listProject.getValue(task.listId), it) } })
        .filter { it.name.isNotEmpty() }
        .distinctBy { it.idPT to it.name.lowercase() }

    val allTags = tags
        .filter { it.id > 0 && it.idPT in projectIds }
        .map { it.copy(title = it.title.trim()) }
        .filter { it.title.isNotEmpty() }
        .distinctBy { it.id }
    val tags = allTags.distinctBy { it.idPT to it.title }
    // Тег-дубль названия отбрасывается, а его задачи получают оставшийся тег
    val keptTagId = tags.associate { (it.idPT to it.title) to it.id }
    val tagReplacement = allTags.associate { it.id to keptTagId.getValue(it.idPT to it.title) }
    val tagProject = tags.associate { it.id to it.idPT }

    val taskTags = taskTags
        .mapNotNull { taskTag -> tagReplacement[taskTag.tagId]?.let { taskTag.copy(tagId = it) } }
        .filter { tagProject[it.tagId] == taskProject[it.taskId] && it.taskId in taskProject }
        .distinct()
    val links = links
        .filter { it.id > 0 && it.taskId != it.linkedTaskId }
        .filter { taskProject[it.taskId] != null && taskProject[it.taskId] == taskProject[it.linkedTaskId] }
        .distinctBy { it.id }
        .distinctBy { minOf(it.taskId, it.linkedTaskId) to maxOf(it.taskId, it.linkedTaskId) }
    val comments = comments.filter { it.id > 0 && it.taskId in taskProject }.distinctBy { it.id }

    return DomainTodoBackup(
        assignees = assignees,
        lists = lists,
        sections = sections,
        statuses = statuses,
        tasks = tasks,
        tags = tags,
        taskTags = taskTags,
        links = links,
        comments = comments
    )
}
