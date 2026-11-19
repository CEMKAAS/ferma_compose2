package com.zaroslikov.todo.ui.task_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.zaroslikov.todo.R
import com.zaroslikov.todo.ui.theme.black_2
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.gray_8
import com.zaroslikov.todo.ui.theme.grey_2
import com.zaroslikov.todo.ui.theme.textBold_16
import com.zaroslikov.todo.ui.theme.text_16
import com.zaroslikov.todo.ui.theme.white
import kotlinx.coroutines.flow.collect

private class TodoAnimatedAssignee(val name: String, initiallyVisible: Boolean) {
    val visibility = MutableTransitionState(initiallyVisible).apply { targetState = true }
}

/** Общий раздел для задачи и подзадачи: имена сохраняются независимо от родителя. */
@Composable
internal fun TodoAssigneesGroup(
    state: TodoTaskDetailsState,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    val task = state.data?.task ?: return
    val focusRegistry = LocalTodoAssigneeFocus.current
    val fieldFocusManager = LocalFocusManager.current
    val fieldFocus = remember(task.id, fieldFocusManager) { TodoAssigneeFieldFocus { fieldFocusManager.clearFocus() } }
    val currentOnIntent by rememberUpdatedState(onIntent)
    DisposableEffect(focusRegistry, fieldFocus) {
        focusRegistry?.field = fieldFocus
        onDispose { if (focusRegistry?.field === fieldFocus) focusRegistry.field = null }
    }
    var fieldWidthPx by remember { mutableIntStateOf(0) }
    val fieldWidth = with(LocalDensity.current) { fieldWidthPx.toDp() }
    val fieldShape = RoundedCornerShape(12.dp)
    val candidates = state.assigneeCandidates
    val canAdd = state.assigneeName.isNotBlank() &&
        task.assignees.none { it.equals(state.assigneeName.trim(), ignoreCase = true) }
    Column {
        Text(
            stringResource(R.string.todo_assignees), style = textBold_16, color = black_2,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp).semantics { heading() }
        )
        TodoAssignedNames(taskId = task.id, names = task.assignees, accent = accent, onIntent = onIntent)
        Box(Modifier.fillMaxWidth().onSizeChanged { fieldWidthPx = it.width }) {
            OutlinedTextField(
                value = state.assigneeName,
                onValueChange = { onIntent(TodoTaskDetailsIntent.AssigneeNameChange(it)) },
                label = { Text(stringResource(R.string.todo_assignee_name)) },
                trailingIcon = {
                    IconButton(onClick = { onIntent(TodoTaskDetailsIntent.AddAssignee) }, enabled = canAdd) {
                        Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.todo_assignee_add),
                            tint = if (canAdd) accent else gray_8)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (canAdd) onIntent(TodoTaskDetailsIntent.AddAssignee) }),
                textStyle = text_16,
                shape = fieldShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent, unfocusedBorderColor = gray_8,
                    focusedLabelColor = accent, cursorColor = accent,
                    focusedContainerColor = white, unfocusedContainerColor = white
                ),
                modifier = Modifier.fillMaxWidth()
                    .onGloballyPositioned { fieldFocus.boundsInWindow = it.boundsInWindow() }
                    .onFocusChanged {
                        fieldFocus.isFocused = it.isFocused
                        if (it.isFocused) onIntent(TodoTaskDetailsIntent.OpenAssigneeChoices)
                        else onIntent(TodoTaskDetailsIntent.CloseAssigneeChoices)
                    }
                    .pointerInput(task.id) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (event.changes.any { it.changedToDownIgnoreConsumed() })
                                    currentOnIntent(TodoTaskDetailsIntent.OpenAssigneeChoices)
                            }
                        }
                    }
            )
            DropdownMenu(
                expanded = state.showAssigneeChoices && fieldWidthPx > 0,
                onDismissRequest = { onIntent(TodoTaskDetailsIntent.CloseAssigneeChoices) },
                // Поле сохраняет фокус и получает ввод с клавиатуры, пока меню открыто.
                properties = PopupProperties(focusable = false),
                containerColor = white,
                shape = fieldShape,
                modifier = Modifier.width(fieldWidth).heightIn(max = 240.dp)
            ) {
                if (candidates.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.todo_assignee_no_matches), color = gray_7) },
                        onClick = {}, enabled = false
                    )
                }
                candidates.forEachIndexed { index, name ->
                    if (index > 0) HorizontalDivider(color = grey_2)
                    val description = stringResource(R.string.todo_assignee_select_name, name)
                    DropdownMenuItem(
                        text = { Text(name, style = text_16, color = black_2) },
                        onClick = { onIntent(TodoTaskDetailsIntent.SelectAssignee(name)) },
                        modifier = Modifier.semantics { contentDescription = description }
                    )
                }
            }
        }
    }
}

/** Исчезающие строки остаются в карточке до конца анимации, но больше не принимают действия. */
@Composable
private fun TodoAssignedNames(
    taskId: Long,
    names: List<String>,
    accent: Color,
    onIntent: (TodoTaskDetailsIntent) -> Unit
) {
    var expanded by remember(taskId) { mutableStateOf(false) }
    val visibleNames = if (expanded) names else names.take(3)
    val visibleSet = visibleNames.toSet()
    val rows = remember(taskId) {
        mutableStateListOf<TodoAnimatedAssignee>().apply {
            addAll(visibleNames.map { TodoAnimatedAssignee(it, initiallyVisible = true) })
        }
    }
    LaunchedEffect(taskId, names, visibleNames) {
        rows.forEach { it.visibility.targetState = it.name in visibleSet }
        val displayedNames = rows.mapTo(mutableSetOf()) { it.name }
        visibleNames.filter { it !in displayedNames }.forEach {
            rows.add(TodoAnimatedAssignee(it, initiallyVisible = false))
        }
        val previousIndices = rows.mapIndexed { index, row -> row.name to index }.toMap()
        val currentIndices = names.mapIndexed { index, name -> name to index }.toMap()
        rows.sortBy { currentIndices[it.name] ?: previousIndices.getValue(it.name) }
    }
    LaunchedEffect(taskId) {
        snapshotFlow {
            rows.filter { it.visibility.isIdle && !it.visibility.currentState && !it.visibility.targetState }
        }.collect { finished -> rows.removeAll(finished.toSet()) }
    }
    if (rows.isNotEmpty()) {
        Column(Modifier.fillMaxWidth().border(1.dp, grey_2, RoundedCornerShape(12.dp))) {
            rows.forEachIndexed { index, row ->
                key(taskId, row.name) {
                    AnimatedVisibility(
                        visibleState = row.visibility,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            if (index > 0) HorizontalDivider(color = grey_2, modifier = Modifier.padding(horizontal = 12.dp))
                            Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(row.name, style = text_16, color = black_2, modifier = Modifier.weight(1f))
                                IconButton(
                                    enabled = row.name in visibleSet,
                                    onClick = { onIntent(TodoTaskDetailsIntent.RemoveAssignee(row.name)) }
                                ) {
                                    Icon(Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.todo_assignee_remove, row.name), tint = gray_7)
                                }
                            }
                        }
                    }
                }
            }
            if (names.size > 3) {
                HorizontalDivider(color = grey_2, modifier = Modifier.padding(horizontal = 12.dp))
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = accent)
                ) {
                    Text(stringResource(if (expanded) R.string.todo_assignees_collapse else R.string.todo_assignees_show_all), style = text_16)
                }
            }
        }
    }
    // Отступ сжимается вместе с последней строкой, а не исчезает после очистки её снимка.
    AnimatedVisibility(
        visible = names.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Spacer(Modifier.height(8.dp))
    }
}
