package com.zaroslikov.calendar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.zaroslikov.calendar.R
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarDraft
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.calendar.domain.CalendarExternalSource
import com.zaroslikov.calendar.domain.CalendarOccurrence
import com.zaroslikov.calendar.domain.CalendarRepository
import com.zaroslikov.calendar.domain.CalendarTask
import com.zaroslikov.calendar.domain.CalendarTaskKey
import com.zaroslikov.calendar.domain.CalendarTaskSource
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarTaskCompletionTest {
    @Test
    fun writesExplicitTargetsAndUsesObservedCompletion() = withCalendar { viewModel, source ->
        viewModel.onIntent(CalendarIntent.SetTaskCompleted(todo.key, true))
        viewModel.onIntent(CalendarIntent.SetTaskCompleted(todo.key, true))
        val first = source.writes.receive()
        assertEquals(todo.key, first.key)
        assertEquals(projectId, first.projectId)
        assertTrue(first.completed)
        assertEquals(setOf(todo.key), viewModel.state.value.savingTasks)
        assertTrue(viewModel.state.value.events.any { it.task == todo })

        viewModel.onIntent(CalendarIntent.SetTaskCompleted(crm.key, false))
        val second = source.writes.receive()
        assertEquals(crm.key, second.key)
        assertEquals(projectId, second.projectId)
        assertFalse(second.completed)
        assertEquals(setOf(todo.key, crm.key), viewModel.state.value.savingTasks)

        first.result.complete(Unit)
        second.result.complete(Unit)
        val afterWrite = viewModel.state.first { it.savingTasks.isEmpty() }
        assertTrue(afterWrite.events.any { it.task == todo })
        assertTrue(afterWrite.events.any { it.task == crm })

        source.events.value = source.events.value.map { event ->
            val task = requireNotNull(event.task)
            event.copy(task = task.copy(completed = task.key == todo.key))
        }

        viewModel.state.first { state ->
            state.events.any { it.task == todo.copy(completed = true) } &&
                state.events.any { it.task == crm.copy(completed = false) }
        }
    }

    @Test
    fun failureUnlocksTheTaskAndReportsTheError() = withCalendar { viewModel, source ->
        viewModel.onIntent(CalendarIntent.SetTaskCompleted(crm.key, false))
        source.writes.receive().result.completeExceptionally(IllegalStateException("Ошибка записи"))

        assertEquals(R.string.calendar_complete_error, viewModel.messages.first())
        val afterFailure = viewModel.state.first { it.savingTasks.isEmpty() }
        assertTrue(afterFailure.events.any { it.task == crm })

        viewModel.onIntent(CalendarIntent.SetTaskCompleted(crm.key, false))
        val retry = source.writes.receive()
        assertEquals(crm.key, retry.key)
        assertFalse(retry.completed)
    }

    private fun withCalendar(
        test: suspend (CalendarViewModel, TaskSource) -> Unit,
    ) = runBlocking {
        withContext(Dispatchers.Main) {
            val source = TaskSource()
            val store = ViewModelStore()
            try {
                val factory = viewModelFactory {
                    initializer {
                        CalendarViewModel(SavedStateHandle(mapOf(calendarProjectArg to projectId)), EmptyCalendar, source)
                    }
                }
                val viewModel = ViewModelProvider.create(store, factory)[CalendarViewModel::class]
                withTimeout(5_000) {
                    viewModel.state.first { it.events.size == 2 }
                    test(viewModel, source)
                }
            } finally {
                store.clear()
            }
        }
    }

    private class TaskSource : CalendarExternalSource {
        val events = MutableStateFlow(
            listOf(
                CalendarEvent("todo", CalendarEventKind.TODO_TASK, "Задача Todo", LocalDate.now(), task = todo),
                CalendarEvent("crm", CalendarEventKind.CRM_TASK, "Задача CRM", LocalDate.now(), task = crm),
            )
        )
        val writes = Channel<Write>(Channel.UNLIMITED)

        override fun observe(projectId: Long) = events

        override suspend fun setTaskCompleted(projectId: Long, key: CalendarTaskKey, completed: Boolean) {
            val result = CompletableDeferred<Unit>()
            writes.send(Write(projectId, key, completed, result))
            result.await()
        }
    }

    private data class Write(
        val projectId: Long,
        val key: CalendarTaskKey,
        val completed: Boolean,
        val result: CompletableDeferred<Unit>,
    )

    private object EmptyCalendar : CalendarRepository {
        override fun observeEvents(projectId: Long, month: YearMonth) = flowOf(emptyList<CalendarEvent>())
        override fun observeCategories(projectId: Long) = flowOf(CalendarCategory.entries.toSet())
        override suspend fun setCategoryEnabled(projectId: Long, category: CalendarCategory, enabled: Boolean) =
            error("Изменение фильтра не ожидается")
        override suspend fun save(projectId: Long, seriesId: Long?, draft: CalendarDraft) =
            error("Сохранение события не ожидается")
        override suspend fun saveOccurrence(projectId: Long, occurrence: CalendarOccurrence, draft: CalendarDraft) =
            error("Сохранение события не ожидается")
        override suspend fun delete(projectId: Long, seriesId: Long) =
            error("Удаление события не ожидается")
        override suspend fun deleteOccurrence(projectId: Long, occurrence: CalendarOccurrence) =
            error("Удаление события не ожидается")
    }

    private companion object {
        const val projectId = 37L
        val todo = CalendarTask(CalendarTaskSource.TODO, id = 9, completed = false)
        val crm = CalendarTask(CalendarTaskSource.CRM, id = 9, completed = true)
    }
}
