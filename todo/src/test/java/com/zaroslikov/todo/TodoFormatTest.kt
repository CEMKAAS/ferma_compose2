package com.zaroslikov.todo

import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.todoColorPalette
import com.zaroslikov.todo.util.TodoDayLabels
import com.zaroslikov.todo.util.dueToDisplay
import com.zaroslikov.todo.util.dueToSmartDisplay
import com.zaroslikov.todo.util.isoDateTimeToDisplay
import com.zaroslikov.todo.util.isoToDisplay
import com.zaroslikov.todo.util.isoToMillis
import com.zaroslikov.todo.util.millisToIso
import com.zaroslikov.todo.util.timeToIso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class TodoFormatTest {

    @Test
    fun dates_displayAsRussianFormat() {
        assertEquals("05.03.2026", isoToDisplay("2026-03-05"))
        assertEquals("05.03.2026 09:07", isoDateTimeToDisplay("2026-03-05T09:07"))
        assertEquals("мусор", isoToDisplay("мусор"))
    }

    @Test
    fun due_withAndWithoutTime() {
        assertEquals("", dueToDisplay(null, "10:00"))
        assertEquals("05.03.2026", dueToDisplay("2026-03-05", null))
        assertEquals("05.03.2026 18:30", dueToDisplay("2026-03-05", "18:30"))
    }

    @Test
    fun smartDue_relativeDaysAndTime() {
        val labels = TodoDayLabels(today = "Сегодня", tomorrow = "Завтра", yesterday = "Вчера")
        val today = LocalDate.of(2026, 3, 15)
        assertEquals("", dueToSmartDisplay(null, "10:00", labels, today))
        assertEquals("Сегодня", dueToSmartDisplay("2026-03-15", null, labels, today))
        assertEquals("Завтра 09:05", dueToSmartDisplay("2026-03-16", "09:05", labels, today))
        assertEquals("Вчера", dueToSmartDisplay("2026-03-14", "мусор", labels, today))
        assertEquals("17.03.2026 18:30", dueToSmartDisplay("2026-03-17", "18:30", labels, today))
        assertEquals("01.01.2027", dueToSmartDisplay("2027-01-01", null, labels, today))
        assertEquals("мусор", dueToSmartDisplay("мусор", null, labels, today))
    }

    @Test
    fun millis_roundTrip() {
        assertEquals("2026-03-05", millisToIso(isoToMillis("2026-03-05")!!))
        assertNull(isoToMillis(null))
        assertNull(isoToMillis(""))
    }

    @Test
    fun time_isZeroPadded() {
        assertEquals("09:05", timeToIso(9, 5))
        assertEquals("23:59", timeToIso(23, 59))
    }

    @Test
    fun palette_hasEightColorsAndIndexIsSafe() {
        assertEquals(8, todoColorPalette.size)
        assertEquals(todoColorPalette[0], todoColor(8))
        assertEquals(todoColorPalette[7], todoColor(-1))
    }
}
