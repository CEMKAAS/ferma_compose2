package com.zaroslikov.todo.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoSmartHeader
import com.zaroslikov.todo.domain.model.TodoSmartList
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.ios_blue
import com.zaroslikov.todo.ui.theme.ios_dark_gray
import com.zaroslikov.todo.ui.theme.ios_gray
import com.zaroslikov.todo.ui.theme.ios_red
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.util.isoToDisplay

// Подписи, цвета и иконки умных списков — одинаковые на плитках и на их экранах

@StringRes
fun TodoSmartList.titleRes(): Int = when (this) {
    TodoSmartList.TODAY -> R.string.todo_smart_today
    TodoSmartList.SCHEDULED -> R.string.todo_smart_scheduled
    TodoSmartList.ALL -> R.string.todo_smart_all
    TodoSmartList.COMPLETED -> R.string.todo_smart_completed
}

fun TodoSmartList.color(): Color = when (this) {
    TodoSmartList.TODAY -> ios_blue
    TodoSmartList.SCHEDULED -> ios_red
    TodoSmartList.ALL -> ios_dark_gray
    TodoSmartList.COMPLETED -> ios_gray
}

fun TodoSmartList.icon(): ImageVector = when (this) {
    TodoSmartList.TODAY -> Icons.Rounded.CalendarToday
    TodoSmartList.SCHEDULED -> Icons.Rounded.CalendarMonth
    TodoSmartList.ALL -> Icons.Rounded.Inbox
    TodoSmartList.COMPLETED -> Icons.Rounded.Check
}

/** Заголовок секции: даты — чёрным, список — своим цветом. */
@Composable
fun TodoSmartHeader.title(): String = when (this) {
    TodoSmartHeader.Overdue -> stringResource(R.string.todo_smart_overdue)
    TodoSmartHeader.Today -> stringResource(R.string.todo_smart_today)
    TodoSmartHeader.Tomorrow -> stringResource(R.string.todo_list_tomorrow)
    is TodoSmartHeader.Day -> isoToDisplay(date)
    is TodoSmartHeader.ListTitle -> title
}

fun TodoSmartHeader.color(): Color = when (this) {
    is TodoSmartHeader.ListTitle -> todoColor(colorIndex)
    else -> black_2
}
