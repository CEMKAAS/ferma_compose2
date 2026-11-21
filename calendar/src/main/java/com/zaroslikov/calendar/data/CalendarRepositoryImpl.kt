package com.zaroslikov.calendar.data

import androidx.room.withTransaction
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarDraft
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.calendar.domain.CalendarOccurrence
import com.zaroslikov.calendar.domain.CalendarRepeat
import com.zaroslikov.calendar.domain.CalendarRepository
import com.zaroslikov.calendar.domain.datesIn
import com.zaroslikov.calendar.domain.occursOn
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class CalendarRepositoryImpl @Inject constructor(
    private val database: CalendarDatabase,
) : CalendarRepository {
    private val dao = database.calendarDao()

    override fun observeEvents(projectId: Long, month: YearMonth): Flow<List<CalendarEvent>> =
        dao.observeSeries(projectId).map { rows -> rows.flatMap { it.eventsIn(month) } }.distinctUntilChanged()

    override fun observeCategories(projectId: Long): Flow<Set<CalendarCategory>> =
        dao.observeDisabledCategories(projectId).map { disabled ->
            CalendarCategory.entries.filterTo(mutableSetOf()) { it.code !in disabled }
        }.distinctUntilChanged()

    override suspend fun setCategoryEnabled(projectId: Long, category: CalendarCategory, enabled: Boolean) {
        if (enabled) dao.setEnabled(projectId, category.code)
        else dao.setDisabled(CalendarDisabledCategoryRow(projectId, category.code))
    }

    override suspend fun save(projectId: Long, seriesId: Long?, draft: CalendarDraft) {
        val validated = draft.validated()
        if (seriesId == null) {
            dao.insertSeries(validated.toSeriesRow(projectId, id = 0))
            return
        }
        database.withTransaction {
            checkNotNull(dao.getSeries(projectId, seriesId))
            dao.updateSeries(validated.toSeriesRow(projectId, seriesId))
            if (validated.repeat == CalendarRepeat.NONE) {
                dao.deleteExceptions(seriesId)
            } else {
                dao.getExceptions(seriesId).forEach { exception ->
                    if (!validated.occursOn(LocalDate.parse(exception.originalDate))) {
                        dao.deleteException(seriesId, exception.originalDate)
                    }
                }
            }
        }
    }

    override suspend fun saveOccurrence(projectId: Long, occurrence: CalendarOccurrence, draft: CalendarDraft) {
        val replacement = draft.copy(repeat = CalendarRepeat.NONE, repeatUntil = null).validated()
        putException(projectId, occurrence, replacement)
    }

    override suspend fun delete(projectId: Long, seriesId: Long) {
        // Внешний ключ удаляет исключения вместе с серией в той же транзакции.
        database.withTransaction { dao.deleteSeries(projectId, seriesId) }
    }

    override suspend fun deleteOccurrence(projectId: Long, occurrence: CalendarOccurrence) {
        putException(projectId, occurrence, replacement = null)
    }

    private suspend fun putException(projectId: Long, occurrence: CalendarOccurrence, replacement: CalendarDraft?) {
        database.withTransaction {
            val series = checkNotNull(dao.getSeries(projectId, occurrence.seriesId)).toDraft()
            check(series.repeat != CalendarRepeat.NONE && series.occursOn(occurrence.originalDate))
            dao.putException(
                CalendarExceptionRow(
                    seriesId = occurrence.seriesId,
                    originalDate = occurrence.originalDate.toString(),
                    title = replacement?.title,
                    date = replacement?.date?.toString(),
                    time = replacement?.time?.toString(),
                    note = replacement?.note,
                )
            )
        }
    }
}

internal fun CalendarDraft.validated(): CalendarDraft {
    require(title.isNotBlank())
    require(repeat == CalendarRepeat.NONE || repeatUntil == null || repeatUntil >= date)
    return copy(title = title.trim(), repeatUntil = repeatUntil.takeUnless { repeat == CalendarRepeat.NONE })
}

internal fun CalendarDraft.toSeriesRow(projectId: Long, id: Long): CalendarSeriesRow = CalendarSeriesRow(
    id = id,
    projectId = projectId,
    title = title,
    date = date.toString(),
    time = time?.toString(),
    note = note,
    repeatCode = repeat.code,
    repeatUntil = repeatUntil?.toString(),
)

internal fun CalendarSeriesRow.toDraft(): CalendarDraft = CalendarDraft(
    title = title,
    date = LocalDate.parse(date),
    time = time?.let(LocalTime::parse),
    note = note,
    repeat = CalendarRepeat.entries.first { it.code == repeatCode },
    repeatUntil = repeatUntil?.let(LocalDate::parse),
)

internal fun CalendarExceptionRow.toDraft(): CalendarDraft? {
    val effectiveDate = date ?: return null
    return CalendarDraft(
        title = requireNotNull(title),
        date = LocalDate.parse(effectiveDate),
        time = time?.let(LocalTime::parse),
        note = note.orEmpty(),
    )
}

private fun CalendarSeriesWithExceptions.eventsIn(month: YearMonth): List<CalendarEvent> {
    val draft = series.toDraft()
    val overridden = exceptions.mapTo(mutableSetOf()) { LocalDate.parse(it.originalDate) }
    return buildList {
        draft.datesIn(month).filterNot { it in overridden }.forEach { day ->
            add(series.event(day, draft, draft.copy(date = day)))
        }
        exceptions.forEach { exception ->
            val replacement = exception.toDraft()
            if (replacement != null && YearMonth.from(replacement.date) == month) {
                add(series.event(LocalDate.parse(exception.originalDate), draft, replacement))
            }
        }
    }
}

private fun CalendarSeriesRow.event(
    originalDate: LocalDate, series: CalendarDraft, content: CalendarDraft,
): CalendarEvent = CalendarEvent(
    id = "personal:$id:$originalDate",
    kind = CalendarEventKind.PERSONAL,
    title = content.title,
    date = content.date,
    time = content.time,
    note = content.note,
    occurrence = CalendarOccurrence(id, originalDate, series),
)
