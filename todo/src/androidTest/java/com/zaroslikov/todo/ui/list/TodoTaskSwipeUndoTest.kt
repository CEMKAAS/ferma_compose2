package com.zaroslikov.todo.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import com.zaroslikov.todo.ui.base.TodoSnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.ui.base.TodoUiEvent
import com.zaroslikov.todo.ui.base.showTodoMessage
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TodoTaskSwipeUndoTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun undoRestoresSwipedRowWithoutDeletingItAgain() {
        var deleteCount = 0
        compose.setContent {
            var visible by remember { mutableStateOf(true) }
            val host = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            val row = TodoListRow(
                DomainTodoTaskItem(DomainTodoTask(id = 42, listId = 1, title = "Полить", statusId = 1), null),
                isDone = false,
                isSubtask = false
            )
            MaterialTheme {
                Scaffold(snackbarHost = { TodoSnackbarHost(host) }) { _ ->
                    LazyColumn {
                        if (visible) item(key = row.item.task.id) {
                            TodoTaskSwipeRow(
                                row = row,
                                swipeEnabled = true,
                                onDelete = {
                                    deleteCount++
                                    visible = false
                                    scope.launch {
                                        host.showTodoMessage(context, TodoUiEvent.Message(R.string.todo_list_msg_task_deleted) {
                                            visible = true
                                        })
                                    }
                                },
                                modifier = Modifier.testTag("task")
                            ) {
                                Box(Modifier.fillMaxWidth().height(72.dp)) { Text(row.item.task.title) }
                            }
                        }
                    }
                }
            }
        }

        repeat(2) { index ->
            compose.onNodeWithTag("task").performTouchInput { swipeLeft() }
            compose.onNodeWithText("Отменить").assertIsDisplayed().performClick()
            compose.waitForIdle()
            // Несколько кадров: восстановленная сохранённая dismiss-позиция не должна
            // повторно вызывать onDismiss или оставлять содержимое за границами строки.
            compose.mainClock.advanceTimeBy(1_000)
            compose.onNodeWithText("Полить").assertIsDisplayed()
            compose.runOnIdle { assertEquals(index + 1, deleteCount) }
        }
    }
}
