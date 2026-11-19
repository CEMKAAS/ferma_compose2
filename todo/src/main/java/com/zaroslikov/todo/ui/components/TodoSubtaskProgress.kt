package com.zaroslikov.todo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.ui.theme.gray_7

@Composable
fun TodoSubtaskProgress(
    done: Int,
    total: Int,
    accent: Color,
    onToggleSubtasks: (() -> Unit)? = null,
    isCollapsed: Boolean = false
) {
    if (total <= 0) return
    val description = stringResource(R.string.todo_list_subtasks, done, total)
    val toggleLabel = stringResource(
        if (isCollapsed) R.string.todo_list_subtasks_expand else R.string.todo_list_subtasks_collapse
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onToggleSubtasks == null) Modifier else Modifier.clickable(onClick = onToggleSubtasks))
            .clearAndSetSemantics {
                contentDescription = description
                if (onToggleSubtasks != null) {
                    role = Role.Button
                    onClick(label = toggleLabel) {
                        onToggleSubtasks()
                        true
                    }
                }
            }
    ) {
        LinearProgressIndicator(
            progress = { (done.toFloat() / total).coerceIn(0f, 1f) },
            color = accent,
            trackColor = accent.copy(alpha = 0.15f),
            modifier = Modifier.weight(1f).height(4.dp)
        )
        Text(text = "$done/$total", style = TextStyle(fontSize = 12.sp), color = gray_7, maxLines = 1)
    }
}
