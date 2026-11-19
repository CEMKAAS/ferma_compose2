package com.zaroslikov.todo.data.room.repository

import androidx.room.Room
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.table.*
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsIntent
import com.zaroslikov.todo.ui.task_details.TodoTaskDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TodoTaskListTransferTest {
    private lateinit var db: TodoDatabase
    private lateinit var repository: TodoTaskRepositoryImpl

    @Before fun setUp() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().targetContext, TodoDatabase::class.java).build()
        repository = TodoTaskRepositoryImpl(db, db.todoTaskDao(), db.todoStatusDao(), db.todoListDao(), db.todoTagDao(), db.todoSectionDao())
        for (id in 1L..3L) db.todoListDao().insertList(TodoListTable(id = id, idPT = if (id == 3L) 8 else 7, title = "Список $id", createdAt = "2026-11-16T09:00"))
        db.todoStatusDao().insertStatus(TodoStatusTable(id = 1, idPT = 7, title = "В планах"))
        db.todoSectionDao().insertSection(TodoSectionTable(id = 1, listId = 1, title = "Грядки"))
        db.todoTaskDao().insertTask(task(10).copy(sectionId = 1, dueDate = "2026-11-20", repeatPeriod = TodoRepeatPeriod.WEEK, note = "Заметка"))
        db.todoTaskDao().insertTask(task(11).copy(parentId = 10, position = 3))
        db.todoTaskDao().insertTask(task(12).copy(sectionId = 1, position = 4))
        db.todoTaskDao().insertTask(task(13).copy(listId = 2))
        db.todoTagDao().insertTag(TodoTagTable(id = 1, idPT = 7, title = "Корм"))
        db.todoTaskDao().insertTaskTags(listOf(TodoTaskTagTable(taskId = 10, tagId = 1), TodoTaskTagTable(taskId = 11, tagId = 1)))
        db.todoCommentDao().insertAllForImport(listOf(TodoCommentTable(id = 1, taskId = 11, text = "Комментарий", createdAt = "2026-11-16T09:00")))
        db.todoLinkDao().insertLink(TodoTaskLinkTable(id = 1, taskId = 10, linkedTaskId = 13))
        Unit
    }

    @After fun tearDown() = db.close()

    @Test fun taskRowsUseTheIconOfTheirOwnList() = runBlocking {
        db.todoListDao().updateList(1, "Курятник", colorIndex = 0, icon = TodoListIcon.ANIMALS)
        db.todoListDao().updateList(2, "Огород", colorIndex = 0, icon = TodoListIcon.PLANTS)
        val linkRepository = TodoLinkRepositoryImpl(db, db.todoLinkDao(), db.todoTaskDao())

        val projectTask = repository.getProjectTasks(7).first().single { it.id == 13L }
        assertEquals(TodoListIcon.PLANTS, projectTask.listIcon)
        assertEquals(TodoListIcon.PLANTS, linkRepository.getLinks(10).first().single().listIcon)
        assertEquals(TodoListIcon.ANIMALS, linkRepository.getLinks(13).first().single().listIcon)
        assertEquals(TodoListIcon.PLANTS, linkRepository.getListLinks(1).first().single().listIcon)
    }

    @Test fun quickAddFromTodayCreatesFirstListForAnEmptyProject() = runBlocking {
        val taskId = repository.quickAddToProject(9, "Полить грядки", "2026-11-20", "Новый список", note = "Утром")

        val list = db.todoListDao().getAllForExport().single { it.idPT == 9L }
        val task = requireNotNull(db.todoTaskDao().getTask(taskId))
        assertEquals("Новый список", list.title)
        assertEquals(list.id, task.listId)
        assertEquals("2026-11-20", task.dueDate)
        assertEquals("Утром", task.note)
    }

    @Test fun quickAddFromTodayUsesLastTaskDestinationOfItsOwnProject() = runBlocking {
        repository.quickAdd(1, "Предыдущая задача проекта")
        val lastTaskId = repository.quickAdd(2, "Последняя задача проекта")
        repository.quickAdd(3, "Задача другого проекта")
        repository.setList(lastTaskId, 1)
        assertEquals(2L, db.todoListDao().getQuickAddDestination(7))
        repository.delete(lastTaskId)
        val backup = TodoBackupRepositoryImpl(db)
        backup.import(backup.export(), setOf(7L, 8L))

        val taskId = repository.quickAddToProject(7, "Задача на сегодня", "2026-11-20", "Новый список")

        assertEquals(2L, db.todoTaskDao().getTask(taskId)?.listId)
        assertEquals(listOf(1L, 2L), db.todoListDao().getListIds(7))
    }

    @Test fun quickAddFromTodayUsesFirstExistingListWhenNoTaskWasAdded() = runBlocking {
        db.todoListDao().insertList(TodoListTable(id = 4, idPT = 8, title = "Первый список", position = -1, createdAt = "2026-11-16T09:00"))

        val taskId = repository.quickAddToProject(8, "Задача на сегодня", "2026-11-20", "Новый список")

        assertEquals(4L, db.todoTaskDao().getTask(taskId)?.listId)
        assertEquals(listOf(4L, 3L), db.todoListDao().getListIds(8))
    }

    @Test fun movesTaskAndChildrenWithoutLosingDataAndRenumbersPositions() = runBlocking {
        val before = requireNotNull(db.todoTaskDao().getTask(10))
        val child = requireNotNull(db.todoTaskDao().getTask(11))
        repository.setList(10, 2)
        assertEquals(before.copy(listId = 2, sectionId = null, position = 1), db.todoTaskDao().getTask(10))
        assertEquals(child.copy(listId = 2), db.todoTaskDao().getTask(11))
        assertEquals(0, db.todoTaskDao().getTask(12)?.position)
        assertEquals(1, db.todoTaskDao().getDeletionComments(listOf(11)).size)
        assertEquals(1, db.todoTaskDao().getDeletionLinks(listOf(10)).size)
        assertEquals(setOf(1L), repository.getTaskTagIds(10))
        assertEquals(setOf(1L), repository.getTaskTagIds(11))
    }

    @Test fun rejectsSubtasksForeignProjectsAndMissingListsAndDoesNotReorderSameList() = runBlocking {
        val parent = db.todoTaskDao().getTask(10)
        val child = db.todoTaskDao().getTask(11)
        repository.setList(11, 2)
        repository.setList(10, 3)
        repository.setList(10, 999)
        repository.setList(10, 1)
        assertEquals(parent, db.todoTaskDao().getTask(10))
        assertEquals(child, db.todoTaskDao().getTask(11))
    }

    @Test fun undoDeletedChildUsesParentsCurrentListAfterTransfer() = runBlocking {
        val deletion = requireNotNull(repository.deleteWithUndo(11))
        repository.setList(10, 2)
        db.todoListDao().deleteList(1)
        deletion.undo()
        assertEquals(2L, db.todoTaskDao().getTask(11)?.listId)
        assertEquals(10L, db.todoTaskDao().getTask(11)?.parentId)
        assertEquals(1, db.todoTaskDao().getDeletionComments(listOf(11)).size)
    }

    @Test fun detailsFollowNewListAndSectionsWithoutLosingDraftsIncludingOpenChild() = runBlocking {
        val store = ViewModelStore()
        val viewModel = withContext(Dispatchers.Main) {
            TodoTaskDetailsViewModel(
                SavedStateHandle(), TodoListRepositoryImpl(db, db.todoListDao()), repository,
                TodoStatusRepositoryImpl(db, db.todoStatusDao(), db.todoTaskDao()),
                TodoTagRepositoryImpl(db, db.todoTagDao()),
                TodoLinkRepositoryImpl(db, db.todoLinkDao(), db.todoTaskDao()),
                TodoCommentRepositoryImpl(db.todoCommentDao()), TodoSectionRepositoryImpl(db, db.todoSectionDao())
            ).also { store.put("details", it); it.onIntent(TodoTaskDetailsIntent.Open(10)) }
        }
        try {
            withTimeout(5_000) { viewModel.state.first { it.data?.sections?.size == 1 } }
            withContext(Dispatchers.Main) {
                viewModel.onIntent(TodoTaskDetailsIntent.TitleChange("Черновик названия"))
                viewModel.onIntent(TodoTaskDetailsIntent.NoteChange("Черновик примечания"))
                viewModel.onIntent(TodoTaskDetailsIntent.SetList(2))
            }
            val moved = withTimeout(5_000) { viewModel.state.first { it.data?.task?.listId == 2L && it.data.listTitle == "Список 2" } }
            assertTrue(moved.data!!.sections.isEmpty())
            assertEquals("Черновик названия", moved.title)
            assertEquals("Черновик примечания", moved.note)
            assertEquals(setOf(1L, 2L), moved.data.projectLists.map { it.id }.toSet())
            withContext(Dispatchers.Main) { viewModel.onIntent(TodoTaskDetailsIntent.NavigateToTask(11)) }
            withTimeout(5_000) { viewModel.state.first { it.data?.task?.id == 11L } }
            repository.setList(10, 1)
            val child = withTimeout(5_000) { viewModel.state.first { it.data?.task?.listId == 1L && it.data.listTitle == "Список 1" } }
            assertTrue(child.data!!.isSubtask)
            assertTrue(child.data.sections.isEmpty())
        } finally {
            withContext(Dispatchers.Main) { store.clear() }
        }
    }

    private fun task(id: Long) = TodoTaskTable(id = id, listId = 1, title = "Задача $id", statusId = 1, createdAt = "2026-11-16T09:00")
}
