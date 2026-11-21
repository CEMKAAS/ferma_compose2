package com.zaroslikov.crm.data.room.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.crm.data.room.database.CrmDatabase
import com.zaroslikov.crm.domain.model.DomainCrmTask
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrmCalendarTasksTest {
    @Test
    fun completionAndReopeningUpdateProjectFlowAndKeepTaskOnItsDueDate() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, CrmDatabase::class.java).build()
        try {
            val repository = CrmTaskRepositoryImpl(database.crmTaskDao())
            val dueDate = "2026-01-15"
            val taskId = repository.upsertTask(DomainCrmTask(idPT = 7, title = "Звонок", dueDate = dueDate))
            val otherTaskId = repository.upsertTask(DomainCrmTask(idPT = 8, title = "Встреча", dueDate = dueDate))

            val tasks = repository.getCalendarTasks(7)
            repository.setTaskCompleted(8, taskId, completed = true)
            repository.setTaskCompleted(7, otherTaskId, completed = true)
            assertFalse(tasks.first().single().isDone)
            assertFalse(repository.getCalendarTasks(8).first().single().isDone)

            val observedTasks = Channel<DomainCrmTask>(Channel.UNLIMITED)
            val observer = launch {
                tasks.distinctUntilChanged().collect { observedTasks.send(it.single()) }
            }
            try {
                assertFalse(withTimeout(5_000) { observedTasks.receive() }.isDone)

                repository.setTaskCompleted(7, taskId, completed = true)
                val completed = withTimeout(5_000) { observedTasks.receive() }
                assertTrue(completed.isDone)
                assertEquals(dueDate, completed.dueDate)
                assertEquals(completed, repository.getTasks(7, isDone = true).first().single())

                repository.setTaskCompleted(8, taskId, completed = false)
                assertEquals(completed, tasks.first().single())

                repository.setTaskCompleted(7, taskId, completed = false)
                val reopened = withTimeout(5_000) { observedTasks.receive() }
                assertEquals(completed.copy(isDone = false), reopened)
                assertEquals(reopened, repository.getTasks(7, isDone = false).first().single())
            } finally {
                observer.cancelAndJoin()
            }
        } finally {
            database.close()
        }
    }
}
