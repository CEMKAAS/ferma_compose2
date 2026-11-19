package com.zaroslikov.todo.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.ui.theme.blue_4
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.orang_9
import com.zaroslikov.todo.ui.theme.red_7
import com.zaroslikov.todo.ui.theme.todoColor

// Единые подписи и цвета типов задач — одинаковые на всех экранах

@StringRes
fun TodoDefaultStatus.titleRes(): Int = when (this) {
    TodoDefaultStatus.PLANNED -> R.string.todo_status_planned
    TodoDefaultStatus.IN_PROGRESS -> R.string.todo_status_in_progress
    TodoDefaultStatus.DONE -> R.string.todo_status_done
}

/** Название статуса: у дефолтного — из ресурсов, у пользовательского — сохранённое. */
@Composable
fun DomainTodoStatus.displayTitle(): String =
    defaultCode?.let { stringResource(it.titleRes()) } ?: title

fun DomainTodoStatus.color(): Color = todoColor(colorIndex)

@StringRes
fun TodoPriority.titleRes(): Int = when (this) {
    TodoPriority.NONE -> R.string.todo_priority_none
    TodoPriority.LOW -> R.string.todo_priority_low
    TodoPriority.MEDIUM -> R.string.todo_priority_medium
    TodoPriority.HIGH -> R.string.todo_priority_high
}

fun TodoPriority.color(): Color = when (this) {
    TodoPriority.NONE -> gray_7
    TodoPriority.LOW -> blue_4
    TodoPriority.MEDIUM -> orang_9
    TodoPriority.HIGH -> red_7
}

fun TodoPriority.marks(): String = when (this) {
    TodoPriority.NONE -> ""
    TodoPriority.LOW -> "!"
    TodoPriority.MEDIUM -> "!!"
    TodoPriority.HIGH -> "!!!"
}

@Composable
internal fun TodoPriorityMarks(priority: TodoPriority, style: TextStyle = TODO_ROW_TITLE_STYLE) {
    val colors = when (priority) {
        TodoPriority.NONE -> return
        TodoPriority.LOW -> listOf(Color(0xFF2563EB), Color(0xFF1E40AF))
        TodoPriority.MEDIUM -> listOf(Color(0xFF8A7200), Color(0xFF735F00))
        TodoPriority.HIGH -> listOf(Color(0xFFDC2626), Color(0xFF991B1B))
    }
    Text(
        text = priority.marks(),
        modifier = Modifier.padding(end = 4.dp),
        style = style.copy(brush = Brush.verticalGradient(colors), fontWeight = FontWeight.Bold)
    )
}

/** Подпись связи глазами открытой задачи: у BLOCKS входящая — «Заблокирована задачей». */
@StringRes
fun TodoLinkType.titleRes(isOutgoing: Boolean = true): Int = when (this) {
    TodoLinkType.RELATES -> R.string.todo_link_relates
    TodoLinkType.BLOCKS -> if (isOutgoing) R.string.todo_link_blocks else R.string.todo_link_blocked_by
}

/** Значок роли открытой задачи в связи. */
fun TodoLinkType.icon(isOutgoing: Boolean = true): ImageVector = when (this) {
    TodoLinkType.RELATES -> Icons.Rounded.Link
    TodoLinkType.BLOCKS -> if (isOutgoing) Icons.Rounded.VpnKey else Icons.Rounded.Lock
}
