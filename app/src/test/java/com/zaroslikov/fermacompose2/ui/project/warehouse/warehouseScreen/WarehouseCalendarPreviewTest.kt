package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.calendar.domain.CalendarTask
import com.zaroslikov.calendar.domain.CalendarTaskSource
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WarehouseCalendarPreviewTest {
    private val today = LocalDate.of(2026, 1, 31)
    private val categories = CalendarCategory.entries.toSet()

    @Test
    fun includesAllMonthsAndFindsNextDateAfterMonthBoundary() {
        assertEquals(
            listOf(YearMonth.of(2026, 1), YearMonth.of(2026, 2), YearMonth.of(2026, 3)),
            warehouseCalendarMonths(today),
        )
        val preview = buildWarehouseCalendarPreview(
            today,
            personal = listOf(event("later", today.plusDays(3)), event("tomorrow", today.plusDays(1))),
            external = emptyList(),
            categories = categories,
        )

        assertEquals(today, preview.today.date)
        assertEquals(LocalDate.of(2026, 2, 1), preview.upcoming?.date)
        assertEquals(listOf("tomorrow"), preview.upcoming?.entries?.map(CalendarEvent::id))
    }

    @Test
    fun skipsCompletedTasksAndDisabledCategoriesBeforeSelectingUpcomingDate() {
        val preview = buildWarehouseCalendarPreview(
            today,
            personal = listOf(event("hidden personal", today.plusDays(1))),
            external = listOf(
                task("done", today, completed = true),
                task("open", today, completed = false),
                task("done tomorrow", today.plusDays(1), completed = true),
                event("vaccination", today.plusDays(2), CalendarEventKind.NEXT_VACCINATION),
            ),
            categories = categories - CalendarCategory.PERSONAL,
        )

        assertEquals(listOf("open"), preview.today.entries.map(CalendarEvent::id))
        assertEquals(listOf("vaccination"), preview.upcoming?.entries?.map(CalendarEvent::id))
    }

    @Test
    fun includesAnnualEventsAndLastDayOfHorizonButExcludesPastAndLaterEvents() {
        val beneficiary = event("annual", LocalDate.of(2020, 3, 2), CalendarEventKind.BIRTHDAY)
        val preview = buildWarehouseCalendarPreview(
            today,
            personal = listOf(event("past", today.minusDays(1)), event("outside", today.plusDays(31))),
            external = listOf(beneficiary.copy(annual = true)),
            categories = categories,
        )

        assertEquals(today.plusDays(30), preview.upcoming?.date)
        assertEquals(listOf("annual"), preview.upcoming?.entries?.map(CalendarEvent::id))
    }

    @Test
    fun showsThreeSortedEntriesAndCountsRemainingEntries() {
        val events = listOf("fourth", "third", "second", "first").mapIndexed { index, title ->
            event(title, today).copy(time = LocalTime.of(12 - index, 0))
        }
        val preview = buildWarehouseCalendarPreview(today, events, emptyList(), categories)

        assertEquals(listOf("first", "second", "third"), preview.today.entries.map(CalendarEvent::id))
        assertEquals(1, preview.today.remainingCount)
        assertNull(preview.upcoming)
    }

    @Test
    fun preservesTodayWhenThereAreNoEntries() {
        val preview = buildWarehouseCalendarPreview(today, emptyList(), emptyList(), emptySet())

        assertEquals(today, preview.today.date)
        assertEquals(emptyList<CalendarEvent>(), preview.today.entries)
        assertEquals(0, preview.today.remainingCount)
        assertNull(preview.upcoming)
    }

    private fun event(
        title: String,
        date: LocalDate,
        kind: CalendarEventKind = CalendarEventKind.PERSONAL,
    ) = CalendarEvent(id = title, kind = kind, title = title, date = date)

    private fun task(title: String, date: LocalDate, completed: Boolean) =
        event(title, date, CalendarEventKind.TODO_TASK).copy(
            task = CalendarTask(CalendarTaskSource.TODO, 1, completed)
        )
}
