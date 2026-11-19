package com.zaroslikov.todo.ui.list

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.ui.components.TodoBottomSheet
import com.zaroslikov.todo.ui.components.TodoInlineTitleField
import com.zaroslikov.todo.ui.components.TodoTextField
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.green_6
import com.zaroslikov.todo.ui.theme.grey_2
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.ui.theme.white

// Подсписки в виде «список»: заголовок с шевроном (название правится на месте, удержание —
// перенос), пунктирная строка новой задачи, шторка нового подсписка, меню действий

/** Название подсписка — крупно и жирно цветом списка, как в «Напоминаниях». */
private val SECTION_TITLE_STYLE = TextStyle(fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold)

/**
 * Заголовок подсписка: тап по названию — переименование на месте ([editingTitle] — текст поля
 * тем же стилем), удержание и перенос — перестановка подсписков ([drag]; null — заголовок
 * «призрака»), долгий тап без движения — «Переименовать / Удалить». Справа число задач
 * (у свёрнутого) и шеврон сворачивания, если [canCollapse].
 */
@Composable
internal fun TodoSectionHeader(
    group: TodoListGroup,
    accent: Color,
    onIntent: (TodoListIntent) -> Unit,
    modifier: Modifier = Modifier,
    editingTitle: String? = null,
    drag: TodoDragState<TodoDragItem>? = null,
    canCollapse: Boolean = true
) {
    val section = group.section ?: return
    var menuExpanded by remember { mutableStateOf(false) }
    val dragSource = if (drag == null) Modifier
    else rememberTodoDragSource(
        drag = drag,
        item = TodoDragItem.Section(section.id),
        enabled = editingTitle == null,
        onLongClick = { menuExpanded = true },
        longClickLabel = stringResource(R.string.todo_section_actions, section.title)
    )
    Column(modifier.fillMaxWidth().then(dragSource)) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) {
                if (editingTitle != null)
                    TodoInlineTitleField(
                        text = editingTitle,
                        accent = accent,
                        onTextChange = { onIntent(TodoListIntent.SectionTitleChanged(it)) },
                        onSubmit = { onIntent(TodoListIntent.SectionTitleFinish()) },
                        onFinish = { onIntent(TodoListIntent.SectionTitleFinish()) },
                        textStyle = SECTION_TITLE_STYLE,
                        textColor = accent
                    )
                else
                    Text(
                        text = section.title,
                        style = SECTION_TITLE_STYLE,
                        color = accent,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable(onClickLabel = stringResource(R.string.todo_section_rename)) {
                            onIntent(TodoListIntent.StartRenameSection(section.id))
                        }
                    )
                TodoSectionMenu(
                    expanded = menuExpanded,
                    section = section,
                    onDismiss = { menuExpanded = false },
                    onIntent = onIntent
                )
            }
            if (group.isCollapsed)
                Text(
                    text = group.taskCount.toString(),
                    style = text_16,
                    color = gray_7,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            if (canCollapse)
                IconButton(onClick = { onIntent(TodoListIntent.ToggleSectionCollapse(section.id)) }) {
                    Icon(
                        imageVector = if (group.isCollapsed) Icons.AutoMirrored.Rounded.KeyboardArrowRight
                        else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(
                            if (group.isCollapsed) R.string.todo_section_expand else R.string.todo_section_collapse
                        ),
                        tint = gray_7
                    )
                }
        }
        HorizontalDivider(thickness = 0.5.dp, color = grey_2, modifier = Modifier.padding(start = 16.dp))
    }
}

/** Меню подсписка: переименовать (на месте) или удалить (с подтверждением). */
@Composable
internal fun TodoSectionMenu(
    expanded: Boolean,
    section: DomainTodoSection,
    onDismiss: () -> Unit,
    onIntent: (TodoListIntent) -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss, containerColor = white) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.todo_section_rename), style = text_16, color = black_2) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.Edit, contentDescription = null, tint = black_2) },
            onClick = {
                onDismiss()
                onIntent(TodoListIntent.StartRenameSection(section.id))
            }
        )
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.todo_section_delete), style = text_16, color = error_base) },
            leadingIcon = { Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = null, tint = error_base) },
            onClick = {
                onDismiss()
                onIntent(TodoListIntent.RequestDeleteSection(section.id))
            }
        )
    }
}

/** Пунктирный круг на месте круга отметки — у ещё не созданной задачи; зона та же, 40dp. */
@Composable
internal fun TodoDashedCircle() {
    Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(22.dp)) {
            val stroke = 1.5.dp.toPx()
            drawCircle(
                color = gray_8,
                radius = size.minDimension / 2 - stroke / 2,
                style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())))
            )
        }
    }
}

/** Пустая строка в конце подсписка: пунктирный круг без текста, тап — ввод новой задачи здесь. */
@Composable
internal fun TodoNewTaskRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSubtask: Boolean = false
) {
    val description = stringResource(if (isSubtask) R.string.todo_card_subtask_quick else R.string.todo_task_new_title)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = description
                role = Role.Button
            }
            .padding(start = 7.dp + if (isSubtask) SUBTASK_INDENT else 0.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TodoDashedCircle()
        // Высота как у строки ввода, в которую строка превращается: строка текста + отступы
        Spacer(Modifier.padding(vertical = 10.dp).height(22.dp))
    }
}

/** Шторка «Новый подсписок». */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoSectionSheet(
    form: TodoSectionForm,
    onTitleChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    TodoBottomSheet(
        title = stringResource(R.string.todo_section_new),
        onDismiss = onDismiss
    ) {
        TodoTextField(
            value = form.title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.todo_field_title),
            isError = form.titleError,
            errorText = stringResource(R.string.todo_error_title_empty)
        )
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = green_6, contentColor = white)
        ) {
            Text(
                text = stringResource(R.string.todo_create),
                style = textBold_16
            )
        }
    }
}
