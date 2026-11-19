package com.zaroslikov.todo.data.room.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.ui.task_details.TodoLinkDraft
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoLinkDirectionTest {
    private lateinit var database: TodoDatabase
    private lateinit var repository: TodoLinkRepositoryImpl

    @Before
    fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            TodoDatabase::class.java
        ).build()
        repository = TodoLinkRepositoryImpl(database, database.todoLinkDao(), database.todoTaskDao())
        database.todoListDao().insertList(TodoListTable(id = 1, idPT = 7, title = "Хозяйство", createdAt = "2026-01-01T09:00"))
        database.todoStatusDao().insertStatus(TodoStatusTable(id = 1, idPT = 7, title = "В планах"))
        listOf(10L, 11L, 12L).forEach { id ->
            database.todoTaskDao().insertTask(
                TodoTaskTable(id = id, listId = 1, title = "Задача $id", statusId = 1, createdAt = "2026-01-01T09:00")
            )
        }
        Unit
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun incomingChoicePersistsSelectedTaskAsBlockerAndIsReadFromBothSides() = runBlocking {
        val link = TodoLinkDraft(type = TodoLinkType.BLOCKS, isOutgoing = false).toLink(10, 11)
        assertTrue(repository.addLink(link.taskId, link.linkedTaskId, link.type))

        val blockedSide = repository.getLinks(10).first().single()
        assertFalse(blockedSide.isOutgoing)
        assertEquals(11L, blockedSide.task.id)
        assertEquals(TodoLinkType.BLOCKS, blockedSide.link.type)
        val blockingSide = repository.getLinks(11).first().single()
        assertTrue(blockingSide.isOutgoing)
        assertEquals(10L, blockingSide.task.id)
        assertEquals(listOf(11L), database.todoTaskDao().getConnectedTasks(10, 7).map { it.id })
        assertEquals(listOf(10L), database.todoTaskDao().getConnectedTasks(11, 7).map { it.id })
        // Обратный выбор той же пары не создаёт вторую связь.
        assertFalse(repository.addLink(10, 11, TodoLinkType.BLOCKS))
    }

    @Test
    fun outgoingChoiceKeepsCurrentTaskAsBlocker() = runBlocking {
        val link = TodoLinkDraft(type = TodoLinkType.BLOCKS).toLink(10, 12)
        assertTrue(repository.addLink(link.taskId, link.linkedTaskId, link.type))

        assertTrue(repository.getLinks(10).first().single().isOutgoing)
        assertFalse(repository.getLinks(12).first().single().isOutgoing)
        assertEquals(listOf(12L), database.todoTaskDao().getConnectedTasks(10, 7).map { it.id })
        assertEquals(listOf(10L), database.todoTaskDao().getConnectedTasks(12, 7).map { it.id })
    }
}
