package com.zaroslikov.todo.ui.task_details

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.zaroslikov.todo.domain.model.*
import com.zaroslikov.todo.ui.components.TodoRepeatMetadata
import com.zaroslikov.todo.ui.components.TodoReminderRow
import com.zaroslikov.todo.ui.list.TodoCardMeta
import com.zaroslikov.todo.ui.list.TodoListRow
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_7
import com.zaroslikov.todo.ui.theme.green_shamrock
import com.zaroslikov.todo.util.TodoDayLabels
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class TodoRepeatMetadataUiTest {
    @get:Rule val compose = createComposeRule()
    private val task = DomainTodoTask(listId = 1, title = "Корм", statusId = 1,
        dueDate = "2026-11-13", repeatPeriod = TodoRepeatPeriod.WEEK)
    private val labels = TodoDayLabels("Сегодня", "Завтра", "Вчера")

    @Test fun ordinaryTaskHasNoRepeatIconOrLabel() {
        compose.setContent { MaterialTheme {
            TodoRepeatMetadata(task.copy(repeatPeriod = TodoRepeatPeriod.NONE), false)
        } }
        compose.onNodeWithContentDescription("Повтор").assertDoesNotExist()
        compose.onNodeWithText("Каждую неделю").assertDoesNotExist()
    }
    @Test fun missingDateHasNoRepeatMetadata() {
        compose.setContent { MaterialTheme { TodoRepeatMetadata(task.copy(dueDate = null), false) } }
        compose.onNodeWithContentDescription("Повтор").assertDoesNotExist()
    }
    @Test fun overdueRepeatIconAndLabelAreRed() {
        compose.setContent { MaterialTheme {
            TodoRepeatMetadata(task, false, now = LocalDateTime.of(2026, 11, 14, 12, 0))
        } }
        assertColor(compose.onNodeWithContentDescription("Повтор"), error_base)
        assertColor(compose.onNodeWithText("Каждую неделю"), error_base)
    }
    @Test fun completedOverdueRepeatIconAndLabelAreNeutral() {
        compose.setContent { MaterialTheme {
            TodoRepeatMetadata(task, true, now = LocalDateTime.of(2026, 11, 14, 12, 0))
        } }
        assertColor(compose.onNodeWithContentDescription("Повтор"), gray_7)
        assertColor(compose.onNodeWithText("Каждую неделю"), gray_7)
    }
    @Test fun sharedListRowIncludesRepeatMetadata() {
        compose.setContent { MaterialTheme {
            TodoReminderRow(DomainTodoTaskItem(task, null), false, green_shamrock, labels, {}, {})
        } }
        compose.onNodeWithText("Каждую неделю").assertIsDisplayed()
        compose.onNodeWithContentDescription("Повтор").assertIsDisplayed()
    }
    @Test fun columnCardIncludesRepeatMetadata() {
        compose.setContent { MaterialTheme {
            TodoCardMeta(TodoListRow(DomainTodoTaskItem(task, null), false, false), labels)
        } }
        compose.onNodeWithText("Каждую неделю").assertIsDisplayed()
        compose.onNodeWithContentDescription("Повтор").assertIsDisplayed()
    }
    private fun assertColor(node: SemanticsNodeInteraction, expected: androidx.compose.ui.graphics.Color) {
        val pixels = node.captureToImage().toPixelMap()
        var found = false
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
            val color = pixels[x, y]
            if (kotlin.math.abs(color.red - expected.red) < .03f &&
                kotlin.math.abs(color.green - expected.green) < .03f &&
                kotlin.math.abs(color.blue - expected.blue) < .03f) found = true
        }
        assertTrue("Цвет значка и периода должен соответствовать сроку и состоянию задачи", found)
    }
}
