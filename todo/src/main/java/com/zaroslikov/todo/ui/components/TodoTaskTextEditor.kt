package com.zaroslikov.todo.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.ui.list.CARD_META_STYLE
import com.zaroslikov.todo.ui.theme.gray_7

/** Один контейнер владеет изменением высоты; перенос удержанием использует точные размеры. */
fun Modifier.animateTodoTextSize(enabled: Boolean = true): Modifier =
    if (enabled) animateContentSize(
        animationSpec = tween(180),
        alignment = Alignment.TopStart
    ) else this

/** Название и заметка — одна область фокуса: переход между полями не сохраняет и не закрывает ввод. */
@Composable
fun TodoTaskTextEditor(
    title: String,
    note: String,
    accent: Color,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
    titleTrailing: (@Composable () -> Unit)? = null,
    noteTextStyle: TextStyle = CARD_META_STYLE,
    noteSpacing: Dp = 4.dp,
    priority: TodoPriority = TodoPriority.NONE,
    readOnly: Boolean = false
) {
    var noteExpanded by remember { mutableStateOf(note.isNotEmpty()) }
    var requestNoteFocus by remember { mutableStateOf(false) }
    val noteFocus = remember { FocusRequester() }
    var wasFocused by remember { mutableStateOf(false) }
    val finish by rememberUpdatedState(onFinish)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                if (it.hasFocus) wasFocused = true
                else if (wasFocused) {
                    wasFocused = false
                    finish()
                }
            }
            .focusGroup(),
        verticalArrangement = Arrangement.spacedBy(noteSpacing)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            TodoPriorityMarks(priority)
            Box(Modifier.weight(1f)) {
                TodoInlineTitleField(
                    text = title,
                    accent = accent,
                    onTextChange = onTitleChange,
                    onSubmit = {
                        noteExpanded = false
                        onSubmit()
                    },
                    onFinish = {},
                    singleLine = false,
                    readOnly = readOnly
                )
            }
            if (titleTrailing != null)
                Box(modifier = Modifier.padding(end = 7.dp)) { titleTrailing() }
        }
        if (noteExpanded || note.isNotEmpty()) {
            LaunchedEffect(requestNoteFocus) {
                if (requestNoteFocus) {
                    noteFocus.requestFocus()
                    requestNoteFocus = false
                }
            }
            BasicTextField(
                value = note,
                onValueChange = onNoteChange,
                readOnly = readOnly,
                textStyle = noteTextStyle.copy(color = gray_7),
                cursorBrush = SolidColor(accent),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(noteFocus),
                decorationBox = { inner ->
                    androidx.compose.foundation.layout.Box {
                        if (note.isEmpty()) Text(
                            text = stringResource(R.string.todo_add_note),
                            style = noteTextStyle,
                            color = gray_7
                        )
                        inner()
                    }
                }
            )
        } else {
            Text(
                text = stringResource(R.string.todo_add_note),
                fontSize = 14.sp,
                lineHeight = 18.sp,
                color = gray_7,
                modifier = Modifier
                    .focusProperties { canFocus = false }
                    .clickable(enabled = !readOnly, role = Role.Button) {
                        noteExpanded = true
                        requestNoteFocus = true
                    }
            )
        }
    }
}
