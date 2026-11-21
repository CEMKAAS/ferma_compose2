package com.zaroslikov.calendar.domain

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarRecurrenceTest {
    @Test
    fun `разовое событие показывается только в свой день`() {
        val draft = CalendarDraft("План", date("2026-01-02"))
        assertEquals(listOf(draft.date), draft.datesIn(month("2026-01")))
        assertTrue(draft.datesIn(month("2026-02")).isEmpty())
    }

    @Test
    fun `ежедневный повтор включает дату окончания`() {
        val draft = CalendarDraft(
            "План", date("2026-01-31"), repeat = CalendarRepeat.DAILY, repeatUntil = date("2026-02-02")
        )
        assertEquals(listOf(date("2026-02-01"), date("2026-02-02")), draft.datesIn(month("2026-02")))
        assertFalse(draft.occursOn(date("2026-01-30")))
    }

    @Test
    fun `еженедельный повтор включает окончание только если день соответствует расписанию`() {
        val draft = CalendarDraft(
            "План", date("2026-01-31"), repeat = CalendarRepeat.WEEKLY, repeatUntil = date("2026-02-15")
        )
        assertEquals(listOf(date("2026-02-07"), date("2026-02-14")), draft.datesIn(month("2026-02")))
    }

    @Test
    fun `ежемесячный повтор восстанавливает число начала после короткого месяца`() {
        val draft = CalendarDraft("План", date("2026-01-31"), repeat = CalendarRepeat.MONTHLY)
        assertEquals(listOf(date("2026-02-28")), draft.datesIn(month("2026-02")))
        assertEquals(listOf(date("2026-03-31")), draft.datesIn(month("2026-03")))
    }

    @Test
    fun `ежегодный повтор и день рождения 29 февраля учитывают високосный год`() {
        val leapDay = date("2024-02-29")
        val draft = CalendarDraft("План", leapDay, repeat = CalendarRepeat.YEARLY)
        val birthday = CalendarEvent("birthday", CalendarEventKind.BIRTHDAY, "День рождения", leapDay, annual = true)

        for ((year, day) in listOf(2026 to 28, 2028 to 29)) {
            val february = YearMonth.of(year, 2)
            val expected = listOf(february.atDay(day))
            assertEquals(expected, draft.datesIn(february))
            assertEquals(expected, listOf(birthday).eventsInMonth(february).map { it.date })
        }
    }

    private fun date(value: String): LocalDate = LocalDate.parse(value)
    private fun month(value: String): YearMonth = YearMonth.parse(value)
}
