package com.zaroslikov.calendar.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

enum class CalendarRepeat(val code: String) {
    NONE("none"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly"),
}

data class CalendarDraft(
    val title: String,
    val date: LocalDate,
    val time: LocalTime? = null,
    val note: String = "",
    val repeat: CalendarRepeat = CalendarRepeat.NONE,
    val repeatUntil: LocalDate? = null,
)

internal fun CalendarDraft.occursOn(day: LocalDate): Boolean {
    if (day < date || (repeatUntil != null && day > repeatUntil)) return false
    return when (repeat) {
        CalendarRepeat.NONE -> day == date
        CalendarRepeat.DAILY -> true
        CalendarRepeat.WEEKLY -> day.dayOfWeek == date.dayOfWeek
        CalendarRepeat.MONTHLY -> day.dayOfMonth == minOf(date.dayOfMonth, day.lengthOfMonth())
        CalendarRepeat.YEARLY -> day == date.withYear(day.year)
    }
}

internal fun CalendarDraft.datesIn(month: YearMonth): List<LocalDate> {
    val first = maxOf(date, month.atDay(1))
    val last = minOf(month.atEndOfMonth(), repeatUntil ?: LocalDate.MAX)
    if (first > last) return emptyList()
    return (first.dayOfMonth..last.dayOfMonth).map(month::atDay).filter(::occursOn)
}

fun List<CalendarEvent>.eventsInMonth(month: YearMonth): List<CalendarEvent> = mapNotNull { event ->
    val day = if (event.annual) event.date.withYear(month.year) else event.date
    if (day >= event.date && YearMonth.from(day) == month) event.copy(date = day) else null
}
