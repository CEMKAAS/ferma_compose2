package com.zaroslikov.todo.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.domain.model.isTodoOverdue
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.util.TodoDayLabels
import com.zaroslikov.todo.util.dueToSmartDisplay
import java.time.LocalDateTime

@StringRes
internal fun TodoRepeatPeriod.titleRes(): Int = when (this) {
    TodoRepeatPeriod.NONE -> R.string.todo_repeat_none
    TodoRepeatPeriod.DAY -> R.string.todo_repeat_day
    TodoRepeatPeriod.WEEK -> R.string.todo_repeat_week
    TodoRepeatPeriod.MONTH -> R.string.todo_repeat_month
    TodoRepeatPeriod.YEAR -> R.string.todo_repeat_year
}

internal fun todoRepeatMetadataColor(task: DomainTodoTask, isDone: Boolean, now: LocalDateTime): Color =
    if (isTodoOverdue(task.dueDate, task.dueTime, isDone, now)) error_base else gray_7

private val DomainTodoTask.showsRepeatMetadata: Boolean
    get() = dueDate != null && repeatPeriod != TodoRepeatPeriod.NONE

/** Срок и повтор в одной строке карточки задачи. */
@Composable
internal fun TodoTaskScheduleMetadata(
    task: DomainTodoTask,
    isDone: Boolean,
    labels: TodoDayLabels,
    style: TextStyle
) {
    val now = LocalDateTime.now()
    val due = dueToSmartDisplay(task.dueDate, task.dueTime, labels, now.toLocalDate())
    if (due.isEmpty() && !task.showsRepeatMetadata) return

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        if (due.isNotEmpty())
            Text(
                text = due,
                style = style,
                color = todoRepeatMetadataColor(task, isDone, now),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        TodoRepeatMetadata(task, isDone, style = style, now = now, maxLines = 1)
    }
}

/** Одинаковая подпись повтора в строках, карточках и подзадачах. */
@Composable
internal fun TodoRepeatMetadata(
    task: DomainTodoTask,
    isDone: Boolean,
    modifier: Modifier = Modifier,
    style: TextStyle = TODO_ROW_META_STYLE,
    now: LocalDateTime = LocalDateTime.now(),
    maxLines: Int = Int.MAX_VALUE
) {
    if (!task.showsRepeatMetadata) return
    val color = todoRepeatMetadataColor(task, isDone, now)
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Repeat, contentDescription = stringResource(R.string.todo_repeat),
            tint = color, modifier = Modifier.size(14.dp))
        Text(
            text = stringResource(task.repeatPeriod.titleRes()),
            style = style,
            color = color,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}
