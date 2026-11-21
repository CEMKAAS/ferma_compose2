package com.zaroslikov.calendar.data

import androidx.room.withTransaction
import com.zaroslikov.calendar.domain.CalendarBackup
import com.zaroslikov.calendar.domain.CalendarBackupRepository
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarDisabledCategoryBackup
import com.zaroslikov.calendar.domain.CalendarExceptionBackup
import com.zaroslikov.calendar.domain.CalendarRepeat
import com.zaroslikov.calendar.domain.CalendarSeriesBackup
import com.zaroslikov.calendar.domain.occursOn
import java.time.LocalDate
import javax.inject.Inject

internal class CalendarBackupRepositoryImpl @Inject constructor(
    private val database: CalendarDatabase,
) : CalendarBackupRepository {
    private val dao = database.calendarDao()

    override suspend fun export(): CalendarBackup = database.withTransaction {
        CalendarBackup(
            series = dao.exportSeries().map {
                CalendarSeriesBackup(
                    it.id, it.projectId, it.title, it.date, it.time, it.note, it.repeatCode, it.repeatUntil
                )
            },
            exceptions = dao.exportExceptions().map {
                CalendarExceptionBackup(it.seriesId, it.originalDate, it.title, it.date, it.time, it.note)
            },
            disabledCategories = dao.exportDisabledCategories().map {
                CalendarDisabledCategoryBackup(it.projectId, it.categoryCode)
            }
        )
    }

    override suspend fun import(backup: CalendarBackup, projectIds: Set<Long>) {
        val series = backup.series.asSequence()
            .filter { it.id > 0 && it.projectId in projectIds }
            .mapNotNull(CalendarSeriesBackup::validatedRow)
            .distinctBy { it.id }
            .toList()
        val draftsById = series.associate { it.id to it.toDraft() }
        val exceptions = backup.exceptions.asSequence()
            .mapNotNull { exception ->
                val seriesDraft = draftsById[exception.seriesId] ?: return@mapNotNull null
                val originalDate = runCatching { LocalDate.parse(exception.originalDate) }.getOrNull()
                    ?: return@mapNotNull null
                if (seriesDraft.repeat == CalendarRepeat.NONE || !seriesDraft.occursOn(originalDate)) {
                    return@mapNotNull null
                }
                exception.validatedRow(originalDate)
            }
            .distinctBy { it.seriesId to it.originalDate }
            .toList()
        val categoryCodes = CalendarCategory.entries.mapTo(mutableSetOf()) { it.code }
        val categories = backup.disabledCategories.asSequence()
            .filter { it.projectId in projectIds && it.categoryCode in categoryCodes }
            .map { CalendarDisabledCategoryRow(it.projectId, it.categoryCode) }
            .distinct()
            .toList()

        database.withTransaction {
            dao.clearSeries()
            dao.clearCategories()
            dao.importSeries(series)
            dao.importExceptions(exceptions)
            dao.importDisabledCategories(categories)
        }
    }

    override suspend fun deleteForProject(projectId: Long) {
        database.withTransaction {
            dao.deleteProjectSeries(projectId)
            dao.deleteProjectCategories(projectId)
        }
    }

    override suspend fun clearAll() {
        database.withTransaction {
            dao.clearSeries()
            dao.clearCategories()
        }
    }
}

private fun CalendarSeriesBackup.validatedRow(): CalendarSeriesRow? = runCatching {
    CalendarSeriesRow(id, projectId, title, date, time, note, repeatCode, repeatUntil)
        .toDraft().validated().toSeriesRow(projectId, id)
}.getOrNull()

private fun CalendarExceptionBackup.validatedRow(original: LocalDate): CalendarExceptionRow? = runCatching {
    val row = CalendarExceptionRow(seriesId, original.toString(), title, date, time, note)
    val draft = row.toDraft()?.validated()
    row.copy(
        title = draft?.title,
        date = draft?.date?.toString(),
        time = draft?.time?.toString(),
        note = draft?.note,
    )
}.getOrNull()
