package com.zaroslikov.todo.ui.task_details

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoList
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import com.zaroslikov.todo.domain.model.TodoLinkType
import org.junit.Rule
import org.junit.Test

class TodoLinkTooltipTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun relatesHelpOpensOnTapWithoutSelectingType() = checkTooltip(
        "Связана с",
        "Связывает задачи без направления. При завершении любой задачи автоматически завершается вся цепочка связанных задач."
    )

    @Test
    fun outgoingHelpOpensOnTapWithoutSelectingType() = checkTooltip(
        "Блокирует",
        "Текущая задача обозначается как блокирующая выбранную. При завершении любой из них автоматически завершается вся цепочка связанных задач."
    )

    @Test
    fun incomingHelpOpensOnTapWithoutSelectingType() = checkTooltip(
        "Заблокирована задачей",
        "Выбранная задача обозначается как блокирующая текущую. При завершении любой из них автоматически завершается вся цепочка связанных задач."
    )

    private fun checkTooltip(typeTitle: String, explanation: String) {
        val intents = mutableListOf<TodoTaskDetailsIntent>()
        compose.setContent {
            MaterialTheme {
                TodoLinkSheet(
                    draft = TodoLinkDraft(), candidates = emptyList(), lists = emptyMap(),
                    accent = Color.Blue, onIntent = intents::add
                )
            }
        }

        compose.onNodeWithContentDescription("О типе связи «$typeTitle»").performClick()

        compose.onNodeWithText(explanation).assertIsDisplayed()
        compose.runOnIdle { assertTrue(intents.isEmpty()) }
    }

    @Test
    fun pickerShowsCandidateStatusAndSelectsAllTasksFilter() {
        val intents = mutableListOf<TodoTaskDetailsIntent>()
        val status = DomainTodoStatus(id = 9, idPT = 7, title = "На проверке", colorIndex = 2)
        compose.setContent {
            var draft by remember { mutableStateOf(TodoLinkDraft()) }
            MaterialTheme {
                TodoLinkSheet(
                    draft = draft,
                    candidates = listOf(DomainTodoTask(id = 10, listId = 2, title = "Проверить корм", statusId = 9)),
                    lists = mapOf(2L to DomainTodoList(id = 2, idPT = 7, title = "Теплица")),
                    statuses = mapOf(9L to status), accent = Color.Blue,
                    onIntent = {
                        intents += it
                        if (it is TodoTaskDetailsIntent.LinkShowCompletedChange) draft = draft.copy(showCompleted = it.showCompleted)
                    }
                )
            }
        }

        compose.onNodeWithText("Незавершённые").performScrollTo().assertIsSelected()
        compose.onNodeWithText("На проверке").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Все задачи").performScrollTo().performClick().assertIsSelected()
        compose.runOnIdle {
            assertEquals(listOf(TodoTaskDetailsIntent.LinkShowCompletedChange(true)), intents)
        }
    }

    @Test
    fun radioLabelStillSelectsLinkType() {
        val intents = mutableListOf<TodoTaskDetailsIntent>()
        compose.setContent {
            MaterialTheme {
                TodoLinkSheet(
                    draft = TodoLinkDraft(), candidates = emptyList(), lists = emptyMap(),
                    accent = Color.Blue, onIntent = intents::add
                )
            }
        }

        compose.onNodeWithText("Заблокирована задачей").performClick()

        compose.runOnIdle {
            assertEquals(listOf(TodoTaskDetailsIntent.LinkTypeChange(TodoLinkType.BLOCKS, false)), intents)
        }
    }
}
