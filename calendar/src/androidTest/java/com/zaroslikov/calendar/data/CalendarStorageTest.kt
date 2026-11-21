package com.zaroslikov.calendar.data

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.calendar.domain.CalendarBackup
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarDraft
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarRepeat
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.UUID
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarStorageTest {
    private lateinit var context: Context
    private lateinit var database: CalendarDatabase
    private val dbName = "calendar-test-${UUID.randomUUID()}.db"
    private val repository get() = CalendarRepositoryImpl(database)
    private val backupRepository get() = CalendarBackupRepositoryImpl(database)

    @Before
    fun createDatabase() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        database = openDatabase()
    }

    @After
    fun deleteDatabase() {
        database.close()
        context.deleteDatabase(dbName)
    }

    @Test
    fun movesAndCancellationsStayWithOriginalDatesAfterEditingSeries() = runBlocking {
        repository.save(
            1, null, CalendarDraft("Исходная серия", date("2026-01-31"), repeat = CalendarRepeat.MONTHLY)
        )
        val february = events(1, "2026-02").single()
        val march = events(1, "2026-03").single()
        val april = events(1, "2026-04").single()
        val initial = requireNotNull(february.occurrence)
        val moved = CalendarDraft("Перенос", date("2026-03-02"), LocalTime.of(10, 30), "Заметка")
        repository.saveOccurrence(1, initial, moved)
        repository.deleteOccurrence(1, requireNotNull(march.occurrence))
        repository.deleteOccurrence(1, requireNotNull(april.occurrence))

        repository.save(
            1, initial.seriesId,
            initial.series.copy(title = "Новое название серии", repeatUntil = date("2026-03-31")),
        )

        assertTrue(events(1, "2026-02").isEmpty())
        val updated = events(1, "2026-03").single()
        assertEquals(february.id, updated.id)
        assertEquals(moved.title, updated.title)
        assertEquals(moved.date, updated.date)
        assertEquals(moved.time, updated.time)
        assertEquals(moved.note, updated.note)
        assertEquals(initial.originalDate, updated.occurrence?.originalDate)
        assertEquals("Новое название серии", updated.occurrence?.series?.title)
        assertEquals(2, backupRepository.export().exceptions.size)
        assertTrue(events(1, "2026-04").isEmpty())

        // Редактирование уже перенесённого события заменяет тот же ключ.
        repository.saveOccurrence(1, requireNotNull(updated.occurrence), moved.copy(date = date("2026-04-02")))
        assertTrue(events(1, "2026-03").isEmpty())
        assertEquals(february.id, events(1, "2026-04").single().id)

        repository.save(1, initial.seriesId, initial.series.copy(repeat = CalendarRepeat.NONE))
        assertTrue(backupRepository.export().exceptions.isEmpty())
        assertTrue(events(1, "2026-04").isEmpty())
        assertEquals(initial.series.date, events(1, "2026-01").single().date)

        repository.save(1, initial.seriesId, initial.series)
        repository.deleteOccurrence(1, initial)
        assertEquals(1, backupRepository.export().exceptions.size)
        repository.delete(1, initial.seriesId)
        assertTrue(backupRepository.export().exceptions.isEmpty())
        assertTrue(events(1, "2026-01").isEmpty())
    }

    @Test
    fun filtersSurviveReopeningAndStayScopedToProject() = runBlocking {
        val all = CalendarCategory.entries.toSet()
        assertEquals(all, categories(1))
        assertEquals(all, categories(2))
        repository.setCategoryEnabled(1, CalendarCategory.TASKS, false)
        repository.setCategoryEnabled(2, CalendarCategory.BIRTHDAYS, false)
        database.close()
        database = openDatabase()

        assertEquals(all - CalendarCategory.TASKS, categories(1))
        assertEquals(all - CalendarCategory.BIRTHDAYS, categories(2))
        repository.setCategoryEnabled(1, CalendarCategory.TASKS, true)
        assertEquals(all, categories(1))
        assertFalse(CalendarCategory.BIRTHDAYS in categories(2))
    }

    @Test
    fun backupsRestoreSeriesAndExceptionsAndDeleteByProject() = runBlocking {
        val draft = CalendarDraft("Серия", date("2026-01-01"), repeat = CalendarRepeat.DAILY)
        repository.save(1, null, draft)
        repository.save(2, null, draft.copy(title = "Другой проект"))
        val occurrence = requireNotNull(events(1, "2026-01").first().occurrence)
        repository.saveOccurrence(1, occurrence, CalendarDraft("Перенос", date("2026-02-01")))
        repository.setCategoryEnabled(1, CalendarCategory.PERSONAL, false)
        repository.setCategoryEnabled(2, CalendarCategory.BIRTHS, false)
        val snapshot = backupRepository.export()

        backupRepository.clearAll()
        assertEquals(CalendarBackup(), backupRepository.export())
        backupRepository.import(snapshot, projectIds = setOf(1, 2))
        assertEquals(snapshot, backupRepository.export())
        assertTrue(events(1, "2026-02").any { it.title == "Перенос" })
        assertTrue(events(2, "2026-02").none { it.title == "Перенос" })

        backupRepository.deleteForProject(1)
        assertTrue(events(1, "2026-01").isEmpty())
        assertTrue(backupRepository.export().exceptions.isEmpty())
        assertTrue(CalendarCategory.PERSONAL in categories(1))
        assertTrue(events(2, "2026-01").isNotEmpty())
        assertFalse(CalendarCategory.BIRTHS in categories(2))

        backupRepository.import(snapshot, projectIds = setOf(1))
        assertEquals(setOf(1L), backupRepository.export().series.map { it.projectId }.toSet())
        assertEquals(setOf(1L), backupRepository.export().disabledCategories.map { it.projectId }.toSet())
        backupRepository.import(CalendarBackup(), projectIds = setOf(1, 2))
        assertEquals(CalendarBackup(), backupRepository.export())
    }

    @Test
    fun observersReceiveOccurrenceChangesWithoutReopeningCalendar() = runBlocking {
        withTimeout(5_000) {
            repository.save(
                1, null, CalendarDraft("Серия", date("2026-02-28"), repeat = CalendarRepeat.MONTHLY)
            )
            val observed = Channel<List<CalendarEvent>>(Channel.UNLIMITED)
            val observing = launch {
                repository.observeEvents(1, YearMonth.of(2026, 2)).collect { observed.send(it) }
            }
            try {
                val event = observed.receive().single()
                repository.deleteOccurrence(1, requireNotNull(event.occurrence))
                assertTrue(observed.receive().isEmpty())
            } finally {
                observing.cancel()
            }
        }
    }

    private fun openDatabase(): CalendarDatabase = Room.databaseBuilder(context, CalendarDatabase::class.java, dbName).build()

    private suspend fun events(projectId: Long, month: String): List<CalendarEvent> = withTimeout(5_000) {
        repository.observeEvents(projectId, YearMonth.parse(month)).first()
    }

    private suspend fun categories(projectId: Long): Set<CalendarCategory> = withTimeout(5_000) {
        repository.observeCategories(projectId).first()
    }

    private fun date(value: String): LocalDate = LocalDate.parse(value)
}
