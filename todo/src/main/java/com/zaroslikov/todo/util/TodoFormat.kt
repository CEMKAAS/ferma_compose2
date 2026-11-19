package com.zaroslikov.todo.util

import com.zaroslikov.todo.domain.model.parseTodoDate
import com.zaroslikov.todo.domain.model.parseTodoDateTime
import com.zaroslikov.todo.domain.model.parseTodoTime
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DISPLAY_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val DISPLAY_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val DISPLAY_DATE_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

/** `yyyy-MM-dd` → `dd.MM.yyyy`; нераспознанная строка возвращается как есть. */
fun isoToDisplay(iso: String): String = parseTodoDate(iso)?.format(DISPLAY_DATE) ?: iso

/** `yyyy-MM-dd'T'HH:mm` → `dd.MM.yyyy HH:mm`; нераспознанная строка возвращается как есть. */
fun isoDateTimeToDisplay(iso: String): String = parseTodoDateTime(iso)?.format(DISPLAY_DATE_TIME) ?: iso

/** Срок задачи: `dd.MM.yyyy` или `dd.MM.yyyy HH:mm`; без даты — пустая строка. */
fun dueToDisplay(dueDate: String?, dueTime: String?): String {
    if (dueDate.isNullOrEmpty()) return ""
    val date = isoToDisplay(dueDate)
    val time = parseTodoTime(dueTime)?.format(DISPLAY_TIME) ?: return date
    return "$date $time"
}

/** Подписи относительных дней для [dueToSmartDisplay]; берутся из ресурсов экрана. */
data class TodoDayLabels(
    val today: String,
    val tomorrow: String,
    val yesterday: String
)

/**
 * Срок в строке списка, как в «Напоминаниях»: «Сегодня», «Завтра», «Вчера», иначе
 * `dd.MM.yyyy`; если задано время — через пробел `HH:mm`. Без даты — пустая строка,
 * нераспознанная дата возвращается как есть.
 */
fun dueToSmartDisplay(
    dueDate: String?,
    dueTime: String?,
    labels: TodoDayLabels,
    today: LocalDate = LocalDate.now()
): String {
    if (dueDate.isNullOrEmpty()) return ""
    val date = parseTodoDate(dueDate)
    val day = when (date) {
        null -> dueDate
        today -> labels.today
        today.plusDays(1) -> labels.tomorrow
        today.minusDays(1) -> labels.yesterday
        else -> date.format(DISPLAY_DATE)
    }
    val time = parseTodoTime(dueTime)?.format(DISPLAY_TIME) ?: return day
    return "$day $time"
}

/** Миллисекунды UTC для `DatePicker`. */
fun isoToMillis(iso: String?): Long? =
    parseTodoDate(iso)?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

fun millisToIso(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()

/** Часы и минуты → `HH:mm`; Locale.ROOT — цифры всегда латинские. */
fun timeToIso(hour: Int, minute: Int): String = String.format(Locale.ROOT, "%02d:%02d", hour, minute)
