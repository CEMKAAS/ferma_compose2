package com.zaroslikov.calendar.domain

import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

enum class CalendarCategory(val code: String) {
    TASKS("tasks"),
    VACCINATIONS("vaccinations"),
    BIRTHS("births"),
    BIRTHDAYS("birthdays"),
    PROCESSING_AND_FEED("processing_and_feed"),
    PERSONAL("personal"),
}

enum class CalendarEventKind(val category: CalendarCategory) {
    TODO_TASK(CalendarCategory.TASKS),
    CRM_TASK(CalendarCategory.TASKS),
    VACCINATION(CalendarCategory.VACCINATIONS),
    NEXT_VACCINATION(CalendarCategory.VACCINATIONS),
    BIRTH(CalendarCategory.BIRTHS),
    EXPECTED_BIRTH(CalendarCategory.BIRTHS),
    BIRTHDAY(CalendarCategory.BIRTHDAYS),
    RECYCLING_READY(CalendarCategory.PROCESSING_AND_FEED),
    FEED_END(CalendarCategory.PROCESSING_AND_FEED),
    PERSONAL(CalendarCategory.PERSONAL),
}

enum class CalendarTaskSource { TODO, CRM }

data class CalendarTaskKey(val source: CalendarTaskSource, val id: Long)

data class CalendarTask(val source: CalendarTaskSource, val id: Long, val completed: Boolean) {
    val key = CalendarTaskKey(source, id)
}

data class CalendarOccurrence(
    val seriesId: Long,
    val originalDate: LocalDate,
    val series: CalendarDraft,
)

data class CalendarEvent(
    val id: String,
    val kind: CalendarEventKind,
    val title: String,
    val date: LocalDate,
    val time: LocalTime? = null,
    val note: String = "",
    val task: CalendarTask? = null,
    val annual: Boolean = false,
    val occurrence: CalendarOccurrence? = null,
    val statusTitle: String? = null,
    val repeat: CalendarRepeat? = null,
    val repeatUntil: LocalDate? = null,
) {
    val category: CalendarCategory get() = kind.category
}

interface CalendarExternalSource {
    fun observe(projectId: Long): Flow<List<CalendarEvent>>
    suspend fun setTaskCompleted(projectId: Long, key: CalendarTaskKey, completed: Boolean)
}
