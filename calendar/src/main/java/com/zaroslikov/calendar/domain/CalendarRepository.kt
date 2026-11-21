package com.zaroslikov.calendar.domain

import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun observeEvents(projectId: Long, month: YearMonth): Flow<List<CalendarEvent>>
    fun observeCategories(projectId: Long): Flow<Set<CalendarCategory>>
    suspend fun setCategoryEnabled(projectId: Long, category: CalendarCategory, enabled: Boolean)
    suspend fun save(projectId: Long, seriesId: Long?, draft: CalendarDraft)
    suspend fun saveOccurrence(projectId: Long, occurrence: CalendarOccurrence, draft: CalendarDraft)
    suspend fun delete(projectId: Long, seriesId: Long)
    suspend fun deleteOccurrence(projectId: Long, occurrence: CalendarOccurrence)
}
