package com.zaroslikov.calendar.ui

import androidx.annotation.StringRes
import com.zaroslikov.calendar.R
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarDraft
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarOccurrence
import com.zaroslikov.calendar.domain.CalendarRepeat
import com.zaroslikov.calendar.domain.CalendarTaskKey
import java.time.LocalDate
import java.time.YearMonth

internal data class CalendarState(
    val today: LocalDate,
    val selectedDate: LocalDate = today,
    val events: List<CalendarEvent> = emptyList(),
    val categories: Set<CalendarCategory> = CalendarCategory.entries.toSet(),
    val isLoadingCategories: Boolean = false,
    val categoriesLoadFailed: Boolean = false,
    val savingCategories: Set<CalendarCategory> = emptySet(),
    val savingTasks: Set<CalendarTaskKey> = emptySet(),
    val overlay: CalendarOverlay? = null,
    val savingEvent: Boolean = false,
    val isLoading: Boolean = false,
    val loadFailed: Boolean = false,
) {
    val month: YearMonth get() = YearMonth.from(selectedDate)
}

internal sealed interface CalendarOverlay {
    data object Filters : CalendarOverlay
    data class Details(val event: CalendarEvent) : CalendarOverlay
    data class Scope(val event: CalendarEvent, val delete: Boolean) : CalendarOverlay
    data class Delete(val event: CalendarEvent) : CalendarOverlay
    data class Edit(
        val draft: CalendarDraft,
        val occurrence: CalendarOccurrence? = null,
        val singleOccurrence: Boolean = false,
        val endsOnDate: Boolean = draft.repeatUntil != null,
        val saveFailed: Boolean = false,
    ) : CalendarOverlay {
        val showsRepeatEnd: Boolean get() = !singleOccurrence && draft.repeat != CalendarRepeat.NONE

        @get:StringRes
        val endError: Int?
            get() = when {
                !showsRepeatEnd || !endsOnDate -> null
                draft.repeatUntil == null -> R.string.calendar_repeat_end_required
                draft.repeatUntil < draft.date -> R.string.calendar_repeat_end_error
                else -> null
            }
    }
}

internal sealed interface CalendarIntent {
    data object Retry : CalendarIntent
    data object ShowFilters : CalendarIntent
    data object RetryFilters : CalendarIntent
    data object Dismiss : CalendarIntent
    data object Create : CalendarIntent
    data object Edit : CalendarIntent
    data object Delete : CalendarIntent
    data object ConfirmDelete : CalendarIntent
    data object Save : CalendarIntent
    data class MoveMonth(val delta: Long) : CalendarIntent
    data class SelectDate(val date: LocalDate) : CalendarIntent
    data class Filter(val category: CalendarCategory, val enabled: Boolean) : CalendarIntent
    data class Details(val event: CalendarEvent) : CalendarIntent
    data class ChooseScope(val singleOccurrence: Boolean) : CalendarIntent
    data class UpdateDraft(val draft: CalendarDraft) : CalendarIntent
    data class ChangeEndMode(val endsOnDate: Boolean) : CalendarIntent
    data class SetTaskCompleted(val key: CalendarTaskKey, val completed: Boolean) : CalendarIntent
}
