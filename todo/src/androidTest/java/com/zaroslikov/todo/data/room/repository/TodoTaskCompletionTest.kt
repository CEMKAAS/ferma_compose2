package com.zaroslikov.todo.data.room.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoTaskCompletionTest {
    private lateinit var database: TodoDatabase
    private lateinit var repository: TodoTaskRepositoryImpl
    private val createdAt = "2026-01-01T09:00"

    @Before
    fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, TodoDatabase::class.java
        ).build()
        repository = TodoTaskRepositoryImpl(
            database, database.todoTaskDao(), database.todoStatusDao(),
            database.todoListDao(), database.todoTagDao(), database.todoSectionDao()
        )
        database.todoListDao().insertList(TodoListTable(id = 1, idPT = 7, title = "Огород", createdAt = createdAt))
        database.todoListDao().insertList(TodoListTable(id = 2, idPT = 7, title = "Теплица", createdAt = createdAt))
        database.todoStatusDao().insertStatus(TodoStatusTable(
            id = 1, idPT = 7, title = "В планах", defaultCode = TodoDefaultStatus.PLANNED
        ))
        database.todoStatusDao().insertStatus(TodoStatusTable(
            id = 2, idPT = 7, title = "Завершённые", defaultCode = TodoDefaultStatus.DONE, isDone = true
        ))
        database.todoStatusDao().insertStatus(TodoStatusTable(id = 3, idPT = 7, title = "Готово", isDone = true))
        for (id in 10L..14L) database.todoTaskDao().insertTask(task(id))
        Unit
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun checkboxCompletesChainAndCycleInBothDirectionsAcrossLists() = runBlocking {
        database.todoTaskDao().updateTask(task(12).copy(listId = 2))
        link(10, 11, TodoLinkType.RELATES)
        link(12, 11, TodoLinkType.BLOCKS)
        link(10, 12, TodoLinkType.BLOCKS)
        link(12, 13, TodoLinkType.RELATES)

        repository.toggleDone(11)

        for (id in 10L..13L) assertDone(id)
        assertPlanned(14)
    }

    @Test
    fun customDoneStatusCompletesLinksWithDefaultStatusAndPreservesCompletedTasks() = runBlocking {
        link(10, 11, TodoLinkType.BLOCKS)
        link(11, 12, TodoLinkType.RELATES)
        link(12, 13, TodoLinkType.RELATES)
        database.todoTaskDao().updateTask(task(12).copy(statusId = 3, completedAt = createdAt))

        repository.setStatus(11, 3)

        assertDone(10)
        assertDone(11, 3)
        assertDone(13)
        assertEquals(task(12).copy(statusId = 3, completedAt = createdAt), database.todoTaskDao().getTask(12))
    }

    @Test
    fun upsertCompletionSavesFieldsAndCompletesLinkedTasks() = runBlocking {
        link(10, 11, TodoLinkType.RELATES)
        val task = requireNotNull(database.todoTaskDao().getTask(10)).toDomain()

        repository.upsert(task.copy(title = "Новое название", note = "Заметка", statusId = 3), emptySet())

        assertDone(10, 3)
        assertDone(11)
        assertEquals("Новое название", database.todoTaskDao().getTask(10)?.title)
        assertEquals("Заметка", database.todoTaskDao().getTask(10)?.note)
    }

    @Test
    fun reopeningAndEditingCompletedTaskDoNotChangeLinkedTasks() = runBlocking {
        link(10, 11, TodoLinkType.RELATES)
        database.todoTaskDao().updateTask(task(10).copy(statusId = 2, completedAt = createdAt))
        repository.upsert(requireNotNull(database.todoTaskDao().getTask(10)).toDomain().copy(note = "Правка"), emptySet())
        assertPlanned(11)
        repository.setStatus(11, 2)
        val linkedBefore = database.todoTaskDao().getTask(11)

        repository.toggleDone(10)

        assertPlanned(10)
        assertEquals(linkedBefore, database.todoTaskDao().getTask(11))
    }

    @Test
    fun parentAndSubtasksAreIndependentUnlessExplicitlyLinked() = runBlocking {
        database.todoTaskDao().updateTask(task(11).copy(parentId = 10))
        database.todoTaskDao().updateTask(task(12).copy(parentId = 10))
        link(11, 13, TodoLinkType.RELATES)

        repository.toggleDone(11)

        assertDone(11)
        assertDone(13)
        assertPlanned(10)
        assertPlanned(12)
    }

    @Test
    fun explicitCompletionsOfLinkedTasksAreIdempotent() = runBlocking {
        link(10, 11, TodoLinkType.RELATES)

        repository.setDone(10, true)
        repository.setDone(11, true)

        assertDone(10)
        assertDone(11)
        repository.setDone(10, false)
        repository.setDone(11, true)
        assertPlanned(10)
        assertDone(11)
    }

    @Test
    fun nonDoneStatusDoesNotCompleteLinks() = runBlocking {
        link(10, 11, TodoLinkType.RELATES)

        repository.setStatus(10, 1)

        assertPlanned(10)
        assertPlanned(11)
    }

    @Test
    fun failureRollsBackEntireCompletion() = runBlocking {
        link(10, 11, TodoLinkType.RELATES)
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_completion BEFORE UPDATE OF status_id ON todo_task_table
            WHEN NEW.id = 11 BEGIN SELECT RAISE(ABORT, 'test failure'); END
        """.trimIndent())

        try {
            repository.toggleDone(10)
            fail("Ожидалась ошибка записи")
        } catch (_: android.database.sqlite.SQLiteException) {
            // Ошибка связанной задачи должна откатить и исходную.
        }

        assertPlanned(10)
        assertPlanned(11)
    }

    private suspend fun assertDone(id: Long, statusId: Long = 2) {
        assertEquals(statusId, database.todoTaskDao().getTask(id)?.statusId)
        assertNotNull(database.todoTaskDao().getTask(id)?.completedAt)
    }

    private suspend fun assertPlanned(id: Long) {
        assertEquals(1L, database.todoTaskDao().getTask(id)?.statusId)
        assertNull(database.todoTaskDao().getTask(id)?.completedAt)
    }

    private suspend fun link(from: Long, to: Long, type: TodoLinkType) {
        database.todoLinkDao().insertLink(TodoTaskLinkTable(taskId = from, linkedTaskId = to, type = type))
    }

    private fun task(id: Long) = TodoTaskTable(
        id = id, listId = 1, title = "Задача $id", statusId = 1, createdAt = createdAt
    )
}
