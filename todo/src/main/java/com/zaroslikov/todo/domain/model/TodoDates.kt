package com.zaroslikov.todo.domain.model

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Форматы хранения: дата `yyyy-MM-dd`, время `HH:mm`, момент `yyyy-MM-dd'T'HH:mm`.
// Строки одного формата сравниваются лексикографически так же, как сами даты

private val ISO_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val ISO_DATE_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

/** Сегодняшняя дата, `yyyy-MM-dd`. */
fun todoToday(now: LocalDateTime = LocalDateTime.now()): String = now.toLocalDate().toString()

/** Текущее время, `HH:mm`. */
fun todoNowTime(now: LocalDateTime = LocalDateTime.now()): String = now.format(ISO_TIME)

/** Текущий момент, `yyyy-MM-dd'T'HH:mm`. */
fun todoNowIso(now: LocalDateTime = LocalDateTime.now()): String = now.format(ISO_DATE_TIME)

fun parseTodoDate(iso: String?): LocalDate? =
    try {
        iso?.let { LocalDate.parse(it) }
    } catch (e: Exception) {
        null
    }

fun parseTodoTime(iso: String?): LocalTime? =
    try {
        iso?.let { LocalTime.parse(it, ISO_TIME) }
    } catch (e: Exception) {
        null
    }

fun parseTodoDateTime(iso: String?): LocalDateTime? =
    try {
        iso?.let { LocalDateTime.parse(it, ISO_DATE_TIME) }
    } catch (e: Exception) {
        null
    }

/**
 * Текущий момент сразу при подписке и затем в начале каждой новой минуты: «сегодня» и
 * просрочка на экранах пересчитываются сами, а не замерзают на времени открытия.
 */
fun todoMinuteTicker(): Flow<LocalDateTime> = flow {
    while (true) {
        val now = LocalDateTime.now()
        emit(now)
        delay(Duration.between(now, now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)).toMillis())
    }
}
