package com.zaroslikov.domain.models.animal.pregnancy

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/** Даты беременности хранятся строкой `dd.MM.yyyy`, как остальные даты в БД. */
object PregnancyDates {
    val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun parseOrNull(value: String): LocalDate? =
        try {
            LocalDate.parse(value.trim(), FORMATTER)
        } catch (e: DateTimeParseException) {
            null
        }

    fun format(date: LocalDate): String = date.format(FORMATTER)

    /** Время заметки — `HH:mm`. */
    val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun parseTimeOrNull(value: String): LocalTime? =
        try {
            LocalTime.parse(value.trim(), TIME_FORMATTER)
        } catch (e: DateTimeParseException) {
            null
        }

    fun formatTime(time: LocalTime): String = time.format(TIME_FORMATTER)

    /**
     * Необязательное время заметки: пустое → null, иначе приведённое к `HH:mm`.
     * Нечитаемое время → [IllegalArgumentException].
     */
    fun normalizeTime(value: String?): String? {
        val trimmed = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return formatTime(requireNotNull(parseTimeOrNull(trimmed)) { "Неверное время «$value»" })
    }
}
