package com.zaroslikov.calendar.domain

import kotlinx.serialization.Serializable

@Serializable
data class CalendarBackup(
    val series: List<CalendarSeriesBackup> = emptyList(),
    val exceptions: List<CalendarExceptionBackup> = emptyList(),
    val disabledCategories: List<CalendarDisabledCategoryBackup> = emptyList(),
)

@Serializable
data class CalendarSeriesBackup(
    val id: Long,
    val projectId: Long,
    val title: String,
    val date: String,
    val time: String? = null,
    val note: String = "",
    val repeatCode: String = "none",
    val repeatUntil: String? = null,
)

/** Отсутствие [date] означает отмену. Ключом всегда остаётся [originalDate]. */
@Serializable
data class CalendarExceptionBackup(
    val seriesId: Long,
    val originalDate: String,
    val title: String? = null,
    val date: String? = null,
    val time: String? = null,
    val note: String? = null,
)

@Serializable
data class CalendarDisabledCategoryBackup(val projectId: Long, val categoryCode: String)

interface CalendarBackupRepository {
    suspend fun export(): CalendarBackup
    suspend fun import(backup: CalendarBackup, projectIds: Set<Long>)
    suspend fun deleteForProject(projectId: Long)
    suspend fun clearAll()
}
