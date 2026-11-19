package com.zaroslikov.todo.data.room.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.table.TodoCommentTable
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoTagTable
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.data.room.table.TodoTaskTagTable
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoTaskDeletionTest {
    private lateinit var database: TodoDatabase
    private lateinit var repository: TodoTaskRepositoryImpl
    private val createdAt = "2026-01-01T09:00"

    @Before
    fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            TodoDatabase::class.java
        ).build()
        repository = TodoTaskRepositoryImpl(
            database, database.todoTaskDao(), database.todoStatusDao(),
            database.todoListDao(), database.todoTagDao(), database.todoSectionDao()
        )
        database.todoListDao().insertList(TodoListTable(id = 1, idPT = 7, title = "Огород", createdAt = createdAt))
        database.todoStatusDao().insertStatus(TodoStatusTable(id = 1, idPT = 7, title = "В планах"))
        Unit
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun undoParentDeletionRestoresChildrenAndTheirData() = runBlocking {
        val parent = task(10, position = 3)
        val child = task(11, parentId = 10, position = 2)
        val other = task(12)
        listOf(parent, child, other).forEach { database.todoTaskDao().insertTask(it) }
        database.todoTagDao().insertTag(TodoTagTable(id = 1, idPT = 7, title = "Теплица"))
        val tags = listOf(TodoTaskTagTable(taskId = 10, tagId = 1), TodoTaskTagTable(taskId = 11, tagId = 1))
        database.todoTaskDao().insertTaskTags(tags)
        val comments = listOf(
            TodoCommentTable(id = 1, taskId = 10, text = "Родитель", createdAt = createdAt),
            TodoCommentTable(id = 2, taskId = 11, text = "Подзадача", createdAt = createdAt)
        )
        database.todoCommentDao().insertAllForImport(comments)
        val links = listOf(
            TodoTaskLinkTable(id = 1, taskId = 10, linkedTaskId = 12),
            TodoTaskLinkTable(id = 2, taskId = 12, linkedTaskId = 11)
        )
        database.todoLinkDao().insertAllForImport(links)

        val deletion = requireNotNull(repository.deleteWithUndo(10))
        assertNull(database.todoTaskDao().getTask(10))
        assertNull(database.todoTaskDao().getTask(11))
        assertEquals(emptyList<TodoCommentTable>(), database.todoCommentDao().getAllForExport())
        assertEquals(emptyList<TodoTaskLinkTable>(), database.todoLinkDao().getAllForExport())

        deletion.undo()

        assertEquals(parent, database.todoTaskDao().getTask(10))
        assertEquals(child, database.todoTaskDao().getTask(11))
        assertEquals(other, database.todoTaskDao().getTask(12))
        assertEquals(tags.toSet(), database.todoTaskDao().getAllTaskTagsForExport().toSet())
        assertEquals(comments.toSet(), database.todoCommentDao().getAllForExport().toSet())
        assertEquals(links.toSet(), database.todoLinkDao().getAllForExport().toSet())
    }

    @Test
    fun undoSubtaskDeletionKeepsParentAndSiblingUnchanged() = runBlocking {
        val parent = task(10)
        val child = task(11, parentId = 10, position = 1)
        val sibling = task(12, parentId = 10, position = 2)
        listOf(parent, child, sibling).forEach { database.todoTaskDao().insertTask(it) }

        val deletion = requireNotNull(repository.deleteWithUndo(11))
        assertNull(database.todoTaskDao().getTask(11))
        assertEquals(parent, database.todoTaskDao().getTask(10))
        assertEquals(sibling, database.todoTaskDao().getTask(12))
        deletion.undo()

        assertEquals(child, database.todoTaskDao().getTask(11))
        assertEquals(parent, database.todoTaskDao().getTask(10))
        assertEquals(sibling, database.todoTaskDao().getTask(12))
    }

    @Test
    fun undoCanBeCalledTwiceWithoutReplacingRestoredTask() = runBlocking {
        database.todoTaskDao().insertTask(task(10))
        val deletion = requireNotNull(repository.deleteWithUndo(10))
        deletion.undo()
        database.todoTaskDao().updateTitle(10, "Изменено после отмены")

        deletion.undo()

        assertEquals("Изменено после отмены", database.todoTaskDao().getTask(10)?.title)
        assertEquals(1, database.todoTaskDao().getAllForExport().size)
    }

    @Test
    fun undoTwoLinkedTasksInDeletionOrderRestoresTheirLink() = runBlocking {
        database.todoTaskDao().insertTask(task(10))
        database.todoTaskDao().insertTask(task(11))
        val link = TodoTaskLinkTable(id = 1, taskId = 10, linkedTaskId = 11)
        database.todoLinkDao().insertLink(link)
        val first = requireNotNull(repository.deleteWithUndo(10))
        val second = requireNotNull(repository.deleteWithUndo(11))

        first.undo()
        second.undo()

        assertEquals(listOf(link), database.todoLinkDao().getAllForExport())
    }

    @Test
    fun laterUndoDoesNotResurrectLinkExplicitlyDeletedAfterFirstUndo() = runBlocking {
        database.todoTaskDao().insertTask(task(10))
        database.todoTaskDao().insertTask(task(11))
        val link = TodoTaskLinkTable(id = 1, taskId = 10, linkedTaskId = 11)
        database.todoLinkDao().insertLink(link)
        val first = requireNotNull(repository.deleteWithUndo(10))
        first.undo()
        assertEquals(listOf(link), database.todoLinkDao().getAllForExport())
        database.todoLinkDao().deleteLink(link.id)

        val second = requireNotNull(repository.deleteWithUndo(10))
        second.undo()
        // Сохраняем первый снимок доступным на протяжении второй отмены.
        first.undo()

        assertEquals(emptyList<TodoTaskLinkTable>(), database.todoLinkDao().getAllForExport())
        assertEquals(task(10), database.todoTaskDao().getTask(10))
        assertEquals(task(11), database.todoTaskDao().getTask(11))
    }

    private fun task(id: Long, parentId: Long? = null, position: Int = 0) = TodoTaskTable(
        id = id, listId = 1, parentId = parentId, title = "Задача $id",
        note = "Заметка $id", statusId = 1, position = position,
        dueDate = "2026-02-01", dueTime = "10:30", createdAt = createdAt
    )
}
