package com.zaroslikov.todo.domain.model

import java.time.DateTimeException

/** Следующий срок от текущего планового, с календарным округлением месяца/года и включительным окончанием. */
fun DomainTodoTask.nextTodoRepeatDate(): String? {
    val due = parseTodoDate(dueDate) ?: return null
    val end = if (repeatEndDate == null) null else parseTodoDate(repeatEndDate) ?: return null
    val next = try {
        when (repeatPeriod) {
            TodoRepeatPeriod.NONE -> return null
            TodoRepeatPeriod.DAY -> due.plusDays(1)
            TodoRepeatPeriod.WEEK -> due.plusWeeks(1)
            TodoRepeatPeriod.MONTH -> due.plusMonths(1)
            TodoRepeatPeriod.YEAR -> due.plusYears(1)
        }
    } catch (_: DateTimeException) {
        return null
    }
    return next.takeUnless { end != null && it.isAfter(end) }?.toString()
}
