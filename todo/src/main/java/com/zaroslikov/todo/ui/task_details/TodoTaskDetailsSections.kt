package com.zaroslikov.todo.ui.task_details

import com.zaroslikov.todo.ui.components.animateTodoTextSize
import com.zaroslikov.todo.ui.components.TodoTaskListLabel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Repeat
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material.icons.rounded.UnfoldMore
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoComment
import com.zaroslikov.todo.domain.model.DomainTodoLinkedTask
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.ui.components.TodoDoneCircle
import com.zaroslikov.todo.ui.components.TodoTaskLinkCounters
import com.zaroslikov.todo.ui.components.TodoTaskTags
import com.zaroslikov.todo.ui.components.TodoPriorityMarks
import com.zaroslikov.todo.ui.components.TodoRepeatMetadata
import com.zaroslikov.todo.ui.components.TodoAssigneesMetadata
import com.zaroslikov.todo.ui.components.icon
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.domain.model.isTodoOverdue
import com.zaroslikov.todo.domain.model.isDone
import com.zaroslikov.todo.domain.model.todoToday
import com.zaroslikov.todo.ui.components.TodoBottomSheet
import com.zaroslikov.todo.ui.components.TodoChip
import com.zaroslikov.todo.ui.components.TodoSegmentedButtons
import com.zaroslikov.todo.ui.components.TodoColorDot
import com.zaroslikov.todo.ui.components.TodoListBadge
import com.zaroslikov.todo.ui.components.TodoDatePickerDialog
import com.zaroslikov.todo.ui.components.TodoTextField
import com.zaroslikov.todo.ui.components.TodoTimePickerDialog
import com.zaroslikov.todo.ui.components.color
import com.zaroslikov.todo.ui.components.displayTitle
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.blue_4
import com.zaroslikov.todo.ui.theme.grey_2
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_shamrock
import com.zaroslikov.todo.ui.theme.red_7
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import com.zaroslikov.todo.ui.theme.gray_6
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.orang_9
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.text_14
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.white
import com.zaroslikov.todo.util.TodoDayLabels
import kotlin.math.roundToInt
import com.zaroslikov.todo.util.dueToDisplay
import com.zaroslikov.todo.util.dueToSmartDisplay
import com.zaroslikov.todo.util.isoDateTimeToDisplay
import com.zaroslikov.todo.util.timeToIso
import java.time.LocalTime

// Разделы формы используют палитру, типографику и поля основного приложения.

private val DETAILS_TEXT: TextStyle
    @Composable get() = text_16
private val DETAILS_META: TextStyle
    @Composable get() = text_14

/** Разделы шторки сверху вниз; у подзадачи нет своих подзадач. */
@Composable
internal fun ColumnScope.TodoTaskDetailsContent(
    state: TodoTaskDetailsState,
    data: TodoTaskDetailsData,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    val labels = TodoDayLabels(
        today = stringResource(R.string.todo_list_today),
        tomorrow = stringResource(R.string.todo_list_tomorrow),
        yesterday = stringResource(R.string.todo_list_yesterday)
    )
    TodoTextGroup(state = state, accent = accent, onIntent = onIntent)
    TodoDueGroup(data = data, accent = accent, labels = labels, onIntent = onIntent)
    TodoParametersGroup(data = data, onIntent = onIntent)
    TodoAssigneesGroup(state = state, accent = accent, onIntent = onIntent)
    if (!data.isSubtask)
        TodoSubtasksGroup(state = state, data = data, accent = accent, labels = labels, onIntent = onIntent)
    TodoLinksGroup(data = data, accent = accent, onIntent = onIntent)
    TodoCommentsGroup(state = state, data = data, accent = accent, onIntent = onIntent)
    OutlinedButton(
        onClick = { onIntent(TodoTaskDetailsIntent.ShowDeleteDialog) },
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).heightIn(min = 48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = red_7)
    ) {
        Icon(Icons.Rounded.Delete, contentDescription = null)
        Text(stringResource(R.string.todo_details_delete), style = textBold_16, modifier = Modifier.padding(start = 8.dp))
    }
}

/** Поля формы приложения; черновик сохраняется при потере фокуса. */
@Composable
private fun TodoTextGroup(
    state: TodoTaskDetailsState,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(Modifier.animateTodoTextSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TodoDetailsField(
            value = state.title,
            onValueChange = { onIntent(TodoTaskDetailsIntent.TitleChange(it)) },
            placeholder = stringResource(R.string.todo_field_title),
            accent = accent,
            onFocusLost = { onIntent(TodoTaskDetailsIntent.CommitText) },
            singleLine = false,
            minLines = 1,
            imeAction = ImeAction.Done,
            onImeAction = { focusManager.clearFocus() },
            modifier = Modifier
        )
        TodoDetailsField(
            value = state.note,
            onValueChange = { onIntent(TodoTaskDetailsIntent.NoteChange(it)) },
            placeholder = stringResource(R.string.todo_field_note),
            accent = accent,
            onFocusLost = { onIntent(TodoTaskDetailsIntent.CommitText) },
            singleLine = false,
            style = DETAILS_TEXT.copy(fontSize = 15.sp, lineHeight = 20.sp),
            modifier = Modifier
        )
    }
}

/** Срок: «Дата» и «Время» с переключателями; тап по значению — выбор в диалоге. */
@Composable
internal fun TodoDueGroup(
    data: TodoTaskDetailsData,
    accent: Color,
    labels: TodoDayLabels,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    val task = data.task
    var showDate by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }
    TodoSectionLabel(text = stringResource(R.string.todo_due))
    TodoDetailsGroup {
        TodoSwitchRow(
            icon = Icons.Rounded.CalendarMonth,
            iconColor = red_7,
            title = stringResource(R.string.todo_due_date),
            value = task.dueDate?.let { dueToSmartDisplay(it, null, labels) },
            checked = task.dueDate != null,
            accent = accent,
            onCheckedChange = { on -> onIntent(TodoTaskDetailsIntent.SetDueDate(if (on) todoToday() else null)) },
            onValueClick = { showDate = true }
        )
        TodoGroupDivider()
        TodoSwitchRow(
            icon = Icons.Rounded.Schedule,
            iconColor = blue_4,
            title = stringResource(R.string.todo_due_time),
            value = task.dueTime,
            checked = task.dueTime != null,
            enabled = task.dueDate != null,
            accent = accent,
            // Включённое время — ближайший следующий час
            onCheckedChange = { on ->
                onIntent(TodoTaskDetailsIntent.SetDueTime(if (on) timeToIso((LocalTime.now().hour + 1) % 24, 0) else null))
            },
            onValueClick = { showTime = true }
        )
        if (task.dueDate != null) {
            TodoGroupDivider()
            TodoPropertyRow(
                icon = Icons.Rounded.Repeat, iconColor = accent,
                title = stringResource(R.string.todo_repeat),
                onClick = { onIntent(TodoTaskDetailsIntent.OpenRepeatSheet) }
            ) {
                Text(stringResource(task.repeatPeriod.titleRes()), style = text_16, color = black_2)
                task.repeatEndDate?.let {
                    Text(stringResource(R.string.todo_repeat_until, com.zaroslikov.todo.util.isoToDisplay(it)), style = text_14, color = gray_7)
                }
            }
        }
    }
    if (showDate)
        TodoDatePickerDialog(
            value = task.dueDate.orEmpty(),
            onConfirm = { onIntent(TodoTaskDetailsIntent.SetDueDate(it)) },
            onDismiss = { showDate = false },
            accent = accent
        )
    if (showTime)
        TodoTimePickerDialog(
            value = task.dueTime.orEmpty(),
            onConfirm = { onIntent(TodoTaskDetailsIntent.SetDueTime(it)) },
            onDismiss = { showTime = false },
            accent = accent
        )
}

/** Параметры задачи: список, подсписок, статус, приоритет и теги в одной группе. */
@Composable
private fun TodoParametersGroup(
    data: TodoTaskDetailsData,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    val status = data.item.status
    val priority = data.task.priority
    TodoSectionLabel(text = stringResource(R.string.todo_details_parameters))
    TodoDetailsGroup {
        TodoTaskListRow(data, onIntent)
        if (!data.isSubtask && (data.sections.isNotEmpty() || data.task.sectionId != null)) {
            TodoGroupDivider()
            TodoSectionRow(data = data, accent = todoColor(data.listColorIndex), onIntent = onIntent)
        }
        TodoGroupDivider()
        TodoMenuRow(
            icon = Icons.Rounded.Timelapse,
            iconColor = status?.color() ?: gray_7,
            title = stringResource(R.string.todo_status),
            value = status?.displayTitle() ?: stringResource(R.string.todo_dash),
            valueColor = status?.color() ?: gray_7
        ) { dismiss ->
            data.statuses.forEach { option ->
                TodoMenuOption(
                    text = option.displayTitle(),
                    selected = option.id == data.task.statusId,
                    leading = { TodoColorDot(color = option.color(), size = 10.dp) },
                    onClick = {
                        dismiss()
                        if (option.id != data.task.statusId) onIntent(TodoTaskDetailsIntent.SetStatus(option.id))
                    }
                )
            }
        }
        TodoGroupDivider()
        TodoMenuRow(
            icon = Icons.Rounded.Flag,
            iconColor = orang_9,
            title = stringResource(R.string.todo_priority),
            value = stringResource(priority.titleRes()),
            valueColor = if (priority == TodoPriority.NONE) gray_7 else priority.color()
        ) { dismiss ->
            TodoPriority.entries.forEach { option ->
                TodoMenuOption(
                    text = stringResource(option.titleRes()),
                    selected = option == priority,
                    onClick = {
                        dismiss()
                        if (option != priority) onIntent(TodoTaskDetailsIntent.SetPriority(option))
                    }
                )
            }
        }
        TodoGroupDivider()
        TodoTagsRow(data = data, onIntent = onIntent)
    }
}

/** Подзадача принадлежит списку родителя и отдельно не переносится. */
@Composable
internal fun TodoTaskListRow(data: TodoTaskDetailsData, onIntent: (TodoTaskDetailsIntent) -> Unit) {
    val accent = todoColor(data.listColorIndex)
    if (data.isSubtask) {
        TodoPropertyRow(
            icon = Icons.Rounded.ViewAgenda,
            iconColor = accent,
            title = stringResource(R.string.todo_list_title),
            onClick = null,
            showIndicator = false
        ) { Text(text = data.listTitle, style = text_16, color = accent) }
    } else {
        TodoMenuRow(
            icon = Icons.Rounded.ViewAgenda,
            iconColor = accent,
            title = stringResource(R.string.todo_list_title),
            value = data.listTitle,
            valueColor = accent,
            matchRowWidth = true
        ) { dismiss ->
            data.projectLists.forEach { option ->
                TodoMenuOption(
                    text = option.title,
                    selected = option.id == data.task.listId,
                    onClick = {
                        dismiss()
                        if (option.id != data.task.listId) onIntent(TodoTaskDetailsIntent.SetList(option.id))
                    }
                )
            }
        }
    }
}

/** Подсписок задачи — строка с меню «Без подсписка» + подсписки списка. */
@Composable
private fun TodoSectionRow(
    data: TodoTaskDetailsData,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    val current = data.sections.firstOrNull { it.id == data.task.sectionId }
    TodoMenuRow(
        icon = Icons.Rounded.ViewAgenda,
        iconColor = accent,
        title = stringResource(R.string.todo_section_field),
        value = current?.title ?: stringResource(R.string.todo_section_none),
        valueColor = if (current != null) accent else gray_7,
        matchRowWidth = true
    ) { dismiss ->
        TodoMenuOption(
            text = stringResource(R.string.todo_section_none),
            selected = current == null,
            onClick = {
                dismiss()
                if (current != null) onIntent(TodoTaskDetailsIntent.SetSection(null))
            }
        )
        data.sections.forEach { option ->
            TodoMenuOption(
                text = option.title,
                selected = option.id == current?.id,
                onClick = {
                    dismiss()
                    if (option.id != current?.id) onIntent(TodoTaskDetailsIntent.SetSection(option.id))
                }
            )
        }
    }
}

/** Теги — такая же строка свойства, как приоритет, с отдельной шторкой выбора. */
@Composable
private fun TodoTagsRow(
    data: TodoTaskDetailsData,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val openTags: () -> Unit = {
        focusManager.clearFocus()
        onIntent(TodoTaskDetailsIntent.OpenTagsSheet)
    }
    TodoPropertyRow(
        icon = Icons.Rounded.Label,
        iconColor = green_shamrock,
        title = stringResource(R.string.todo_tags_title),
        onClick = openTags,
        showIndicator = false
    ) {
        if (data.item.tags.isEmpty())
            Text(stringResource(R.string.todo_tags_not_specified), style = text_16, color = gray_7)
        else
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                data.item.tags.forEach { tag ->
                    // Нажатие обрабатывает вся строка с минимальной высотой 52dp.
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(todoColor(tag.colorIndex))
                            .semantics { contentDescription = tag.title }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = tag.title, style = text_14, color = white)
                    }
                }
            }
    }
}

/** Все теги проекта; выбор меняет черновик и применяется кнопкой «Сохранить». */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoTagsSheet(
    data: TodoTaskDetailsData,
    selectedTagIds: Set<Long> = data.tagIds,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    TodoBottomSheet(
        title = stringResource(R.string.todo_tags_title),
        onDismiss = { onIntent(TodoTaskDetailsIntent.CloseTagsSheet) },
        headerActions = {
            IconButton(onClick = { onIntent(TodoTaskDetailsIntent.Close) }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.todo_tags_close_all),
                    tint = gray_7
                )
            }
        },
        footer = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { onIntent(TodoTaskDetailsIntent.ResetTags) },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = green_shamrock)
                ) {
                    Text(stringResource(R.string.todo_tags_reset), style = textBold_16)
                }
                Button(
                    onClick = { onIntent(TodoTaskDetailsIntent.SaveTags) },
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green_shamrock, contentColor = white)
                ) {
                    Text(stringResource(R.string.todo_tags_close), style = textBold_16)
                }
            }
        }
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            data.tags.forEach { tag ->
                TodoChip(
                    text = tag.title,
                    selected = tag.id in selectedTagIds,
                    onClick = { onIntent(TodoTaskDetailsIntent.ToggleTag(tag.id)) },
                    color = todoColor(tag.colorIndex),
                    fillWithColor = true
                )
            }
            TodoChip(
                text = stringResource(R.string.todo_edit_new_tag),
                selected = false,
                onClick = { onIntent(TodoTaskDetailsIntent.StartNewTag) },
                leadingIcon = Icons.Rounded.Add
            )
        }
    }
}

/** Снимок строки сохраняется до конца анимации скрытия или удаления. */
private class TodoAnimatedSubtask(subtask: DomainTodoTaskItem, initiallyVisible: Boolean) {
    var subtask by mutableStateOf(subtask)
    val visibility = MutableTransitionState(initiallyVisible).apply { targetState = true }
}

/** Подзадачи с круглой отметкой; тап по названию открывает подробности. */
@Composable
private fun TodoSubtasksGroup(
    state: TodoTaskDetailsState,
    data: TodoTaskDetailsData,
    accent: Color,
    labels: TodoDayLabels,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    var expanded by remember(data.task.id) { mutableStateOf(false) }
    val visibleSubtasks = if (expanded) data.subtasks else data.subtasks.take(3)
    val visibleIds = visibleSubtasks.mapTo(mutableSetOf()) { it.task.id }
    val subtasks = remember(data.task.id) {
        mutableStateListOf<TodoAnimatedSubtask>().apply {
            addAll(visibleSubtasks.map { TodoAnimatedSubtask(it, initiallyVisible = true) })
        }
    }
    LaunchedEffect(data.task.id, data.subtasks, visibleSubtasks) {
        val currentById = visibleSubtasks.associateBy { it.task.id }
        subtasks.forEach { row ->
            val current = currentById[row.subtask.task.id]
            if (current != null) row.subtask = current
            row.visibility.targetState = current != null
        }
        val displayedIds = subtasks.mapTo(mutableSetOf()) { it.subtask.task.id }
        visibleSubtasks.filter { it.task.id !in displayedIds }.forEach {
            subtasks.add(TodoAnimatedSubtask(it, initiallyVisible = false))
        }
        // Живые строки следуют порядку репозитория; исчезающие сохраняют прежнее место.
        val previousIndices = subtasks.mapIndexed { index, row -> row.subtask.task.id to index }.toMap()
        val currentIndices = data.subtasks.mapIndexed { index, item -> item.task.id to index }.toMap()
        subtasks.sortBy { currentIndices[it.subtask.task.id] ?: previousIndices.getValue(it.subtask.task.id) }
    }
    LaunchedEffect(data.task.id) {
        snapshotFlow {
            subtasks.filter { it.visibility.isIdle && !it.visibility.currentState && !it.visibility.targetState }
        }.collect { finished -> subtasks.removeAll(finished.toSet()) }
    }
    val progress = if (data.subtasks.isNotEmpty()) data.subtaskDoneCount.toFloat() / data.subtasks.size else 0f
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TodoSectionLabel(
            text = if (data.subtasks.isEmpty()) stringResource(R.string.todo_card_subtasks)
            else stringResource(R.string.todo_card_subtasks_progress, data.subtaskDoneCount, data.subtasks.size)
        )
        AnimatedVisibility(
            visible = data.subtasks.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    color = accent,
                    trackColor = accent.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.todo_card_subtasks_percent, (progress * 100).roundToInt()),
                    style = text_14,
                    color = gray_7
                )
            }
        }
        if (subtasks.isNotEmpty()) {
            TodoDetailsGroup {
                subtasks.forEachIndexed { index, row ->
                    key(data.task.id, row.subtask.task.id) {
                        AnimatedVisibility(
                            visibleState = row.visibility,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                val subtask = row.subtask
                                val isInteractive = subtask.task.id in visibleIds
                                if (index > 0) TodoGroupDivider()
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(48.dp).semantics(mergeDescendants = true) {
                                            contentDescription = subtask.task.title
                                        },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TodoDoneCircle(
                                            done = subtask.isDone,
                                            accent = accent,
                                            onToggle = {
                                                if (isInteractive) onIntent(TodoTaskDetailsIntent.ToggleSubtaskDone(subtask.task.id))
                                            }
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f)
                                            .clickable(enabled = isInteractive) { onIntent(TodoTaskDetailsIntent.NavigateToTask(subtask.task.id)) }
                                            .heightIn(min = 48.dp).padding(vertical = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
                                    ) {
                                        Row {
                                            TodoPriorityMarks(subtask.task.priority, style = text_16)
                                            Text(
                                                text = subtask.task.title, style = text_16,
                                                color = if (subtask.isDone) gray_7 else black_2,
                                                textDecoration = if (subtask.isDone) TextDecoration.LineThrough else null,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        val due = dueToSmartDisplay(subtask.task.dueDate, subtask.task.dueTime, labels)
                                        if (due.isNotEmpty()) Text(
                                            text = due, style = text_14,
                                            color = if (isTodoOverdue(subtask.task.dueDate, subtask.task.dueTime, subtask.isDone)) red_7 else gray_7
                                        )
                                        TodoRepeatMetadata(subtask.task, subtask.isDone, style = text_14)
                                        TodoAssigneesMetadata(subtask.task, style = text_14)
                                        subtask.status?.takeIf { !it.isDone && it.defaultCode != TodoDefaultStatus.PLANNED }?.let { status ->
                                            Text(status.displayTitle(), style = text_14, color = status.color())
                                        }
                                        TodoTaskTags(subtask.tags)
                                        if (subtask.commentCount > 0 || subtask.linkCount > 0)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (subtask.commentCount > 0) {
                                                    val description = stringResource(R.string.todo_list_comments, subtask.commentCount)
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                        modifier = Modifier.clearAndSetSemantics { contentDescription = description }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.ChatBubbleOutline,
                                                            contentDescription = null,
                                                            tint = gray_7,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Text(text = subtask.commentCount.toString(), style = text_14, color = gray_7)
                                                    }
                                                }
                                                TodoTaskLinkCounters(subtask)
                                            }
                                        if (subtask.task.note.isNotBlank())
                                            Text(text = subtask.task.note, style = text_14, color = gray_7, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    }
                                    IconButton(
                                        enabled = isInteractive,
                                        onClick = { onIntent(TodoTaskDetailsIntent.DeleteSubtask(subtask.task.id)) }
                                    ) {
                                        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.todo_details_subtask_delete), tint = gray_7)
                                    }
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = data.subtasks.size > 3,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        TodoGroupDivider()
                        TextButton(
                            enabled = data.subtasks.size > 3,
                            onClick = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = accent)
                        ) {
                            Text(
                                text = stringResource(if (expanded) R.string.todo_card_subtasks_collapse else R.string.todo_card_subtasks_show_all),
                                style = DETAILS_TEXT
                            )
                        }
                    }
                }
            }
        }
    }
    val text = state.quickSubtask
    TodoDetailsField(
        value = text,
        onValueChange = { onIntent(TodoTaskDetailsIntent.QuickSubtaskChange(it)) },
        placeholder = stringResource(R.string.todo_card_subtask_quick),
        accent = accent,
        usePlaceholder = true,
        imeAction = ImeAction.Done,
        onImeAction = { onIntent(TodoTaskDetailsIntent.AddQuickSubtask) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            AnimatedVisibility(
                visible = text.isNotBlank(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(onClick = { onIntent(TodoTaskDetailsIntent.AddQuickSubtask) }, enabled = text.isNotBlank()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = stringResource(R.string.todo_add),
                        tint = accent
                    )
                }
            }
        }
    )
}

/** Связи: тип, название и список; тап — шторка переключается на связанную задачу. */
@Composable
private fun TodoLinksGroup(
    data: TodoTaskDetailsData,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    TodoSectionLabel(text = stringResource(R.string.todo_card_links))
    TodoDetailsGroup {
        data.links.forEach { link ->
            TodoLinkRow(
                link = link,
                onClick = { onIntent(TodoTaskDetailsIntent.NavigateToTask(link.task.id)) },
                onDelete = { onIntent(TodoTaskDetailsIntent.DeleteLink(link)) }
            )
            TodoGroupDivider()
        }
        TodoActionRow(
            text = stringResource(R.string.todo_card_link_add),
            accent = accent,
            onClick = { onIntent(TodoTaskDetailsIntent.OpenLinkSheet) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoLinkRow(
    link: DomainTodoLinkedTask,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isDone = link.status?.isDone == true
    val isIncomingBlocker = link.link.type == TodoLinkType.BLOCKS && !link.isOutgoing
    val iconTint = when {
        isDone && link.link.type == TodoLinkType.BLOCKS && link.isOutgoing -> Color(0xFFD4A017)
        isDone -> green_shamrock
        isIncomingBlocker -> red_7
        else -> gray_7
    }
    val tooltipText = stringResource(
        R.string.todo_link_task_tooltip,
        stringResource(link.link.type.titleRes(link.isOutgoing)),
        link.status?.displayTitle() ?: stringResource(R.string.todo_link_status_unknown)
    )
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
            tooltip = { PlainTooltip { Text(tooltipText) } },
            state = tooltipState
        ) {
            IconButton(onClick = { scope.launch { tooltipState.show() } }) {
                Icon(
                    imageVector = if (isIncomingBlocker && isDone) Icons.Rounded.LockOpen
                        else link.link.type.icon(link.isOutgoing),
                    contentDescription = tooltipText,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = link.task.title,
                style = DETAILS_TEXT,
                color = if (isIncomingBlocker && !isDone) red_7 else black_2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (link.listTitle.isNotBlank())
                TodoTaskListLabel(title = link.listTitle, icon = link.listIcon, style = DETAILS_META)
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.todo_card_link_delete),
                tint = gray_7,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** Строка остаётся в карточке до завершения анимации удаления. */
private class TodoAnimatedComment(comment: DomainTodoComment, initiallyVisible: Boolean) {
    var comment by mutableStateOf(comment)
    val visibility = MutableTransitionState(initiallyVisible).apply { targetState = true }
}

/** Новые комментарии сверху; по умолчанию видны два последних, ввод — под карточкой. */
@Composable
private fun TodoCommentsGroup(
    state: TodoTaskDetailsState,
    data: TodoTaskDetailsData,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    var expanded by remember(data.task.id) { mutableStateOf(false) }
    val sortedComments = remember(data.comments) {
        data.comments.sortedWith(compareByDescending<DomainTodoComment> { it.createdAt }.thenByDescending { it.id })
    }
    val visibleComments = if (expanded) sortedComments else sortedComments.take(2)
    val comments = remember(data.task.id) {
        mutableStateListOf<TodoAnimatedComment>().apply {
            addAll(visibleComments.map { TodoAnimatedComment(it, initiallyVisible = true) })
        }
    }
    LaunchedEffect(data.task.id, visibleComments) {
        val currentById = visibleComments.associateBy { it.id }
        comments.forEach { row ->
            val current = currentById[row.comment.id]
            if (current != null) row.comment = current
            row.visibility.targetState = current != null
        }
        val displayedIds = comments.mapTo(mutableSetOf()) { it.comment.id }
        visibleComments.filter { it.id !in displayedIds }.forEach {
            comments.add(TodoAnimatedComment(it, initiallyVisible = false))
        }
        comments.sortWith(compareByDescending<TodoAnimatedComment> { it.comment.createdAt }.thenByDescending { it.comment.id })
    }
    LaunchedEffect(data.task.id) {
        snapshotFlow {
            comments.filter { it.visibility.isIdle && !it.visibility.currentState && !it.visibility.targetState }
        }.collect { finished -> comments.removeAll(finished.toSet()) }
    }
    TodoSectionLabel(text = stringResource(R.string.todo_card_comments))
    if (comments.isNotEmpty()) {
        TodoDetailsGroup {
            comments.forEachIndexed { index, row ->
                key(data.task.id, row.comment.id) {
                    AnimatedVisibility(
                        visibleState = row.visibility,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            if (index > 0) TodoGroupDivider()
                            TodoCommentRow(comment = row.comment, onIntent = onIntent)
                        }
                    }
                }
            }
            if (data.comments.size > 2) {
                TodoGroupDivider()
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = accent)
                ) {
                    Text(
                        text = stringResource(if (expanded) R.string.todo_card_comments_collapse else R.string.todo_card_comments_show_all),
                        style = DETAILS_TEXT
                    )
                }
            }
        }
    }
    val text = state.commentText
    TodoDetailsField(
        value = text,
        onValueChange = { onIntent(TodoTaskDetailsIntent.CommentTextChange(it)) },
        placeholder = stringResource(R.string.todo_card_comment_hint),
        accent = accent,
        singleLine = false,
        minLines = 1,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            AnimatedVisibility(
                visible = text.isNotBlank(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(onClick = { onIntent(TodoTaskDetailsIntent.SendComment) }, enabled = text.isNotBlank()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = stringResource(R.string.todo_card_comment_send),
                        tint = accent
                    )
                }
            }
        }
    )
}

@Composable
private fun TodoCommentRow(
    comment: DomainTodoComment,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = comment.text, style = DETAILS_TEXT.copy(fontSize = 16.sp), color = black_2)
            val date = isoDateTimeToDisplay(comment.createdAt)
            Text(
                text = if (comment.editedAt != null) stringResource(R.string.todo_card_comment_date_edited, date) else date,
                style = DETAILS_META,
                color = gray_7
            )
        }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.todo_more),
                    tint = gray_7
                )
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, containerColor = white) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.todo_edit_short), style = text_16) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Edit, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onIntent(TodoTaskDetailsIntent.StartEditComment(comment))
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.todo_delete), style = text_16, color = red_7) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Delete, contentDescription = null, tint = red_7) },
                    onClick = {
                        menuExpanded = false
                        onIntent(TodoTaskDetailsIntent.AskDeleteComment(comment))
                    }
                )
            }
        }
    }
}

/** Вложенная шторка «Связать»: тип связи и поиск задач проекта. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoLinkSheet(
    draft: TodoLinkDraft,
    candidates: List<DomainTodoTask>,
    lists: Map<Long, DomainTodoList>,
    statuses: Map<Long, DomainTodoStatus> = emptyMap(),
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    TodoBottomSheet(
        title = stringResource(R.string.todo_card_link_sheet_title),
        modifier = Modifier.imePadding(),
        contentHorizontalPadding = 0.dp,
        onDismiss = { onIntent(TodoTaskDetailsIntent.CloseLinkSheet) }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = stringResource(R.string.todo_card_link_type), style = text_14, color = gray_7)
            Column(modifier = Modifier.selectableGroup()) {
                val options = listOf(
                    TodoLinkType.RELATES to true,
                    TodoLinkType.BLOCKS to true,
                    TodoLinkType.BLOCKS to false
                )
                options.forEach { (type, isOutgoing) ->
                    val selected = draft.type == type && (type == TodoLinkType.RELATES || draft.isOutgoing == isOutgoing)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .selectable(selected = selected, role = Role.RadioButton) {
                                onIntent(TodoTaskDetailsIntent.LinkTypeChange(type, isOutgoing))
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = accent)
                        )
                        Text(
                            text = stringResource(type.titleRes(isOutgoing)),
                            style = text_16,
                            color = black_2,
                            modifier = Modifier.weight(1f).padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
                        )
                        TodoLinkTypeHelp(type, isOutgoing)
                    }
                }
            }
            Text(
                text = stringResource(R.string.todo_card_link_direction_hint),
                style = text_14,
                color = gray_7
            )
            TodoTextField(
                value = draft.query,
                onValueChange = { onIntent(TodoTaskDetailsIntent.LinkQueryChange(it)) },
                label = stringResource(R.string.todo_search),
                leadingIcon = Icons.Rounded.Search,
                placeholder = stringResource(R.string.todo_card_link_search_hint)
            )
            TodoSegmentedButtons(
                options = listOf(stringResource(R.string.todo_link_filter_unfinished), stringResource(R.string.todo_link_filter_all)),
                selectedIndex = if (draft.showCompleted) 1 else 0,
                onSelect = { onIntent(TodoTaskDetailsIntent.LinkShowCompletedChange(it == 1)) }
            )
        }
        if (candidates.isEmpty())
            Text(
                text = stringResource(R.string.todo_card_link_not_found),
                style = text_14,
                color = gray_7,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        else
            Column {
                candidates.forEachIndexed { index, task ->
                    val list = lists[task.listId]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIntent(TodoTaskDetailsIntent.AddLink(task)) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        list?.let {
                            TodoListBadge(icon = it.icon, color = todoColor(it.colorIndex))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = text_16,
                                color = black_2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            list?.let {
                                Text(
                                    text = it.title,
                                    style = DETAILS_META,
                                    color = gray_7,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            statuses[task.statusId]?.let { status ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TodoColorDot(color = status.color(), size = 8.dp)
                                    Text(text = status.displayTitle(), style = DETAILS_META, color = status.color())
                                }
                            }
                            val due = dueToDisplay(task.dueDate, task.dueTime)
                            if (due.isNotEmpty()) Text(text = due, style = text_14, color = accent)
                        }
                    }
                    if (index < candidates.lastIndex) HorizontalDivider(thickness = 0.5.dp, color = grey_2)
                }
            }
    }
}

/** Подсказка открывается обычным тапом, не меняя выбранный тип связи. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoLinkTypeHelp(type: TodoLinkType, isOutgoing: Boolean) {
    val state = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()
    val explanation = when {
        type == TodoLinkType.RELATES -> R.string.todo_link_relates_help
        isOutgoing -> R.string.todo_link_blocks_help
        else -> R.string.todo_link_blocked_by_help
    }
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { PlainTooltip { Text(stringResource(explanation)) } },
        state = state
    ) {
        IconButton(
            onClick = { scope.launch { state.show() } },
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = stringResource(R.string.todo_link_type_help, stringResource(type.titleRes(isOutgoing))),
                tint = gray_7,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** Вложенная шторка правки комментария. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoCommentEditSheet(
    draft: TodoCommentDraft,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    TodoBottomSheet(
        title = stringResource(R.string.todo_card_comment_edit_title),
        modifier = Modifier.imePadding(),
        onDismiss = { onIntent(TodoTaskDetailsIntent.CancelEditComment) }
    ) {
        TodoTextField(
            value = draft.text,
            onValueChange = { onIntent(TodoTaskDetailsIntent.EditCommentTextChange(it)) },
            label = stringResource(R.string.todo_card_comment_hint),
            singleLine = false,
            minLines = 3,
            isError = draft.textError != null,
            errorText = draft.textError?.let { stringResource(it) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
        Button(
            onClick = { onIntent(TodoTaskDetailsIntent.SaveEditComment) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = white)
        ) {
            Text(text = stringResource(R.string.todo_save), style = textBold_16)
        }
    }
}

// Примитивы группы

@Composable
private fun TodoDetailsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, grey_2, RoundedCornerShape(12.dp))
            .background(white),
        content = content
    )
}

/** Заголовок раздела в типографике приложения. */
@Composable
private fun TodoSectionLabel(text: String) {
    Text(
        text = text,
        style = textBold_16,
        color = black_2,
        modifier = Modifier.padding(top = 12.dp).semantics { heading() }
    )
}

@Composable
private fun TodoGroupDivider() {
    HorizontalDivider(thickness = 1.dp, color = grey_2, modifier = Modifier.padding(horizontal = 12.dp))
}

/** Иконка на спокойной подложке в палитре приложения. */
@Composable
private fun TodoRowIcon(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
    }
}

/** Строка с переключателем; тап по включённой строке открывает выбор [value]. */
@Composable
private fun TodoSwitchRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String?,
    checked: Boolean,
    accent: Color,
    onCheckedChange: (Boolean) -> Unit,
    onValueClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { if (checked) onValueClick() else onCheckedChange(true) }
            .heightIn(min = 56.dp)
            .padding(start = 14.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TodoRowIcon(icon = icon, color = if (enabled) iconColor else gray_8)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(text = title, style = DETAILS_TEXT, color = if (enabled) black_2 else gray_7)
            if (checked && value != null)
                Text(text = value, style = DETAILS_META.copy(fontSize = 14.sp), color = accent)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = white,
                checkedTrackColor = accent,
                checkedBorderColor = accent,
                uncheckedThumbColor = gray_7,
                uncheckedTrackColor = gray_6,
                uncheckedBorderColor = gray_6,
                disabledUncheckedThumbColor = white,
                disabledUncheckedTrackColor = gray_6.copy(alpha = 0.5f),
                disabledUncheckedBorderColor = gray_6.copy(alpha = 0.5f)
            )
        )
    }
}

/** Подпись и значение в две строки с выпадающим меню вариантов. */
@Composable
private fun TodoMenuRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    valueColor: Color,
    matchRowWidth: Boolean = false,
    menu: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var rowWidthPx by remember { mutableIntStateOf(0) }
    val anchorModifier = if (matchRowWidth) Modifier.onSizeChanged { rowWidthPx = it.width } else Modifier
    Box(anchorModifier) {
        TodoPropertyRow(icon, iconColor, title, onClick = { expanded = true }) {
            Text(text = value, style = text_16, color = valueColor)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = if (rowWidthPx > 0) Modifier.width(with(LocalDensity.current) { rowWidthPx.toDp() }) else Modifier,
            containerColor = white
        ) {
            menu { expanded = false }
        }
    }
}

/** Общая раскладка строки свойства: подпись, значение и индикатор выбора. */
@Composable
private fun TodoPropertyRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    onClick: (() -> Unit)?,
    showIndicator: Boolean = true,
    valueContent: @Composable () -> Unit
) {
    Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .heightIn(min = 52.dp)
                .padding(start = 14.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TodoRowIcon(icon = icon, color = iconColor)
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, style = text_14, color = gray_7)
                valueContent()
            }
            if (showIndicator) Icon(
                imageVector = Icons.Rounded.UnfoldMore,
                contentDescription = null,
                tint = gray_7,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(18.dp)
            )
        }
}

@Composable
private fun TodoMenuOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leading: (@Composable () -> Unit)? = null
) {
    DropdownMenuItem(
        text = { Text(text = text, style = text_16, color = black_2) },
        leadingIcon = leading,
        trailingIcon = if (selected) {
            { Icon(imageVector = Icons.Rounded.Check, contentDescription = stringResource(R.string.todo_selected)) }
        } else null,
        onClick = onClick
    )
}

/** Последняя строка группы — действие основным акцентом («Связать»). */
@Composable
private fun TodoActionRow(text: String, accent: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        Text(text = text, style = DETAILS_TEXT, color = accent)
    }
}

/**
 * Поле с постоянной подписью. [onFocusLost] — потеря фокуса после того, как поле
 * его получало (сохранение черновика).
 */
@Composable
private fun TodoDetailsField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onFocusLost: () -> Unit = {},
    singleLine: Boolean = true,
    minLines: Int = 2,
    style: TextStyle = DETAILS_TEXT,
    imeAction: ImeAction = ImeAction.Default,
    onImeAction: () -> Unit = {},
    trailingIcon: (@Composable () -> Unit)? = null,
    usePlaceholder: Boolean = false
) {
    var wasFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        textStyle = style.copy(color = black_2),
        label = if (usePlaceholder) null else {
            { Text(text = placeholder, style = text_14) }
        },
        placeholder = if (usePlaceholder) {
            { Text(text = placeholder, style = text_14) }
        } else null,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accent, unfocusedBorderColor = gray_8,
            focusedLabelColor = accent, unfocusedLabelColor = gray_7,
            cursorColor = accent, focusedContainerColor = white, unfocusedContainerColor = white
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.isFocused) wasFocused = true
                else if (wasFocused) {
                    wasFocused = false
                    onFocusLost()
                }
            }
    )
}
