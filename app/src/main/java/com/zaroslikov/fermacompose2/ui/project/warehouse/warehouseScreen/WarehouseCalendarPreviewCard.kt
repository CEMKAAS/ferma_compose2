package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.ui.color
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.blue_16
import com.zaroslikov.fermacompose2.gray_9
import com.zaroslikov.fermacompose2.gray_7
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val CalendarDateInk = Color(0xFF191919)
private val CalendarTodayLabel = Color(0xFFD43A30)
private val CalendarTodayEventFill = Color(0xFFE6F4FB)
private val CalendarTodayEventInk = Color(0xFF165A7E)
private val CalendarEntryShape = RoundedCornerShape(8.dp)

@Composable
internal fun WarehouseCalendarPreviewCardContent(preview: WarehouseCalendarPreview?) {
    val today = preview?.today ?: WarehouseCalendarDay(LocalDate.now())
    val upcoming = preview?.upcoming
    val showUpcoming = preview != null && !preview.loadFailed
    val todayStatus = stringResource(
        when {
            preview == null -> R.string.warehouse_calendar_loading
            preview.loadFailed -> R.string.warehouse_calendar_load_failed
            else -> R.string.warehouse_calendar_no_entries_today
        }
    )
    BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        val fontScale = LocalDensity.current.fontScale.coerceAtLeast(1f)
        if (showUpcoming && maxWidth >= 270.dp * fontScale) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WarehouseCalendarToday(today, todayStatus, Modifier.weight(2f))
                WarehouseCalendarUpcoming(upcoming, today.date, Modifier.weight(3f))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                WarehouseCalendarToday(today, todayStatus)
                if (showUpcoming) WarehouseCalendarUpcoming(upcoming, today.date)
            }
        }
    }
}

@Composable
private fun WarehouseCalendarToday(
    day: WarehouseCalendarDay,
    status: String,
    modifier: Modifier = Modifier,
) {
    WarehouseCalendarDayColumn(
        day = day,
        label = day.date.format(weekdayFormat),
        dateLabel = day.date.dayOfMonth.toString(),
        emptyMessage = status,
        modifier = modifier,
        isToday = true,
    )
}

@Composable
private fun WarehouseCalendarUpcoming(
    day: WarehouseCalendarDay?,
    today: LocalDate,
    modifier: Modifier = Modifier,
) {
    if (day == null) {
        Text(
            text = stringResource(R.string.warehouse_calendar_no_upcoming),
            color = gray_7,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 15.sp, letterSpacing = 0.sp),
            modifier = modifier,
        )
        return
    }
    val tomorrow = day.date == today.plusDays(1)
    WarehouseCalendarDayColumn(
        day = day,
        label = if (tomorrow) stringResource(R.string.warehouse_calendar_tomorrow) else day.date.format(weekdayFormat),
        dateLabel = if (tomorrow) null else day.date.format(shortDateFormat),
        modifier = modifier,
    )
}

@Composable
private fun WarehouseCalendarDayColumn(
    day: WarehouseCalendarDay,
    label: String,
    dateLabel: String?,
    emptyMessage: String? = null,
    modifier: Modifier = Modifier,
    isToday: Boolean = false,
) {
    val secondaryStyle = MaterialTheme.typography.bodySmall.copy(lineHeight = 15.sp, letterSpacing = 0.sp)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label.uppercase(calendarLocale),
            modifier = Modifier.semantics { heading() },
            color = if (isToday) CalendarTodayLabel else gray_7,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 13.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.2.sp,
                hyphens = Hyphens.Auto,
                localeList = russianTextLocale,
            ),
            fontWeight = FontWeight.SemiBold,
        )
        if (dateLabel != null) {
            Text(
                text = dateLabel,
                color = if (isToday) CalendarDateInk else gray_7,
                style = if (isToday) {
                    MaterialTheme.typography.displaySmall.copy(lineHeight = 38.sp, letterSpacing = 0.sp)
                } else {
                    secondaryStyle
                },
                fontWeight = FontWeight.Normal,
            )
        }
        if (day.entries.isEmpty()) {
            emptyMessage?.let {
                Text(it, color = gray_7, style = secondaryStyle)
            }
        } else {
            day.entries.forEach { CalendarPreviewEntry(it, isToday) }
            if (day.remainingCount > 0) {
                Text(
                    text = stringResource(R.string.warehouse_calendar_more_entries, day.remainingCount),
                    color = gray_7,
                    style = secondaryStyle,
                )
            }
        }
    }
}

@Composable
private fun CalendarPreviewEntry(event: CalendarEvent, isToday: Boolean) {
    val time = event.time?.format(timeFormat)
    val markerHeight = if (isToday) Modifier.height(IntrinsicSize.Min) else Modifier
    val entryStyle = MaterialTheme.typography.bodySmall.copy(
        lineHeight = 15.sp,
        letterSpacing = 0.sp,
        localeList = russianTextLocale,
        hyphens = Hyphens.Auto,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(markerHeight)
            .background(if (isToday) CalendarTodayEventFill else gray_9, CalendarEntryShape)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val marker = when {
            isToday -> Modifier.fillMaxHeight().width(2.dp).background(event.category.color, CircleShape)
            event.task != null -> Modifier.size(12.dp).border(1.dp, event.category.color, CircleShape)
            else -> Modifier.size(8.dp).background(event.category.color, CircleShape)
        }
        Box(marker)
        Column(Modifier.weight(1f)) {
            Text(
                text = event.title,
                color = if (isToday) CalendarTodayEventInk else gray_7,
                style = entryStyle,
                fontWeight = FontWeight.SemiBold,
            )
            if (isToday && time != null) {
                Text(time, color = blue_16, style = entryStyle)
            }
        }
        if (!isToday && time != null) {
            Text(time, color = gray_7, style = entryStyle, maxLines = 1)
        }
    }
}

private val calendarLocale = Locale.forLanguageTag("ru-RU")
private val russianTextLocale = LocaleList("ru-RU")
private val weekdayFormat = DateTimeFormatter.ofPattern("EEEE", calendarLocale)
private val shortDateFormat = DateTimeFormatter.ofPattern("d MMMM", calendarLocale)
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", calendarLocale)
