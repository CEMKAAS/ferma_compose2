package com.zaroslikov.todo.ui.task_details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.ui.components.TodoReminderRow
import com.zaroslikov.todo.ui.list.TodoCardMeta
import com.zaroslikov.todo.ui.list.TodoListRow
import com.zaroslikov.todo.ui.theme.green_shamrock
import com.zaroslikov.todo.util.TodoDayLabels
import org.junit.Rule
import org.junit.Test

class TodoAssigneesMetadataUiTest {
    @get:Rule val compose = createComposeRule()
    private val labels = TodoDayLabels("Сегодня", "Завтра", "Вчера")
    private val task = DomainTodoTask(id = 1, listId = 1, title = "Корм", statusId = 1,
        assignees = listOf("Иван", "Мария"))

    @Test fun sharedRowsShowIndependentTaskAndSubtaskAssignments() {
        val subtask = task.copy(id = 2, parentId = 1, title = "Склад", assignees = listOf("Анна", "Пётр"))
        compose.setContent { MaterialTheme {
            Column {
                TodoReminderRow(DomainTodoTaskItem(task, null), false, green_shamrock, labels, {}, {})
                TodoReminderRow(DomainTodoTaskItem(subtask, null), false, green_shamrock, labels, {}, {})
            }
        } }
        compose.onNodeWithText("Иван, Мария").assertExists()
        compose.onNodeWithText("Анна, Пётр").assertExists()
    }

    @Test fun sharedRowHidesNamesWhenNoAssigneesRemain() {
        var current by mutableStateOf(task)
        compose.setContent { MaterialTheme {
            TodoReminderRow(DomainTodoTaskItem(current, null), false, green_shamrock, labels, {}, {})
        } }
        compose.onNodeWithText("Иван, Мария").assertExists()
        compose.runOnIdle { current = task.copy(assignees = emptyList()) }
        compose.onNodeWithText("Иван, Мария").assertDoesNotExist()
        compose.onNodeWithText("Корм").assertExists()
    }

    @Test fun columnCardMetadataShowsCommaSeparatedNames() {
        compose.setContent { MaterialTheme {
            TodoCardMeta(TodoListRow(DomainTodoTaskItem(task, null), false, false), labels)
        } }
        compose.onNodeWithText("Иван, Мария").assertExists()
    }

    @Test fun detailsSubtaskRowShowsItsOwnAssignees() {
        val parent = task.copy(assignees = emptyList())
        val subtask = task.copy(id = 2, parentId = 1, title = "Склад", assignees = listOf("Анна", "Пётр"))
        val data = TodoTaskDetailsData(
            item = DomainTodoTaskItem(parent, null), parent = null,
            listTitle = "Хозяйство", listColorIndex = 0, idPT = 7, statuses = emptyList(),
            tags = emptyList(), subtasks = listOf(DomainTodoTaskItem(subtask, null)),
            links = emptyList(), comments = emptyList()
        )
        val state = TodoTaskDetailsState(taskId = parent.id, data = data, title = parent.title)
        compose.setContent { MaterialTheme {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                TodoTaskDetailsContent(state, data, green_shamrock) {}
            }
        } }
        compose.onNodeWithText("Анна, Пётр").performScrollTo().assertExists()
        compose.onNodeWithText("Иван, Мария").assertDoesNotExist()
    }
}
