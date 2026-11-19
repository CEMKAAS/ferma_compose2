package com.zaroslikov.todo.ui.base

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.zaroslikov.todo.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodoSnackbarTimerTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun countdownDecreasesAndDismissesWithoutUndo() {
        var undone = false
        showUndo { undone = true }
        compose.onNodeWithText("10 с").assertIsDisplayed()
        val progress = compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
        val initialProgress = progress.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current
        compose.mainClock.advanceTimeBy(2_000)
        compose.onNodeWithText("8 с").assertIsDisplayed()
        val remainingProgress = progress.fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo].current
        assertTrue(remainingProgress > 0f && remainingProgress < initialProgress)
        compose.mainClock.advanceTimeBy(8_500)
        compose.onNodeWithText("Отменить").assertDoesNotExist()
        progress.assertDoesNotExist()
        compose.runOnIdle { assertFalse(undone) }
    }

    @Test
    fun undoStillWorksWhileCountdownIsRunning() {
        var undone = false
        showUndo { undone = true }
        compose.mainClock.advanceTimeBy(2_000)
        compose.onNodeWithText("8 с").assertIsDisplayed()
        compose.onNodeWithText("Отменить").performClick()
        compose.mainClock.advanceTimeBy(500)
        compose.runOnIdle { assertTrue(undone) }
        compose.onNodeWithText("Отменить").assertDoesNotExist()
    }

    private fun showUndo(onUndo: () -> Unit) {
        compose.mainClock.autoAdvance = false
        compose.setContent {
            val host = remember { SnackbarHostState() }
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                host.showTodoMessage(context, TodoUiEvent.Message(R.string.todo_list_msg_task_deleted, onUndo))
            }
            MaterialTheme { TodoSnackbarHost(host) }
        }
        compose.mainClock.advanceTimeBy(500)
    }
}
