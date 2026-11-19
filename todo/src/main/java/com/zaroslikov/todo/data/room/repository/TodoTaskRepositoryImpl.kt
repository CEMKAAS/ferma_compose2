package com.zaroslikov.todo.data.room.repository

import androidx.room.withTransaction
import com.zaroslikov.todo.data.room.dao.TodoListDao
import com.zaroslikov.todo.data.room.dao.TodoSectionDao
import com.zaroslikov.todo.data.room.dao.TodoStatusDao
import com.zaroslikov.todo.data.room.dao.TodoTagDao
import com.zaroslikov.todo.data.room.dao.TodoTaskDao
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.mapper.toDomainItems
import com.zaroslikov.todo.data.room.mapper.toDomainProjectTasks
import com.zaroslikov.todo.data.room.mapper.toTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.data.room.table.TodoTaskTagTable
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.data.room.table.TodoAssigneeTable
import java.lang.ref.WeakReference
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.todoMoved
import com.zaroslikov.todo.domain.model.todoNowIso
import com.zaroslikov.todo.domain.model.normalizeRepeat
import com.zaroslikov.todo.domain.model.normalizeTodoAssignees
import com.zaroslikov.todo.domain.model.trimmed
import com.zaroslikov.todo.domain.model.nextTodoRepeatDate
import com.zaroslikov.todo.domain.model.todoOrdered
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import com.zaroslikov.todo.domain.repository.TodoTaskDeletion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Списки задач — два запроса (задачи со счётчиками + их теги), склеенные в Kotlin. */
class TodoTaskRepositoryImpl @Inject constructor(
    private val database: TodoDatabase,
    private val todoTaskDao: TodoTaskDao,
    private val todoStatusDao: TodoStatusDao,
    private val todoListDao: TodoListDao,
    private val todoTagDao: TodoTagDao,
    private val todoSectionDao: TodoSectionDao
) : TodoTaskRepository {

    // Снимки живут, пока доступна отмена; слабые ссылки не удерживают их после закрытия снекбара.
    // Следующее удаление сохраняет и связи с уже удалёнными задачами.
    private val deletionLinks = mutableListOf<WeakReference<List<TodoTaskLinkTable>>>()
    private val deletionTasks = mutableListOf<WeakReference<DeletedTasks>>()

    private class DeletedTasks(val projectId: Long, var tasks: List<TodoTaskTable>)

    override fun getTasks(listId: Long): Flow<List<DomainTodoTaskItem>> =
        combine(todoTaskDao.getTaskItems(listId), todoTaskDao.getTagsForList(listId)) { items, tags ->
            items.toDomainItems(tags)
        }

    override fun getAllTasks(listId: Long): Flow<List<DomainTodoTaskItem>> =
        combine(todoTaskDao.getAllTaskItems(listId), todoTaskDao.getAllTagsForList(listId)) { items, tags ->
            items.toDomainItems(tags)
        }

    override fun getSubtasks(parentId: Long): Flow<List<DomainTodoTaskItem>> =
        combine(todoTaskDao.getSubtaskItems(parentId), todoTaskDao.getTagsForSubtasks(parentId)) { items, tags ->
            items.toDomainItems(tags)
        }

    override fun getTask(id: Long): Flow<DomainTodoTaskItem?> =
        combine(todoTaskDao.getTaskItem(id), todoTaskDao.getTagsForTask(id)) { item, tags ->
            item?.toDomain(tags.map { it.tag.toDomain() })
        }

    override suspend fun getTaskTagIds(id: Long): Set<Long> = todoTaskDao.getTagIds(id).toSet()

    override fun getProjectAssignees(idPT: Long): Flow<List<String>> = database.todoAssigneeDao().getNames(idPT)

    override fun getProjectAssigneeProfiles(idPT: Long): Flow<List<DomainTodoAssignee>> =
        database.todoAssigneeDao().getProfiles(idPT).map { profiles -> profiles.map { it.toDomain() } }

    override suspend fun saveProjectAssigneeProfile(
        idPT: Long,
        originalName: String?,
        profile: TodoAssigneeProfile,
        fallbackName: String
    ): Boolean = database.withTransaction {
        val dao = database.todoAssigneeDao()
        val cleanProfile = profile.trimmed()
        val displayName = cleanProfile.displayName
        if (originalName == null) {
            val name = displayName.ifBlank { unusedAssigneeName(idPT, fallbackName.trim()) }
            if (name.isBlank()) return@withTransaction false
            return@withTransaction dao.insertName(DomainTodoAssignee(idPT, name, cleanProfile).toTable()) != -1L
        }

        val oldKey = originalName.trim().lowercase()
        val oldName = dao.getName(idPT, oldKey) ?: return@withTransaction false
        val name = displayName.ifBlank { oldName }
        if (dao.rename(idPT, oldKey, name.lowercase(), name, cleanProfile) == 0) return@withTransaction false
        if (name != oldName) replaceProjectAssignments(idPT, oldKey, name)
        true
    }

    private suspend fun unusedAssigneeName(idPT: Long, baseName: String): String {
        if (baseName.isBlank()) return baseName
        val usedKeys = database.todoAssigneeDao().getNameKeys(idPT).toSet()
        if (baseName.lowercase() !in usedKeys) return baseName
        return generateSequence(2) { it + 1 }
            .map { "$baseName $it" }
            .first { it.lowercase() !in usedKeys }
    }

    override suspend fun deleteProjectAssignee(idPT: Long, name: String) {
        database.withTransaction {
            val nameKey = name.trim().lowercase()
            database.todoAssigneeDao().deleteName(idPT, nameKey)
            replaceProjectAssignments(idPT, nameKey, replacement = null)
        }
    }

    private suspend fun replaceProjectAssignments(idPT: Long, nameKey: String, replacement: String?) {
        fun updateNames(names: List<String>): List<String> {
            val updated = names.mapNotNull { name ->
                if (name.trim().lowercase() == nameKey) replacement else name
            }
            return if (updated == names) names else normalizeTodoAssignees(updated)
        }

        for ((taskId, names) in todoTaskDao.getProjectAssignments(idPT)) {
            val updated = updateNames(names)
            if (updated != names) todoTaskDao.updateAssignees(taskId, updated)
        }
        deletionTasks.removeAll { it.get() == null }
        for (reference in deletionTasks) {
            val snapshot = reference.get()?.takeIf { it.projectId == idPT } ?: continue
            snapshot.tasks = snapshot.tasks.map { it.copy(assignees = updateNames(it.assignees)) }
        }
    }

    override suspend fun setAssignees(taskId: Long, names: List<String>) {
        database.withTransaction {
            val idPT = todoTaskDao.getProjectId(taskId) ?: return@withTransaction
            val normalized = normalizeTodoAssignees(names)
            todoTaskDao.updateAssignees(taskId, normalized)
            rememberAssignees(idPT, normalized)
        }
    }

    private suspend fun rememberAssignees(idPT: Long, names: List<String>) {
        database.todoAssigneeDao().insertNames(normalizeTodoAssignees(names).map {
            TodoAssigneeTable(idPT = idPT, nameKey = it.lowercase(), name = it)
        })
    }

    override suspend fun upsert(task: DomainTodoTask, tagIds: Set<Long>): Long = database.withTransaction {
        val now = todoNowIso()
        val existing = if (task.id == 0L) null else todoTaskDao.getTask(task.id) ?: error("Задача не найдена")
        val parent = if (existing == null) {
            task.parentId?.let { todoTaskDao.getTask(it) ?: error("Родительская задача не найдена") }
        } else null
        check(parent?.parentId == null) { "Подзадачу подзадачи создать нельзя" }
        // Подзадача всегда в списке родителя; список существующей задачи редактором не меняется
        val listId = existing?.listId ?: parent?.listId ?: task.listId
        val idPT = todoListDao.getProjectId(listId) ?: error("Список не найден")
        // Статус чужого проекта или удалённый — «В планах» этого проекта
        val status = todoStatusDao.getStatus(task.statusId)?.takeIf { it.idPT == idPT }
            ?: plannedStatus(idPT)
        val isDone = status.isDone
        val wasDone = existing?.let { todoStatusDao.getStatus(it.statusId)?.isDone } == true
        val id = if (existing == null) {
            val position = (todoTaskDao.getMaxPosition(listId, task.parentId) ?: -1) + 1
            insertManualTask(
                idPT,
                task.normalizeRepeat().toTable().copy(
                    listId = listId,
                    // Подсписок — только у задачи верхнего уровня и только своего списка
                    sectionId = if (task.parentId == null) validSectionId(listId, task.sectionId) else null,
                    title = task.title.trim(),
                    statusId = status.id,
                    position = position,
                    createdAt = task.createdAt.ifBlank { now },
                    completedAt = if (isDone) task.completedAt ?: now else null,
                    recurrenceGenerated = false
                )
            )
        } else {
            // Родитель, позиция и дата создания редактором не меняются
            todoTaskDao.updateTask(
                task.normalizeRepeat().toTable().copy(
                    listId = existing.listId,
                    parentId = existing.parentId,
                    // Подсписок меняет только setSection
                    sectionId = existing.sectionId,
                    title = task.title.trim(),
                    statusId = status.id,
                    position = existing.position,
                    createdAt = existing.createdAt,
                    completedAt = if (isDone) existing.completedAt ?: now else null,
                    // Снимок редактора может быть старее завершения задачи.
                    recurrenceGenerated = existing.recurrenceGenerated
                )
            )
            existing.id
        }
        todoTaskDao.deleteTaskTags(id)
        // Только существующие теги проекта задачи: тег могли удалить на другом экране
        val validTagIds = if (tagIds.isEmpty()) emptyList() else todoTagDao.getExistingIds(idPT, tagIds)
        todoTaskDao.insertTaskTags(validTagIds.map { TodoTaskTagTable(taskId = id, tagId = it) })
        if (isDone && !wasDone) completeLinkedTasks(id, idPT, now)
        rememberAssignees(idPT, task.assignees)
        id
    }

    override fun getProjectTasks(idPT: Long): Flow<List<DomainTodoProjectTask>> =
        combine(todoTaskDao.getProjectTaskItems(idPT), todoTaskDao.getTagsForProject(idPT)) { items, tags ->
            items.toDomainProjectTasks(tags)
        }

    override suspend fun quickAdd(listId: Long, title: String, dueDate: String?, sectionId: Long?, note: String): Long = database.withTransaction {
        val idPT = todoListDao.getProjectId(listId) ?: error("Список не найден")
        insertQuickTask(idPT, listId, title, dueDate, sectionId, note)
    }

    override suspend fun quickAddToProject(idPT: Long, title: String, dueDate: String, defaultListTitle: String, note: String): Long = database.withTransaction {
        val listId = todoListDao.getQuickAddDestination(idPT) ?: todoListDao.insertList(
            TodoListTable(idPT = idPT, title = defaultListTitle, createdAt = todoNowIso())
        )
        insertQuickTask(idPT, listId, title, dueDate, sectionId = null, note = note)
    }

    private suspend fun insertQuickTask(idPT: Long, listId: Long, title: String, dueDate: String?, sectionId: Long?, note: String): Long {
        val text = title.trim()
        require(text.isNotEmpty()) { "Пустое название задачи" }
        val status = plannedStatus(idPT)
        val position = (todoTaskDao.getMaxPosition(listId, null) ?: -1) + 1
        return insertManualTask(
            idPT,
            TodoTaskTable(
                listId = listId,
                title = text,
                note = note.trim(),
                statusId = status.id,
                position = position,
                dueDate = dueDate,
                createdAt = todoNowIso(),
                sectionId = validSectionId(listId, sectionId)
            )
        )
    }

    // В той же транзакции запоминаем список, в котором пользователь создал задачу.
    private suspend fun insertManualTask(idPT: Long, task: TodoTaskTable): Long {
        val taskId = todoTaskDao.insertTask(task)
        todoListDao.setLastTaskDestination(idPT, task.listId)
        return taskId
    }

    override suspend fun setList(taskId: Long, listId: Long) {
        database.withTransaction {
            val task = todoTaskDao.getTask(taskId) ?: return@withTransaction
            if (task.parentId != null || task.listId == listId) return@withTransaction
            val projectId = todoListDao.getProjectId(task.listId) ?: return@withTransaction
            if (todoListDao.getProjectId(listId) != projectId) return@withTransaction
            val targetIds = todoTaskDao.getTopLevelIds(listId, null) + taskId
            todoTaskDao.updateListWithSubtasks(taskId, listId)
            targetIds.forEachIndexed { position, id -> todoTaskDao.updatePosition(id, position) }
            todoTaskDao.getTopLevelIds(task.listId, task.sectionId)
                .forEachIndexed { position, id -> todoTaskDao.updatePosition(id, position) }
        }
    }

    override suspend fun setSection(taskId: Long, sectionId: Long?) {
        database.withTransaction {
            // Уже в этом подсписке — порядок не трогаем
            val task = todoTaskDao.getTask(taskId) ?: return@withTransaction
            if (task.sectionId != sectionId) moveTask(taskId, sectionId, Int.MAX_VALUE)
        }
    }

    override suspend fun moveTask(taskId: Long, sectionId: Long?, index: Int) {
        database.withTransaction {
            val task = todoTaskDao.getTask(taskId) ?: return@withTransaction
            if (task.parentId != null) return@withTransaction
            val target = validSectionId(task.listId, sectionId)
            // Подсписок чужого списка или уже удалённый — ничего не делаем
            if (target != sectionId) return@withTransaction
            val order = todoTaskDao.getTopLevelIds(task.listId, target).todoMoved(taskId, index)
            if (task.sectionId != target) {
                todoTaskDao.updateSection(taskId, target, order.indexOf(taskId))
                // Исходный подсписок — без дыры в позициях
                todoTaskDao.getTopLevelIds(task.listId, task.sectionId)
                    .forEachIndexed { position, id -> todoTaskDao.updatePosition(id, position) }
            }
            order.forEachIndexed { position, id -> todoTaskDao.updatePosition(id, position) }
        }
    }

    override suspend fun reorderSubtasks(parentId: Long, orderedIds: List<Long>) {
        database.withTransaction {
            val ids = todoTaskDao.getSubtaskIds(parentId)
            todoOrdered(ids, orderedIds).forEachIndexed { position, id -> todoTaskDao.updatePosition(id, position) }
        }
    }

    // Вызывается внутри транзакции
    private suspend fun validSectionId(listId: Long, sectionId: Long?): Long? =
        sectionId?.takeIf { todoSectionDao.getSection(it)?.listId == listId }

    override suspend fun rename(id: Long, title: String) {
        val text = title.trim()
        if (text.isNotEmpty()) todoTaskDao.updateTitle(id, text)
    }

    override suspend fun updateText(id: Long, title: String, note: String) {
        val text = title.trim()
        if (text.isNotEmpty()) todoTaskDao.updateText(id, text, note.trim())
    }

    override suspend fun deleteCompleted(listId: Long) {
        database.withTransaction {
            todoTaskDao.deleteCompletedTasks(listId)
            todoTaskDao.deleteCompletedSubtasks(listId)
        }
    }

    override suspend fun setStatus(taskId: Long, statusId: Long) {
        database.withTransaction {
            val task = todoTaskDao.getTask(taskId) ?: return@withTransaction
            val status = todoStatusDao.getStatus(statusId) ?: return@withTransaction
            // Статус другого проекта задаче не подходит
            if (status.idPT != todoTaskDao.getProjectId(taskId)) return@withTransaction
            val wasDone = todoStatusDao.getStatus(task.statusId)?.isDone == true
            val completedAt = if (status.isDone) task.completedAt ?: todoNowIso() else null
            todoTaskDao.updateStatus(taskId, statusId, completedAt)
            if (status.isDone && !wasDone) completeLinkedTasks(taskId, status.idPT, checkNotNull(completedAt))
        }
    }

    override suspend fun toggleDone(taskId: Long) {
        database.withTransaction {
            val task = todoTaskDao.getTask(taskId) ?: return@withTransaction
            val isDone = todoStatusDao.getStatus(task.statusId)?.isDone == true
            setDone(taskId, !isDone)
        }
    }

    override suspend fun setDone(taskId: Long, done: Boolean) {
        database.withTransaction {
            val task = todoTaskDao.getTask(taskId) ?: return@withTransaction
            val isDone = todoStatusDao.getStatus(task.statusId)?.isDone == true
            if (isDone == done) return@withTransaction
            val idPT = todoTaskDao.getProjectId(taskId) ?: return@withTransaction
            todoStatusDao.insertMissingDefaults(idPT)
            val target = if (done) TodoDefaultStatus.DONE else TodoDefaultStatus.PLANNED
            val status = todoStatusDao.getDefault(idPT, target) ?: error("Нет статуса $target")
            val now = todoNowIso()
            todoTaskDao.updateStatus(taskId, status.id, if (done) now else null)
            if (done) completeLinkedTasks(taskId, idPT, now)
        }
    }

    // В той же транзакции завершаем всю цепочку связей, включая блокирующие задачи.
    // Уже завершённые сохраняют собственный статус и дату; родство задач здесь не участвует.
    private suspend fun completeLinkedTasks(taskId: Long, idPT: Long, now: String) {
        todoTaskDao.getTask(taskId)?.let { createNextOccurrence(it, idPT, now) }
        val tasks = todoTaskDao.getConnectedTasks(taskId, idPT)
        if (tasks.isEmpty()) return
        todoStatusDao.insertMissingDefaults(idPT)
        val done = todoStatusDao.getDefault(idPT, TodoDefaultStatus.DONE) ?: error("Нет статуса «Завершённые»")
        for (task in tasks) {
            if (todoStatusDao.getStatus(task.statusId)?.isDone != true) {
                todoTaskDao.updateStatus(task.id, done.id, now)
                createNextOccurrence(task.copy(statusId = done.id, completedAt = now), idPT, now)
            }
        }
    }

    // Только внутри транзакции завершения: создание, теги и отметка источника атомарны.
    private suspend fun createNextOccurrence(task: TodoTaskTable, idPT: Long, now: String) {
        if (task.recurrenceGenerated) return
        val nextDate = task.toDomain().nextTodoRepeatDate() ?: return
        val planned = plannedStatus(idPT)
        val position = (todoTaskDao.getMaxPosition(task.listId, task.parentId) ?: -1) + 1
        val nextId = todoTaskDao.insertTask(task.copy(
            id = 0,
            statusId = planned.id,
            dueDate = nextDate,
            position = position,
            createdAt = now,
            completedAt = null,
            recurrenceGenerated = false
        ))
        todoTaskDao.insertTaskTags(todoTaskDao.getTagIds(task.id).map {
            TodoTaskTagTable(taskId = nextId, tagId = it)
        })
        // Не сбрасываем при отмене выполнения: повторное завершение не создаёт дубликат.
        todoTaskDao.updateTask(task.copy(recurrenceGenerated = true))
    }

    // Вызывается внутри транзакции
    private suspend fun plannedStatus(idPT: Long): TodoStatusTable {
        todoStatusDao.insertMissingDefaults(idPT)
        return todoStatusDao.getDefault(idPT, TodoDefaultStatus.PLANNED) ?: error("Нет статуса «В планах»")
    }

    override suspend fun deleteWithUndo(id: Long): TodoTaskDeletion? = database.withTransaction {
        val root = todoTaskDao.getTask(id) ?: return@withTransaction null
        val projectId = todoListDao.getProjectId(root.listId) ?: return@withTransaction null
        val tasks = todoTaskDao.getDeletionTasks(id)
        val taskSnapshot = DeletedTasks(projectId, tasks)
        val taskSnapshotRef = WeakReference(taskSnapshot)
        val ids = tasks.map { it.id }
        val tags = todoTaskDao.getDeletionTags(ids)
        val comments = todoTaskDao.getDeletionComments(ids)
        deletionLinks.removeAll { it.get() == null }
        val links = (todoTaskDao.getDeletionLinks(ids) + deletionLinks.flatMap { it.get().orEmpty() }
            .filter { it.taskId in ids || it.linkedTaskId in ids }).distinctBy { it.id }
        val snapshotRef = WeakReference(links)
        deletionLinks.add(snapshotRef)
        todoTaskDao.deleteTask(id)
        deletionTasks.removeAll { it.get() == null }
        deletionTasks.add(taskSnapshotRef)
        var restored = false
        TodoTaskDeletion {
            database.withTransaction {
                if (!restored) {
                    // Родитель мог сменить список, пока доступна отмена удаления подзадачи.
                    val restoreListId = root.parentId?.let { parentId ->
                        todoTaskDao.getTask(parentId)?.listId ?: error("Родительская задача удалена")
                    } ?: root.listId
                    val projectId = todoListDao.getProjectId(restoreListId) ?: error("Список удалён")
                    // Родитель вставляется первым. Существующие строки никогда не заменяем.
                    taskSnapshot.tasks.sortedBy { if (it.id == id) 0 else 1 }.forEach { task ->
                        val status = todoStatusDao.getStatus(task.statusId)?.takeIf { it.idPT == projectId }
                            ?: plannedStatus(projectId)
                        todoTaskDao.insertTask(task.copy(
                            listId = restoreListId,
                            sectionId = validSectionId(restoreListId, task.sectionId),
                            statusId = status.id,
                            completedAt = if (status.isDone) task.completedAt else null
                        ))
                    }
                    val validTags = if (tags.isEmpty()) emptySet() else
                        todoTagDao.getExistingIds(projectId, tags.map { it.tagId }.toSet()).toSet()
                    todoTaskDao.insertTaskTags(tags.filter { it.tagId in validTags })
                    database.todoCommentDao().insertAllForImport(comments)
                    links.forEach { link ->
                        if (todoTaskDao.getTask(link.taskId) != null && todoTaskDao.getTask(link.linkedTaskId) != null &&
                            !database.todoLinkDao().exists(link.taskId, link.linkedTaskId)) {
                            database.todoLinkDao().insertLink(link)
                        }
                    }
                    restored = true
                    deletionLinks.remove(snapshotRef)
                    deletionTasks.remove(taskSnapshotRef)
                }
            }
        }
    }

    override suspend fun delete(id: Long) {
        todoTaskDao.deleteTask(id)
    }

    // LIKE в SQLite без учёта регистра только для латиницы — фильтруем в Kotlin
    override fun searchProjectTasks(idPT: Long, query: String): Flow<List<DomainTodoTask>> {
        val text = query.trim()
        return todoTaskDao.getProjectTasks(idPT).map { list ->
            list.asSequence()
                .filter { text.isEmpty() || it.title.contains(text, ignoreCase = true) }
                .map { it.toDomain() }
                .toList()
        }
    }
}
