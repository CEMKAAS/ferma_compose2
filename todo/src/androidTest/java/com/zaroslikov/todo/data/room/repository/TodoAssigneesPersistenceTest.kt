package com.zaroslikov.todo.data.room.repository

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TodoAssigneesPersistenceTest {
    private lateinit var database: TodoDatabase
    private lateinit var tasks: TodoTaskRepositoryImpl

    @Before fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, TodoDatabase::class.java
        ).build()
        tasks = TodoTaskRepositoryImpl(database, database.todoTaskDao(), database.todoStatusDao(),
            database.todoListDao(), database.todoTagDao(), database.todoSectionDao())
        database.todoListDao().insertList(com.zaroslikov.todo.data.room.table.TodoListTable(
            id = 1, idPT = 7, title = "Куры", createdAt = "2026-11-14T09:00"))
        Unit
    }

    @After fun tearDown() = database.close()

    @Test fun parentAndSubtaskHaveIndependentNamesAndEditsPreserveOtherData() = runBlocking {
        val parent = tasks.quickAdd(1, "Корм", note = "Мешок")
        val child = tasks.upsert(DomainTodoTask(listId = 1, parentId = parent, title = "Склад", statusId = 0), emptySet())
        tasks.setAssignees(parent, listOf(" Иван ", "Анна", "ИВАН", ""))
        tasks.setAssignees(child, listOf("Пётр"))
        tasks.updateText(parent, "Корм утром", "Два мешка")
        assertEquals(listOf("Иван", "Анна"), tasks.getTask(parent).first()?.task?.assignees)
        assertEquals(listOf("Пётр"), tasks.getTask(child).first()?.task?.assignees)
        assertEquals("Два мешка", tasks.getTask(parent).first()?.task?.note)
        tasks.setAssignees(parent, emptyList())
        assertEquals(emptyList<String>(), tasks.getTask(parent).first()?.task?.assignees)
        assertEquals(listOf("Пётр"), tasks.getTask(child).first()?.task?.assignees)
    }

    @Test fun backupAndUndoPreserveAssignees() = runBlocking {
        val id = tasks.quickAdd(1, "Корм")
        tasks.setAssignees(id, listOf("Иван", "Анна"))
        val backupRepository = TodoBackupRepositoryImpl(database)
        val backup = backupRepository.export()
        backupRepository.import(backup, setOf(7))
        assertEquals(listOf("Иван", "Анна"), tasks.getTask(id).first()?.task?.assignees)
        val undo = requireNotNull(tasks.deleteWithUndo(id))
        undo.undo()
        assertEquals(listOf("Иван", "Анна"), tasks.getTask(id).first()?.task?.assignees)
    }

    @Test fun directoryIsProjectScopedAndSurvivesUnassignmentAndTaskDeletion() = runBlocking {
        database.todoListDao().insertList(com.zaroslikov.todo.data.room.table.TodoListTable(
            id = 2, idPT = 8, title = "Соседи", createdAt = "2026-11-14T09:00"))
        val parent = tasks.quickAdd(1, "Корм")
        val child = tasks.upsert(DomainTodoTask(listId = 1, parentId = parent, title = "Склад", statusId = 0,
            assignees = listOf(" Анна ", "АННА")), emptySet())
        val other = tasks.quickAdd(2, "Полив")
        tasks.setAssignees(parent, listOf("Иван"))
        tasks.setAssignees(other, listOf("Пётр", "Иван"))
        tasks.setAssignees(parent, emptyList())
        tasks.delete(parent)
        assertNull(tasks.getTask(child).first())
        assertEquals(setOf("Анна", "Иван"), tasks.getProjectAssignees(7).first().toSet())
        assertEquals(setOf("Пётр", "Иван"), tasks.getProjectAssignees(8).first().toSet())
        assertEquals(emptyList<String>(), tasks.getProjectAssignees(99).first())
        val backupRepository = TodoBackupRepositoryImpl(database)
        val backup = backupRepository.export()
        backupRepository.clearAll()
        assertTrue(tasks.getProjectAssignees(7).first().isEmpty())
        backupRepository.import(backup, setOf(7, 8))
        assertEquals(setOf("Анна", "Иван"), tasks.getProjectAssignees(7).first().toSet())
        backupRepository.deleteForProject(7)
        assertTrue(tasks.getProjectAssignees(7).first().isEmpty())
        assertEquals(setOf("Пётр", "Иван"), tasks.getProjectAssignees(8).first().toSet())
    }

    @Test fun managingDirectoryUpdatesAssignmentsOnlyInTheCurrentProject() = runBlocking {
        database.todoListDao().insertList(com.zaroslikov.todo.data.room.table.TodoListTable(
            id = 2, idPT = 8, title = "Соседи", createdAt = "2026-11-14T09:00"))
        val parent = tasks.quickAdd(1, "Корм")
        val child = tasks.upsert(DomainTodoTask(listId = 1, parentId = parent, title = "Склад", statusId = 0), emptySet())
        val other = tasks.quickAdd(2, "Полив")
        tasks.setAssignees(parent, listOf("Иван", "Анна", "Иванов"))
        tasks.setAssignees(child, listOf("ИВАН"))
        tasks.setAssignees(other, listOf("Иван"))

        suspend fun saveName(originalName: String?, name: String) =
            tasks.saveProjectAssigneeProfile(7, originalName, TodoAssigneeProfile(firstName = name), "Исполнитель")

        assertTrue(saveName(null, "  Пётр  "))
        assertFalse(saveName(null, "пётр"))
        assertFalse(saveName("Иван", " анна "))
        assertTrue(saveName("Иван", "ИВАН"))
        assertTrue(saveName("ИВАН", "  Василий  "))
        assertEquals(listOf("Василий", "Анна", "Иванов"), tasks.getTask(parent).first()?.task?.assignees)
        assertEquals(listOf("Василий"), tasks.getTask(child).first()?.task?.assignees)
        assertEquals(setOf("Анна", "Василий", "Иванов", "Пётр"), tasks.getProjectAssignees(7).first().toSet())

        tasks.deleteProjectAssignee(7, "Василий")
        tasks.deleteProjectAssignee(7, "Василий")
        assertEquals(listOf("Анна", "Иванов"), tasks.getTask(parent).first()?.task?.assignees)
        assertEquals(emptyList<String>(), tasks.getTask(child).first()?.task?.assignees)
        assertEquals(listOf("Иван"), tasks.getTask(other).first()?.task?.assignees)

        val backupRepository = TodoBackupRepositoryImpl(database)
        backupRepository.import(backupRepository.export(), setOf(7, 8))
        assertEquals(setOf("Анна", "Иванов", "Пётр"), tasks.getProjectAssignees(7).first().toSet())
        assertEquals(listOf("Иван"), tasks.getProjectAssignees(8).first())
    }

    @Test fun restoringDeletedTaskUsesUpdatedAssignments() = runBlocking {
        val task = tasks.quickAdd(1, "Корм")
        tasks.setAssignees(task, listOf("Иван", "Анна"))
        val deletion = requireNotNull(tasks.deleteWithUndo(task))
        tasks.deleteProjectAssignee(7, "Иван")
        assertTrue(tasks.saveProjectAssigneeProfile(7, "Анна", TodoAssigneeProfile(firstName = "Мария"), "Исполнитель"))
        deletion.undo()
        assertEquals(listOf("Мария"), tasks.getTask(task).first()?.task?.assignees)
    }

    @Test fun profileSaveUpdatesAssignmentsAndPreservesAllFieldsInBackup() = runBlocking {
        val task = tasks.quickAdd(1, "Корм")
        tasks.setAssignees(task, listOf("Иван"))
        assertEquals("Иван", tasks.getProjectAssigneeProfiles(7).first().single().profile.firstName)

        val profile = TodoAssigneeProfile(
            firstName = "Иван",
            lastName = "Смирнов",
            middleName = "Петрович",
            birthday = "1987-04-12",
            phone = "+7 800 555-01-01",
            email = "ivan@example.com",
            address = "Москва, ул. Лесная",
            description = "Первая строка\nВторая строка"
        )
        val label = "Иван Смирнов Петрович"
        assertTrue(tasks.saveProjectAssigneeProfile(7, "Иван", profile, "Исполнитель"))
        assertEquals(listOf(label), tasks.getTask(task).first()?.task?.assignees)
        assertEquals(profile, tasks.getProjectAssigneeProfiles(7).first().single().profile)
        assertFalse(tasks.saveProjectAssigneeProfile(7, null, profile, "Исполнитель"))

        val updated = profile.copy(firstName = "", lastName = "", middleName = "")
        assertTrue(tasks.saveProjectAssigneeProfile(7, label, updated, "Исполнитель"))
        val backupRepository = TodoBackupRepositoryImpl(database)
        val backup = backupRepository.export()
        backupRepository.import(backup, setOf(7))
        assertEquals(updated, tasks.getProjectAssigneeProfiles(7).first().single().profile)
        assertEquals(listOf(label), tasks.getTask(task).first()?.task?.assignees)
    }

    @Test fun profileWithoutNameGetsAUniqueLabelAndAllowsAnEmptyForm() = runBlocking {
        val blank = TodoAssigneeProfile()
        val contact = blank.copy(phone = "+7 800 555-01-02")
        assertTrue(tasks.saveProjectAssigneeProfile(7, null, blank, "Исполнитель"))
        assertTrue(tasks.saveProjectAssigneeProfile(7, null, contact, "Исполнитель"))
        val profiles = tasks.getProjectAssigneeProfiles(7).first()
        assertEquals(listOf("Исполнитель", "Исполнитель 2"), profiles.map { it.name })
        assertEquals(listOf(blank, contact), profiles.map { it.profile })
    }

    @Test fun directorySurvivesDatabaseReopen() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "todo-assignees-reopen-test"
        context.deleteDatabase(name)
        var persisted = Room.databaseBuilder(context, TodoDatabase::class.java, name).build()
        try {
            persisted.todoListDao().insertList(com.zaroslikov.todo.data.room.table.TodoListTable(
                id = 1, idPT = 7, title = "Куры", createdAt = "2026-11-14T09:00"))
            val repository = TodoTaskRepositoryImpl(persisted, persisted.todoTaskDao(), persisted.todoStatusDao(),
                persisted.todoListDao(), persisted.todoTagDao(), persisted.todoSectionDao())
            val task = repository.quickAdd(1, "Корм")
            repository.setAssignees(task, listOf("Иван"))
            repository.delete(task)
            persisted.close()
            persisted = Room.databaseBuilder(context, TodoDatabase::class.java, name).build()
            val reopened = TodoTaskRepositoryImpl(persisted, persisted.todoTaskDao(), persisted.todoStatusDao(),
                persisted.todoListDao(), persisted.todoTagDao(), persisted.todoSectionDao())
            assertEquals(listOf("Иван"), reopened.getProjectAssignees(7).first())
        } finally {
            persisted.close()
            context.deleteDatabase(name)
        }
    }
}
