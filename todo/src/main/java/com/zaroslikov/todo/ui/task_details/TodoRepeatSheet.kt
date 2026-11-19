package com.zaroslikov.todo.ui.task_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.ui.components.TodoBottomSheet
import com.zaroslikov.todo.ui.components.TodoDatePickerDialog
import com.zaroslikov.todo.ui.components.titleRes
import com.zaroslikov.todo.ui.theme.*
import com.zaroslikov.todo.util.isoToDisplay

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TodoRepeatSheet(draft: TodoRepeatDraft, dueDate: String, onIntent: (TodoTaskDetailsIntent) -> Unit) {
    var showDate by remember { mutableStateOf(false) }
    TodoBottomSheet(
        title = stringResource(R.string.todo_repeat),
        onDismiss = { onIntent(TodoTaskDetailsIntent.CloseRepeatSheet) },
        headerActions = {
            IconButton(onClick = { onIntent(TodoTaskDetailsIntent.CloseRepeatSheet) }) {
                Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.todo_cancel), tint = gray_7)
            }
        },
        footer = {
            Button(
                onClick = { onIntent(TodoTaskDetailsIntent.SaveRepeat) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = green_shamrock)
            ) { Text(stringResource(R.string.todo_save)) }
        }
    ) {
        Column(Modifier.selectableGroup()) {
            TodoRepeatPeriod.entries.forEach { period ->
                Row(
                    Modifier.fillMaxWidth().selectable(
                        selected = draft.period == period, role = Role.RadioButton,
                        onClick = { onIntent(TodoTaskDetailsIntent.RepeatPeriodChange(period)) }
                    ).heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = draft.period == period, onClick = null,
                        colors = RadioButtonDefaults.colors(selectedColor = green_shamrock))
                    Text(stringResource(period.titleRes()), style = text_16, color = black_2,
                        modifier = Modifier.padding(start = 12.dp))
                }
            }
        }
        if (draft.period != TodoRepeatPeriod.NONE) {
            HorizontalDivider(color = grey_2)
            Text(stringResource(R.string.todo_repeat_end), style = textBold_16, color = black_2)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.todo_repeat_forever), style = text_16, color = black_2,
                    modifier = Modifier.weight(1f))
                Switch(checked = draft.endDate == null,
                    onCheckedChange = { onIntent(TodoTaskDetailsIntent.RepeatEndChange(if (it) null else dueDate)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = white, checkedTrackColor = green_shamrock))
            }
            if (draft.endDate != null) {
                OutlinedButton(onClick = { showDate = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(isoToDisplay(draft.endDate), color = green_shamrock)
                }
            }
            if (draft.hasError) Text(stringResource(R.string.todo_repeat_error_end), color = error_base, style = text_14)
        }
    }
    if (showDate) TodoDatePickerDialog(
        value = draft.endDate ?: dueDate,
        onConfirm = { onIntent(TodoTaskDetailsIntent.RepeatEndChange(it)) },
        onDismiss = { showDate = false }
    )
}
