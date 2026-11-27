package com.zaroslikov.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StickyNote2
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Recycling
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.zaroslikov.calendar.R
import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.calendar.domain.CalendarRepeat
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val calendarLocale = Locale.forLanguageTag("ru-RU")
internal val calendarYearRange = 1..9999

private val fullDateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", calendarLocale)
private val dayHeadingFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM", calendarLocale)
private val monthNameFormat = DateTimeFormatter.ofPattern("LLLL", calendarLocale)
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", calendarLocale)

internal fun LocalDate.calendarFullDate(): String = format(fullDateFormat)
internal fun LocalDate.calendarDayHeading(): String = format(dayHeadingFormat).initialCapital()
internal fun YearMonth.calendarMonthName(): String = format(monthNameFormat).initialCapital()
private fun String.initialCapital(): String = replaceFirstChar { it.titlecase(calendarLocale) }

@Composable
internal fun calendarTimeLabel(time: LocalTime?): String =
    time?.format(timeFormat) ?: stringResource(R.string.calendar_all_day)

internal val CalendarCategory.label: Int
    get() = when (this) {
        CalendarCategory.TASKS -> R.string.calendar_tasks
        CalendarCategory.VACCINATIONS -> R.string.calendar_vaccinations
        CalendarCategory.BIRTHS -> R.string.calendar_births
        CalendarCategory.BIRTHDAYS -> R.string.calendar_birthdays
        CalendarCategory.PROCESSING_AND_FEED -> R.string.calendar_processing_feed
        CalendarCategory.PERSONAL -> R.string.calendar_personal
    }

internal val CalendarEvent.label: Int
    get() = when (kind) {
        CalendarEventKind.TODO_TASK -> R.string.calendar_task_todo
        CalendarEventKind.CRM_TASK -> R.string.calendar_task_crm
        CalendarEventKind.VACCINATION -> R.string.calendar_vaccination
        CalendarEventKind.NEXT_VACCINATION -> R.string.calendar_next_vaccination
        CalendarEventKind.BIRTH -> R.string.calendar_birth
        CalendarEventKind.EXPECTED_BIRTH -> R.string.calendar_expected_birth
        CalendarEventKind.BIRTHDAY -> R.string.calendar_birthday
        CalendarEventKind.RECYCLING_READY -> R.string.calendar_recycling_ready
        CalendarEventKind.FEED_END -> R.string.calendar_feed_end
        CalendarEventKind.PERSONAL -> R.string.calendar_personal_event
    }

internal val CalendarRepeat.label: Int
    get() = when (this) {
        CalendarRepeat.NONE -> R.string.calendar_repeat_none
        CalendarRepeat.DAILY -> R.string.calendar_repeat_daily
        CalendarRepeat.WEEKLY -> R.string.calendar_repeat_weekly
        CalendarRepeat.MONTHLY -> R.string.calendar_repeat_monthly
        CalendarRepeat.YEARLY -> R.string.calendar_repeat_yearly
    }

val CalendarCategory.color: Color
    get() = when (this) {
        CalendarCategory.TASKS -> Color(0xFF155DFC)
        CalendarCategory.VACCINATIONS -> Color(0xFFFF2056)
        CalendarCategory.BIRTHS -> Color(0xFF00A63E)
        CalendarCategory.BIRTHDAYS -> Color(0xFF9810FA)
        CalendarCategory.PROCESSING_AND_FEED -> Color(0xFFE17100)
        CalendarCategory.PERSONAL -> Color(0xFF009689)
    }

@Composable
internal fun CategoryDot(category: CalendarCategory, modifier: Modifier) {
    Box(modifier.background(category.color, CircleShape))
}

@Composable
internal fun CalendarEventIcon(event: CalendarEvent, modifier: Modifier = Modifier) {
    val icon = when (event.kind) {
        CalendarEventKind.TODO_TASK, CalendarEventKind.CRM_TASK -> Icons.Outlined.TaskAlt
        CalendarEventKind.VACCINATION -> Icons.Outlined.Vaccines
        CalendarEventKind.NEXT_VACCINATION -> Icons.Outlined.MedicalServices
        CalendarEventKind.BIRTH -> Icons.Outlined.Pets
        CalendarEventKind.EXPECTED_BIRTH -> Icons.Outlined.EventAvailable
        CalendarEventKind.BIRTHDAY -> Icons.Outlined.Cake
        CalendarEventKind.RECYCLING_READY -> Icons.Outlined.Recycling
        CalendarEventKind.FEED_END -> Icons.Outlined.Grass
        CalendarEventKind.PERSONAL -> Icons.AutoMirrored.Outlined.StickyNote2
    }
    Icon(icon, contentDescription = null, modifier = modifier, tint = event.category.color)
}
