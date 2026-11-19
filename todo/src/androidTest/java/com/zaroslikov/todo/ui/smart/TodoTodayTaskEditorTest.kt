package com.zaroslikov.todo.ui.smart

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.zaroslikov.todo.domain.model.TodoSmartList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TodoTodayTaskEditorTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun dottedRowOpensFocusedEditorAndEnterKeepsItOpenUntilDetails() {
        val submissions = mutableListOf<Pair<NewTaskAction, String>>()
        compose.setContent {
            var state by remember {
                mutableStateOf(TodoSmartState(kind = TodoSmartList.TODAY, isLoading = false))
            }
            TodoSmartBody(
                state = state,
                listState = rememberLazyListState(),
                onIntent = { intent ->
                    // Результат записи из Room не подаём: следующая задача вводится сразу.
                    val draft = state.newTask
                    if (intent is TodoSmartIntent.SubmitNewTask && draft != null && draft.title.isNotBlank()) {
                        submissions += intent.action to draft.title
                    }
                    state = todoSmartReduce(state, intent)
                },
                onTapTail = {},
                onOpen = {}
            )
        }

        compose.onNodeWithContentDescription("Новая задача").performClick()
        val title = compose.onNode(hasSetTextAction())
        title.assertIsFocused().performTextInput("Полить")
        title.performImeAction()
        title.assertIsFocused().performTextInput("Собрать")
        compose.onNodeWithContentDescription("Подробнее о задаче").performClick()
        title.assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(
                listOf(NewTaskAction.KEEP_EDITING to "Полить", NewTaskAction.OPEN_DETAILS to "Собрать"),
                submissions
            )
        }
    }
}
