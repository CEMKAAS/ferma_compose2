package com.zaroslikov.todo

import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.domain.model.nextTodoRepeatDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TodoRecurrenceTest {
    private fun task(date: String? = "2026-01-31", period: TodoRepeatPeriod = TodoRepeatPeriod.DAY) =
        DomainTodoTask(listId = 1, title = "Полить", statusId = 1, dueDate = date, repeatPeriod = period)

    @Test fun dayAndWeekAdvanceFromScheduledDate() {
        assertEquals("2026-02-01", task().nextTodoRepeatDate())
        assertEquals("2026-02-07", task(period = TodoRepeatPeriod.WEEK).nextTodoRepeatDate())
    }

    @Test fun monthAndYearClampToLastValidDay() {
        assertEquals("2026-02-28", task(period = TodoRepeatPeriod.MONTH).nextTodoRepeatDate())
        assertEquals("2024-02-29", task("2024-01-31", TodoRepeatPeriod.MONTH).nextTodoRepeatDate())
        assertEquals("2025-02-28", task("2024-02-29", TodoRepeatPeriod.YEAR).nextTodoRepeatDate())
        assertEquals("2026-03-28", task("2026-02-28", TodoRepeatPeriod.MONTH).nextTodoRepeatDate())
    }

    @Test fun endDateIsInclusive() {
        assertEquals("2026-02-01", task().copy(repeatEndDate = "2026-02-01").nextTodoRepeatDate())
        assertNull(task().copy(repeatEndDate = "2026-01-31").nextTodoRepeatDate())
    }

    @Test fun disabledMissingOrInvalidDatesDoNotRepeat() {
        assertNull(task(period = TodoRepeatPeriod.NONE).nextTodoRepeatDate())
        assertNull(task(null).nextTodoRepeatDate())
        assertNull(task("wrong").nextTodoRepeatDate())
        assertNull(task().copy(repeatEndDate = "wrong").nextTodoRepeatDate())
    }
}
