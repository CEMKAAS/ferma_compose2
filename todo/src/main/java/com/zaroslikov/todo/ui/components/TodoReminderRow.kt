package com.zaroslikov.todo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.grey_2
import com.zaroslikov.todo.util.TodoDayLabels

// Строка задачи в стиле «Напоминаний» iOS — общая для экрана списка, умных списков и поиска

/** Начало текста строки от её левого края (под круг отметки) — с него же начинается разделитель. */
val TODO_ROW_TEXT_START = 50.dp

/** Название задачи 17sp, как в iOS. */
val TODO_ROW_TITLE_STYLE = TextStyle(fontSize = 18.sp, lineHeight = 22.sp)

val TODO_ROW_META_STYLE = TextStyle(fontSize = 14.sp, lineHeight = 18.sp)

val TODO_ROW_TEXT_SPACING = 2.dp

val TODO_ROW_TITLE_ACTIONS_WIDTH = 62.dp

/**
 * Строка задачи: круг отметки, приоритет и название, затем примечание, список,
 * [context] («в: родитель»), срок, повтор, исполнители, индикаторы и теги.
 * [editingText] не null — название редактируется на месте, справа «ⓘ».
 * [trailing] — что показать справа вне редактирования.
 */
@Composable
fun TodoReminderRow(
    item: DomainTodoTaskItem,
    isDone: Boolean,
    accent: Color,
    labels: TodoDayLabels,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indent: Dp = 0.dp,
    context: String? = null,
    showDivider: Boolean = true,
    editingText: String? = null,
    animateSize: Boolean = true,
    editingNote: String = "",
    onNoteChange: (String) -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onFinish: () -> Unit = {},
    onInfo: () -> Unit = {},
    onAddSubtask: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onToggleSubtasks: (() -> Unit)? = null,
    subtasksCollapsed: Boolean = false,
    listTitle: String? = null,
    listIcon: TodoListIcon = TodoListIcon.DEFAULT
) {
    val task = item.task
    Column(
        modifier
            .animateTodoTextSize(animateSize)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = editingText == null, onClick = onClick)
                .padding(start = indent + 7.dp, end = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            TodoDoneCircle(done = isDone, accent = accent, onToggle = onToggle)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 3.dp, top = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(TODO_ROW_TEXT_SPACING)
            ) {
                if (editingText != null)
                    TodoTaskTextEditor(
                        title = editingText,
                        note = editingNote,
                        accent = accent,
                        onTitleChange = onTextChange,
                        onNoteChange = onNoteChange,
                        onSubmit = onSubmit,
                        onFinish = onFinish,
                        noteTextStyle = TODO_ROW_META_STYLE,
                        noteSpacing = TODO_ROW_TEXT_SPACING,
                        priority = task.priority,
                        titleTrailing = {
                            Row(
                                modifier = Modifier.width(TODO_ROW_TITLE_ACTIONS_WIDTH - 7.dp).padding(end = 7.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
                            ) {
                                if (onAddSubtask != null)
                                    TodoAddButton(accent) { onAddSubtask() }
                                TodoInfoButton(accent = accent, onClick = onInfo)
                            }
                        }
                    )
                else {
                    Row(Modifier.fillMaxWidth()) {
                        TodoPriorityMarks(task.priority)
                        Text(
                            text = task.title,
                            style = TODO_ROW_TITLE_STYLE,
                            color = if (isDone) gray_7 else black_2,
                            modifier = Modifier.weight(1f)
                        )
                        // Резерв под те же кнопки и отступы, что появятся при редактировании.
                        Spacer(Modifier.size(width = TODO_ROW_TITLE_ACTIONS_WIDTH, height = 22.dp))
                    }
                    if (task.note.isNotBlank())
                        Text(
                            text = task.note,
                            style = TODO_ROW_META_STYLE,
                            color = gray_7,
                            modifier = Modifier.fillMaxWidth()
                        )
                }
                TodoTaskMetadata(item, isDone, labels, context = context, listTitle = listTitle, listIcon = listIcon)
            }
            if (editingText == null && trailing != null) trailing()
        }
        if (item.subtaskCount > 0)
            Box(Modifier.fillMaxWidth().padding(start = indent + TODO_ROW_TEXT_START, end = 4.dp, bottom = 10.dp)) {
                TodoSubtaskProgress(
                    done = item.subtaskDoneCount,
                    total = item.subtaskCount,
                    accent = accent,
                    onToggleSubtasks = onToggleSubtasks,
                    isCollapsed = subtasksCollapsed
                )
            }
        if (showDivider)
            HorizontalDivider(
                thickness = 0.5.dp,
                color = grey_2,
                modifier = Modifier.padding(start = indent + TODO_ROW_TEXT_START)
            )
    }
}

/** Метаданные задачи после описания в списке и столбцах. */
@Composable
internal fun TodoTaskMetadata(
    item: DomainTodoTaskItem,
    isDone: Boolean,
    labels: TodoDayLabels,
    context: String? = null,
    style: TextStyle = TODO_ROW_META_STYLE,
    listTitle: String? = null,
    listIcon: TodoListIcon = TodoListIcon.DEFAULT
) {
    if (!listTitle.isNullOrBlank())
        TodoTaskListLabel(title = listTitle, icon = listIcon, style = style)
    if (!context.isNullOrBlank())
        Text(context, style = style, color = gray_7, maxLines = 2, overflow = TextOverflow.Ellipsis)

    val task = item.task
    TodoTaskScheduleMetadata(task, isDone, labels, style)
    TodoAssigneesMetadata(task = task, style = style)

    item.status?.takeIf { !it.isDone && it.defaultCode != TodoDefaultStatus.PLANNED }?.let { status ->
        Text(status.displayTitle(), style = style, color = status.color())
    }

    if (item.commentCount > 0 || item.linkCount > 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item.commentCount > 0)
                TodoMetaCounter(
                    icon = Icons.Rounded.ChatBubbleOutline,
                    text = item.commentCount.toString(),
                    description = stringResource(R.string.todo_list_comments, item.commentCount),
                    style = style
                )
            TodoTaskLinkCounters(item)
        }
    }
    TodoTaskTags(item.tags)
}

/** Раздельные счётчики обычных связей и обоих направлений блокировки. */
@Composable
fun TodoTaskLinkCounters(item: DomainTodoTaskItem) {
    if (item.relatedLinkCount > 0)
        TodoMetaCounter(
            icon = TodoLinkType.RELATES.icon(),
            text = item.relatedLinkCount.toString(),
            description = stringResource(R.string.todo_list_links, item.relatedLinkCount)
        )
    if (item.blockedByCount > 0)
        TodoMetaCounter(
            icon = TodoLinkType.BLOCKS.icon(isOutgoing = false),
            text = item.blockedByCount.toString(),
            description = stringResource(R.string.todo_links_blocked_by_count, item.blockedByCount)
        )
    if (item.blockingCount > 0)
        TodoMetaCounter(
            icon = TodoLinkType.BLOCKS.icon(isOutgoing = true),
            text = item.blockingCount.toString(),
            description = stringResource(R.string.todo_links_blocking_count, item.blockingCount)
        )
}

/** Маленькая иконка с числом; [description] — для TalkBack вместо цифры. */
@Composable
private fun TodoMetaCounter(
    icon: ImageVector,
    text: String,
    description: String,
    style: TextStyle = TODO_ROW_META_STYLE
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.clearAndSetSemantics { contentDescription = description }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = gray_7,
            modifier = Modifier.size(14.dp)
        )
        Text(text = text, style = style, color = gray_7, maxLines = 1)
    }
}

/**
 * Круг отметки ~22dp: у незавершённой — серый контур, у завершённой — кольцо цвета списка
 * с закрашенной серединой и белым зазором, как в iOS. Зона нажатия — 40dp.
 */
@Composable
fun TodoDoneCircle(done: Boolean, accent: Color, onToggle: () -> Unit) {
    val description =
        stringResource(if (done) R.string.todo_list_undone_checkbox else R.string.todo_list_done_checkbox)
    val fill by animateFloatAsState(if (done) 1f else 0f, label = "doneFill")
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .toggleable(value = done, role = Role.Checkbox, onValueChange = { onToggle() })
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(width = 1.5.dp, color = if (done) accent else gray_8, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (fill > 0f)
                Box(
                    modifier = Modifier
                        .size(14.dp * fill)
                        .clip(CircleShape)
                        .background(accent)
                )
        }
    }
}

@Composable
fun TodoInfoButton(accent: Color, onClick: () -> Unit) {
    val viewConfiguration = LocalViewConfiguration.current
    val compactViewConfiguration = remember(viewConfiguration) {
        object : ViewConfiguration by viewConfiguration {
            override val minimumTouchTargetSize = DpSize(22.dp, 22.dp)
        }
    }
    // Компактная область нажатия применяется только к значку «ⓘ».
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 22.dp,
        LocalViewConfiguration provides compactViewConfiguration
    ) {
        // Тап не снимает фокус с редактора до открытия подробностей.
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(22.dp)
                .focusProperties { canFocus = false }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.todo_list_task_details),
                tint = accent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun TodoAddButton(accent: Color, onClick: () -> Unit) {
    val viewConfiguration = LocalViewConfiguration.current
    val compactViewConfiguration = remember(viewConfiguration) {
        object : ViewConfiguration by viewConfiguration {
            override val minimumTouchTargetSize = DpSize(22.dp, 22.dp)
        }
    }
    // Компактная область нажатия применяется только к значку «ⓘ».
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentSize provides 22.dp,
        LocalViewConfiguration provides compactViewConfiguration
    ) {
        // Тап не снимает фокус с редактора до открытия подробностей.
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(22.dp)
                .focusProperties { canFocus = false }
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = stringResource(R.string.todo_list_task_details),
                tint = accent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Поле названия без рамки: при появлении берёт фокус и открывает клавиатуру, Enter — [onSubmit]
 * (клавиатура остаётся для следующей задачи), потеря фокуса — [onFinish]. [textStyle] и
 * [textColor] — как у текста, который поле заменяет (название задачи, подсписка, столбца).
 */
@Composable
fun TodoInlineTitleField(
    text: String,
    accent: Color,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onFinish: () -> Unit,
    textStyle: TextStyle = TODO_ROW_TITLE_STYLE,
    textColor: Color = black_2,
    singleLine: Boolean = true,
    readOnly: Boolean = false
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var wasFocused by remember { mutableStateOf(false) }
    // Курсор в конце: при переименовании дописывают, а не набирают заново
    var value by remember { mutableStateOf(TextFieldValue(text, TextRange(text.length))) }
    if (value.text != text) value = TextFieldValue(text, TextRange(text.length))
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboard?.show()
    }
    BasicTextField(
        value = value,
        onValueChange = {
            value = it
            if (it.text != text) onTextChange(it.text)
        },
        singleLine = singleLine,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = textColor),
        cursorBrush = SolidColor(accent),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) wasFocused = true
                else if (wasFocused) {
                    wasFocused = false
                    onFinish()
                }
            }
    )
}
