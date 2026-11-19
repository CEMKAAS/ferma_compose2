package com.zaroslikov.todo.ui.task_details

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.espresso.Espresso.pressBack
import com.zaroslikov.todo.domain.model.*
import org.junit.Rule
import org.junit.Test

class TodoRepeatSheetTest {
    @get:Rule val compose = createComposeRule()

    private fun show(date: String?) {
        val data = TodoTaskDetailsData(
            DomainTodoTaskItem(DomainTodoTask(id = 1, listId = 1, title = "Корм", statusId = 1, dueDate = date), null),
            null, "Список", 0, 1, emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
        )
        compose.setContent {
            var state by remember { mutableStateOf(TodoTaskDetailsState(taskId = 1, data = data, title = "Корм")) }
            MaterialTheme {
                TodoTaskDetailsSheet(state, { state = TodoTaskDetailsReducer.reducer(state, it) }, remember { SnackbarHostState() })
            }
        }
    }
    private fun period(text: String) = compose.onNode(
        hasText(text) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))

    @Test fun repeatHiddenWithoutDate() {
        show(null)
        compose.onNodeWithText("Повтор").assertDoesNotExist()
    }
    @Test fun selectingSavingAndReopeningKeepsPeriod() {
        show("2026-11-14")
        compose.onNodeWithText("Повтор").performScrollTo().performClick()
        period("Каждую неделю").performClick().assertIsSelected()
        compose.onNodeWithText("Дата окончания повтора").assertIsDisplayed()
        compose.onNode(isToggleable() and hasAnySibling(hasText("Без окончания"))).performClick()
        compose.onNodeWithText("14.11.2026").performClick()
        compose.onNodeWithText("ОК", ignoreCase = true).performClick()
        compose.onNodeWithText("Сохранить").performClick()
        compose.onNodeWithText("Каждую неделю").assertIsDisplayed()
        compose.onNodeWithText("До 14.11.2026").assertIsDisplayed()
        compose.onNodeWithText("Повтор").performClick()
        period("Каждую неделю").assertIsSelected()
    }
    @Test fun backDiscardsRepeatDraft() {
        show("2026-11-14")
        compose.onNodeWithText("Повтор").performScrollTo().performClick()
        period("Каждый месяц").performClick()
        pressBack()
        compose.onNodeWithText("Не повторять").assertIsDisplayed()
        compose.onNodeWithText("Повтор").performClick()
        period("Не повторять").assertIsSelected()
    }
}
