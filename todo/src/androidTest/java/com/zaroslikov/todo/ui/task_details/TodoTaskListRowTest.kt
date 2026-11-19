package com.zaroslikov.todo.ui.task_details

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.zaroslikov.todo.domain.model.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TodoTaskListRowTest {
    @get:Rule val compose = createComposeRule()
    private val task = DomainTodoTask(id = 10, listId = 1, title = "Корм", statusId = 1)
    private val data = TodoTaskDetailsData(
        item = DomainTodoTaskItem(task, null), parent = null, listTitle = "Огород", listColorIndex = 1,
        idPT = 7, statuses = emptyList(), tags = emptyList(), subtasks = emptyList(), links = emptyList(), comments = emptyList(),
        projectLists = listOf(DomainTodoList(id = 1, idPT = 7, title = "Огород"), DomainTodoList(id = 2, idPT = 7, title = "Теплица"))
    )

    @Test fun topLevelTaskShowsCurrentListAndEmitsSelection() {
        var selected: TodoTaskDetailsIntent? = null
        compose.setContent { MaterialTheme { TodoTaskListRow(data) { selected = it } } }
        compose.onNodeWithText("Огород").assertIsDisplayed()
        compose.onNodeWithText("Список").performClick()
        compose.onNodeWithText("Теплица").performClick()
        compose.runOnIdle { assertEquals(TodoTaskDetailsIntent.SetList(2), selected) }
    }

    @Test fun subtaskShowsListWithoutClickAction() {
        compose.setContent { MaterialTheme {
            TodoTaskListRow(data.copy(item = data.item.copy(task = task.copy(parentId = 9)))) { error("Подзадача не переносится") }
        } }
        compose.onNodeWithText("Огород").assertIsDisplayed()
        compose.onNode(hasText("Список") and hasClickAction()).assertDoesNotExist()
        compose.onNodeWithText("Теплица").assertDoesNotExist()
    }
}
