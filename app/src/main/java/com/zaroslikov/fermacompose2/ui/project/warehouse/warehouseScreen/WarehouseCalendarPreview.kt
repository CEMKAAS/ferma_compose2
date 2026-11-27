package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import android.util.Log
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarExternalSource
import com.zaroslikov.calendar.domain.CalendarRepository
import com.zaroslikov.calendar.domain.eventsInMonth
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen

data class WarehouseCalendarPreview(
    val today: WarehouseCalendarDay,
    val upcoming: WarehouseCalendarDay?,
    val loadFailed: Boolean = false,
)

data class WarehouseCalendarDay(
    val date: LocalDate,
    val entries: List<CalendarEvent> = emptyList(),
    val remainingCount: Int = 0,
)

class WarehouseCalendarPreviewSource @Inject constructor(
    private val repository: CalendarRepository,
    private val externalSource: CalendarExternalSource,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observe(projectId: Long): Flow<WarehouseCalendarPreview> =
        warehouseCalendarDates().flatMapLatest { today ->
            val personalEvents = combine(
                warehouseCalendarMonths(today).map { repository.observeEvents(projectId, it) },
            ) { eventsByMonth -> eventsByMonth.flatMap { it } }
            combine(
                personalEvents,
                externalSource.observe(projectId),
                repository.observeCategories(projectId),
            ) { personal, external, categories ->
                buildWarehouseCalendarPreview(today, personal, external, categories)
            }.retryWhen { error, _ ->
                Log.e("WarehouseCalendarPreview", "Не удалось загрузить записи календаря", error)
                emit(WarehouseCalendarPreview(WarehouseCalendarDay(today), upcoming = null, loadFailed = true))
                delay(60_000L)
                true
            }
        }.distinctUntilChanged().flowOn(Dispatchers.Default)
}

internal fun warehouseCalendarMonths(today: LocalDate): List<YearMonth> {
    val lastMonth = YearMonth.from(today.plusDays(PREVIEW_HORIZON_DAYS))
    return generateSequence(YearMonth.from(today)) { it.plusMonths(1) }
        .takeWhile { it <= lastMonth }
        .toList()
}

internal fun buildWarehouseCalendarPreview(
    today: LocalDate,
    personal: List<CalendarEvent>,
    external: List<CalendarEvent>,
    categories: Set<CalendarCategory>,
): WarehouseCalendarPreview {
    val lastDate = today.plusDays(PREVIEW_HORIZON_DAYS)
    val events = personal + warehouseCalendarMonths(today).flatMap { external.eventsInMonth(it) }
    val byDate = events.filter {
        it.date in today..lastDate && it.category in categories && it.task?.completed != true
    }.groupBy(CalendarEvent::date)
    val nextDate = byDate.keys.filter { it > today }.minOrNull()
    return WarehouseCalendarPreview(
        today = calendarDay(today, byDate[today].orEmpty()),
        upcoming = nextDate?.let { calendarDay(it, byDate.getValue(it)) },
    )
}

private fun calendarDay(date: LocalDate, events: List<CalendarEvent>) = WarehouseCalendarDay(
    date = date,
    entries = events.sortedWith(eventOrder).take(VISIBLE_ENTRIES),
    remainingCount = (events.size - VISIBLE_ENTRIES).coerceAtLeast(0),
)

internal fun warehouseCalendarDates(): Flow<LocalDate> = flow {
    while (true) {
        emit(LocalDate.now())
        delay(60_000)
    }
}.distinctUntilChanged()

private val eventOrder = compareBy(CalendarEvent::time, CalendarEvent::title, CalendarEvent::id)
private const val PREVIEW_HORIZON_DAYS = 30L
private const val VISIBLE_ENTRIES = 3
