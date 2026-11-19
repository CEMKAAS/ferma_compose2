package com.zaroslikov.todo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.ui.theme.gray_7

/** Исполнители задачи или подзадачи в порядке назначения, через запятую. */
@Composable
internal fun TodoAssigneesMetadata(
    task: DomainTodoTask,
    modifier: Modifier = Modifier,
    style: TextStyle = TODO_ROW_META_STYLE
) {
    if (task.assignees.isEmpty()) return
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Person, contentDescription = stringResource(R.string.todo_assignees),
            tint = gray_7, modifier = Modifier.size(14.dp))
        Text(task.assignees.joinToString(", "), style = style, color = gray_7)
    }
}
