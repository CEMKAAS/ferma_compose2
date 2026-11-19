package com.zaroslikov.todo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.ui.theme.text_14
import com.zaroslikov.todo.ui.theme.todoColor
import com.zaroslikov.todo.ui.theme.white

@Composable
internal fun TodoTaskTags(tags: List<DomainTodoTag>) {
    if (tags.isEmpty()) return
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tags.forEach { tag ->
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
