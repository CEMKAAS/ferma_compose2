package com.zaroslikov.todo.data.room.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.table.TodoListTable
import com.zaroslikov.todo.data.room.table.TodoSectionTable
import com.zaroslikov.todo.data.room.table.TodoStatusTable
import com.zaroslikov.todo.data.room.table.TodoTagTable
import com.zaroslikov.todo.data.room.table.TodoTaskLinkTable
import com.zaroslikov.todo.data.room.table.TodoTaskTable
import com.zaroslikov.todo.data.room.table.TodoTaskTagTable
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodoTaskRecurrenceTest {
    private lateinit var database: TodoDatabase
    private lateinit var repository: TodoTaskRepositoryImpl
    private val dao get() = database.todoTaskDao()
    private val createdAt = "2026-01-01T09:00"

    @Before fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, TodoDatabase::class.java
        ).build()
        repository = TodoTaskRepositoryImpl(database, dao, database.todoStatusDao(),
            database.todoListDao(), database.todoTagDao(), database.todoSectionDao())
        database.todoListDao().insertList(TodoListTable(id = 1, idPT = 7, title = "Огород", createdAt = createdAt))
        database.todoSectionDao().insertSection(TodoSectionTable(id = 1, listId = 1, title = "Весна"))
        database.todoStatusDao().insertStatus(TodoStatusTable(
            id = 1, idPT = 7, title = "В планах", defaultCode = TodoDefaultStatus.PLANNED))
        database.todoStatusDao().insertStatus(TodoStatusTable(
            id = 2, idPT = 7, title = "Завершённые", defaultCode = TodoDefaultStatus.DONE, isDone = true))
        database.todoStatusDao().insertStatus(TodoStatusTable(id = 3, idPT = 7, title = "Готово", isDone = true))
        database.todoTagDao().insertTag(TodoTagTable(id = 1, idPT = 7, title = "Полив"))
        dao.insertTask(source())
        dao.insertTaskTags(listOf(TodoTaskTagTable(10, 1)))
        Unit
    }

    @After fun tearDown() = database.close()

    @Test fun checkboxCreatesPlannedSuccessorWithFieldsAndTagsAndKeepsOriginal() = runBlocking {
        dao.insertTask(source().copy(id = 11, parentId = 10, sectionId = null, repeatPeriod = TodoRepeatPeriod.NONE))

        repository.toggleDone(10)

        val original = requireNotNull(dao.getTask(10))
        val next = dao.getAllForExport().single { it.id != 10L && it.id != 11L }
        assertEquals(2L, original.statusId)
        assertNotNull(original.completedAt)
        assertTrue(original.recurrenceGenerated)
        assertEquals("2026-02-28", next.dueDate)
        assertEquals(source().title, next.title)
        assertEquals(source().note, next.note)
        assertEquals(source().priority, next.priority)
        assertEquals(source().dueTime, next.dueTime)
        assertEquals(1L, next.listId)
        assertEquals(1L, next.sectionId)
        assertNull(next.parentId)
        assertEquals(1L, next.statusId)
        assertNull(next.completedAt)
        assertFalse(next.recurrenceGenerated)
        assertEquals(TodoRepeatPeriod.MONTH, next.repeatPeriod)
        assertEquals(source().repeatEndDate, next.repeatEndDate)
        assertEquals(source().assignees, next.assignees)
        assertEquals(listOf(1L), dao.getTagIds(next.id))
        assertTrue(dao.getSubtaskIds(next.id).isEmpty())
    }

    @Test fun repeatedCompletionUndoRedoAndSuccessorDeletionDoNotCreateDuplicates() = runBlocking {
        repository.setDone(10, true)
        repository.setDone(10, true)
        val next = dao.getAllForExport().single { it.id != 10L }
        repository.delete(next.id)
        repository.setDone(10, false)
        val stale = requireNotNull(dao.getTask(10)).toDomain().copy(recurrenceGenerated = false)
        repository.upsert(stale.copy(note = "Правка"), setOf(1))
        repository.setDone(10, true)
        assertEquals(1, dao.getAllForExport().size)
        assertTrue(requireNotNull(dao.getTask(10)).recurrenceGenerated)
    }

    @Test fun upsertDoneCopiesNewFieldsAndValidatedTags() = runBlocking {
        repository.upsert(source().toDomain().copy(title = "Новый полив", statusId = 3), setOf(1, 999))
        val next = dao.getAllForExport().single { it.id != 10L }
        assertEquals("Новый полив", next.title)
        assertEquals(listOf(1L), dao.getTagIds(next.id))
        assertEquals(3L, dao.getTask(10)?.statusId)
    }

    @Test fun customDoneStatusAlsoRepeatsNewlyCompletedLinkedTasks() = runBlocking {
        dao.insertTask(source().copy(id = 11, dueDate = "2026-02-01", repeatPeriod = TodoRepeatPeriod.DAY))
        database.todoLinkDao().insertLink(TodoTaskLinkTable(taskId = 11, linkedTaskId = 10))
        repository.setStatus(10, 3)
        val successors = dao.getAllForExport().filter { it.id != 10L && it.id != 11L }
        assertEquals(setOf("2026-02-28", "2026-02-02"), successors.map { it.dueDate }.toSet())
        assertEquals(3L, dao.getTask(10)?.statusId)
        assertEquals(2L, dao.getTask(11)?.statusId)
        assertTrue(successors.all { it.statusId == 1L })
        assertEquals(1, database.todoLinkDao().getAllForExport().size)
    }

    @Test fun endDateIncludesFinalOccurrenceAndStopsAfterIt() = runBlocking {
        dao.updateTask(source().copy(repeatEndDate = "2026-02-28"))
        repository.setDone(10, true)
        val next = dao.getAllForExport().single { it.id != 10L }
        repository.setDone(next.id, true)
        assertEquals(2, dao.getAllForExport().size)
    }

    @Test fun noRepeatOrDateDoesNotCreateSuccessor() = runBlocking {
        dao.updateTask(source().copy(dueDate = null))
        repository.setDone(10, true)
        assertEquals(1, dao.getAllForExport().size)
    }

    @Test fun repeatingSubtaskKeepsParentAndDoesNotCompleteIt() = runBlocking {
        dao.insertTask(source().copy(id = 11, parentId = 10, sectionId = null, assignees = listOf("Анна")))
        repository.setDone(11, true)
        val next = dao.getAllForExport().single { it.id != 10L && it.id != 11L }
        assertEquals(10L, next.parentId)
        assertNull(next.sectionId)
        assertEquals(listOf("Анна"), next.assignees)
        assertEquals(source().assignees, dao.getTask(10)?.assignees)
        assertEquals(1L, dao.getTask(10)?.statusId)
    }

    @Test fun successorInsertFailureRollsBackCompletionAndGenerationMarker() = runBlocking {
        database.openHelper.writableDatabase.execSQL("""
            CREATE TRIGGER reject_repeat BEFORE INSERT ON todo_task_table
            BEGIN SELECT RAISE(ABORT, 'test failure'); END
        """.trimIndent())
        try {
            repository.setDone(10, true)
            fail("Ожидалась ошибка создания повторной задачи")
        } catch (_: android.database.sqlite.SQLiteException) {
            // Ошибка создания должна откатить всю транзакцию завершения.
        }
        assertEquals(1L, dao.getTask(10)?.statusId)
        assertNull(dao.getTask(10)?.completedAt)
        assertFalse(requireNotNull(dao.getTask(10)).recurrenceGenerated)
    }

    private fun source() = TodoTaskTable(
        id = 10, listId = 1, sectionId = 1, title = "Полить", note = "Теплица", priority = TodoPriority.HIGH,
        statusId = 1, dueDate = "2026-01-31", dueTime = "08:30", createdAt = createdAt,
        repeatPeriod = TodoRepeatPeriod.MONTH, repeatEndDate = "2026-12-31", assignees = listOf("Иван", "Пётр")
    )
}
