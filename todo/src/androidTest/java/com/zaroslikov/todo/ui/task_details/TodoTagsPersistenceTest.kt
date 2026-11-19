package com.zaroslikov.todo.ui.task_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import com.zaroslikov.todo.data.room.repository.TodoCommentRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoLinkRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoListRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoSectionRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoStatusRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoTagRepositoryImpl
import com.zaroslikov.todo.data.room.repository.TodoTaskRepositoryImpl
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.repository.TodoTagRepository
import com.zaroslikov.todo.domain.repository.TodoTaskRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/** Настоящие ViewModel и Room: черновик не пишет связи задачи до SaveTags. */
class TodoTagsPersistenceTest {
    private lateinit var database: TodoDatabase
    private lateinit var tasks: TodoTaskRepository
    private lateinit var tags: TodoTagRepository
    private lateinit var viewModel: TodoTaskDetailsViewModel
    private val store = ViewModelStore()
    private val writes = AtomicInteger()
    private var taskId = 0L
    private var feedId = 0L
    private var repairId = 0L

    @Before
    fun setUp() = runBlocking {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext, TodoDatabase::class.java
        ).build()
        tasks = TodoTaskRepositoryImpl(database, database.todoTaskDao(), database.todoStatusDao(),
            database.todoListDao(), database.todoTagDao(), database.todoSectionDao())
        tags = TodoTagRepositoryImpl(database, database.todoTagDao())
        val lists = TodoListRepositoryImpl(database, database.todoListDao())
        val statuses = TodoStatusRepositoryImpl(database, database.todoStatusDao(), database.todoTaskDao())
        val listId = lists.upsert(DomainTodoList(idPT = 7, title = "Хозяйство"))
        statuses.ensureDefaults(7)
        val statusId = statuses.getStatuses(7).first().first().id
        feedId = tags.upsert(DomainTodoTag(idPT = 7, title = "Корм"))
        repairId = tags.upsert(DomainTodoTag(idPT = 7, title = "Ремонт"))
        taskId = tasks.upsert(DomainTodoTask(listId = listId, title = "Купить корм", statusId = statusId), setOf(feedId))
    }

    private suspend fun openViewModel(tagRepository: TodoTagRepository = tags) {
        val countedTasks = object : TodoTaskRepository by tasks {
            override suspend fun upsert(task: DomainTodoTask, tagIds: Set<Long>): Long {
                writes.incrementAndGet()
                return tasks.upsert(task, tagIds)
            }
        }
        withContext(Dispatchers.Main) {
            viewModel = TodoTaskDetailsViewModel(
                SavedStateHandle(), TodoListRepositoryImpl(database, database.todoListDao()), countedTasks,
                TodoStatusRepositoryImpl(database, database.todoStatusDao(), database.todoTaskDao()), tagRepository,
                TodoLinkRepositoryImpl(database, database.todoLinkDao(), database.todoTaskDao()),
                TodoCommentRepositoryImpl(database.todoCommentDao()),
                TodoSectionRepositoryImpl(database, database.todoSectionDao())
            )
            store.put("details", viewModel)
            viewModel.onIntent(TodoTaskDetailsIntent.Open(taskId))
        }
        withTimeout(5_000) { viewModel.state.first { it.data != null } }
    }

    private suspend fun send(vararg intents: TodoTaskDetailsIntent) = withContext(Dispatchers.Main) {
        intents.forEach(viewModel::onIntent)
    }

    @After
    fun tearDown() = runBlocking {
        withContext(Dispatchers.Main) { store.clear() }
        database.close()
    }

    @Test
    fun selectionResetAndDismissDoNotWriteAndSaveWritesExactDraftOnce() = runBlocking {
        openViewModel()
        send(TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.ToggleTag(repairId),
            TodoTaskDetailsIntent.ResetTags, TodoTaskDetailsIntent.CloseTagsSheet)
        assertEquals(setOf(feedId), tasks.getTaskTagIds(taskId))
        assertEquals(0, writes.get())
        send(TodoTaskDetailsIntent.OpenTagsSheet)
        assertEquals(setOf(feedId), viewModel.state.value.tagSelection!!.selectedIds)
        send(TodoTaskDetailsIntent.ToggleTag(feedId), TodoTaskDetailsIntent.ToggleTag(repairId))
        assertEquals(setOf(feedId), tasks.getTaskTagIds(taskId))
        assertEquals(0, writes.get())
        send(TodoTaskDetailsIntent.SaveTags)
        withTimeout(5_000) { tasks.getTask(taskId).first { it?.tags?.map { tag -> tag.id }?.toSet() == setOf(repairId) } }
        assertEquals(1, writes.get())
        assertFalse(viewModel.state.value.showTagsSheet)
        assertEquals("Купить корм", tasks.getTask(taskId).first()!!.task.title)
        send(TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.ResetTags, TodoTaskDetailsIntent.Close)
        assertEquals(setOf(repairId), tasks.getTaskTagIds(taskId))
        assertEquals(1, writes.get())
    }

    @Test
    fun createdTagExistsInProjectButAttachesOnlyAfterSave() = runBlocking {
        openViewModel()
        send(TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.StartNewTag,
            TodoTaskDetailsIntent.NewTagChange("Новый"), TodoTaskDetailsIntent.SubmitNewTag)
        val newTag = withTimeout(5_000) { tags.getTags(7).first { all -> all.any { it.title == "Новый" } } }.first { it.title == "Новый" }
        withTimeout(5_000) { viewModel.state.first { newTag.id in it.tagSelection!!.selectedIds } }
        assertEquals(setOf(feedId), tasks.getTaskTagIds(taskId))
        assertEquals(0, writes.get())
        send(TodoTaskDetailsIntent.CloseTagsSheet, TodoTaskDetailsIntent.OpenTagsSheet)
        assertEquals(setOf(feedId), viewModel.state.value.tagSelection!!.selectedIds)
        assertTrue(viewModel.state.value.data!!.tags.any { it.id == newTag.id })
        send(TodoTaskDetailsIntent.ToggleTag(newTag.id), TodoTaskDetailsIntent.SaveTags)
        withTimeout(5_000) { tasks.getTask(taskId).first { it?.tags?.size == 2 } }
        assertEquals(setOf(feedId, newTag.id), tasks.getTaskTagIds(taskId))
        assertEquals(1, writes.get())
    }

    @Test
    fun delayedTagCreationDoesNotSelectInReopenedPicker() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val delayedTags = object : TodoTagRepository by tags {
            override suspend fun upsert(tag: DomainTodoTag): Long {
                entered.complete(Unit)
                release.await()
                return tags.upsert(tag)
            }
        }
        openViewModel(delayedTags)
        send(TodoTaskDetailsIntent.OpenTagsSheet, TodoTaskDetailsIntent.StartNewTag,
            TodoTaskDetailsIntent.NewTagChange("Поздний"), TodoTaskDetailsIntent.SubmitNewTag)
        withTimeout(5_000) { entered.await() }
        send(TodoTaskDetailsIntent.CloseTagsSheet, TodoTaskDetailsIntent.OpenTagsSheet)
        release.complete(Unit)
        withTimeout(5_000) { viewModel.state.first { it.data!!.tags.any { tag -> tag.title == "Поздний" } } }
        assertEquals(setOf(feedId), viewModel.state.value.tagSelection!!.selectedIds)
        assertEquals(setOf(feedId), tasks.getTaskTagIds(taskId))
        assertEquals(0, writes.get())
    }
}
