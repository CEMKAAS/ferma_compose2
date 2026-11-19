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
import com.zaroslikov.todo.domain.model.TodoLinkType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoTaskLinkCountsTest {
    private lateinit var database: TodoDatabase

    @Before
    fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, TodoDatabase::class.java
        ).build()
        database.todoListDao().insertList(TodoListTable(id = 1, idPT = 7, title = "Огород", createdAt = "2026-01-01T09:00"))
        database.todoStatusDao().insertStatus(TodoStatusTable(id = 1, idPT = 7, title = "В планах"))
        for (id in 1L..6L) {
            database.todoTaskDao().insertTask(TodoTaskTable(
                id = id, listId = 1, parentId = if (id == 6L) 1 else null,
                title = "Задача $id", statusId = 1, createdAt = "2026-01-01T09:00"
            ))
        }
        Unit
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun mixedLinksHaveSeparateCountsForBothDirectionsAndOrdinaryLinks() = runBlocking {
        link(1, 2, TodoLinkType.BLOCKS)
        link(3, 1, TodoLinkType.BLOCKS)
        link(4, 1, TodoLinkType.BLOCKS)
        link(1, 5, TodoLinkType.RELATES)
        val item = requireNotNull(database.todoTaskDao().getTaskItem(1).first()).toDomain(emptyList())
        assertEquals(4, item.linkCount)
        assertEquals(1, item.blockingCount)
        assertEquals(2, item.blockedByCount)
        assertEquals(1, item.relatedLinkCount)
        val reverse = requireNotNull(database.todoTaskDao().getTaskItem(2).first()).toDomain(emptyList())
        assertEquals(1, reverse.blockedByCount)
        assertEquals(0, reverse.blockingCount)
        assertEquals(0, reverse.relatedLinkCount)
    }

    @Test
    fun subtaskAndProjectQueriesCarryDirectionCounts() = runBlocking {
        link(2, 6, TodoLinkType.BLOCKS)
        link(6, 3, TodoLinkType.BLOCKS)
        val subtask = database.todoTaskDao().getSubtaskItems(1).first().single().toDomain(emptyList())
        assertEquals(1, subtask.blockedByCount)
        assertEquals(1, subtask.blockingCount)
        val projectTask = database.todoTaskDao().getProjectTaskItems(7).first().single { it.item.task.id == 6L }.item
        assertEquals(1, projectTask.blockedByCount)
        assertEquals(1, projectTask.blockingCount)
        val listTask = database.todoTaskDao().getAllTaskItems(1).first().single { it.task.id == 6L }
        assertEquals(2, listTask.linkCount)
        assertEquals(1, listTask.blockedByCount)
        assertEquals(1, listTask.blockingCount)
    }

    private suspend fun link(from: Long, to: Long, type: TodoLinkType) {
        database.todoLinkDao().insertLink(TodoTaskLinkTable(taskId = from, linkedTaskId = to, type = type))
    }
}
