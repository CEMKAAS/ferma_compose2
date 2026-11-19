package com.zaroslikov.todo.ui.task_details

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.ui.theme.green_shamrock
import org.junit.Rule
import org.junit.Test

class TodoAssigneesTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lastAssigneeRemovalDoesNotDropSpacingAfterRowAnimation() {
        compose.mainClock.autoAdvance = false
        var state by mutableStateOf(assignedState(20, listOf("Иван")))
        compose.setContent { MaterialTheme { TodoAssigneesGroup(state, green_shamrock) {} } }
        val input = compose.onNodeWithText("Имя исполнителя")
        compose.runOnIdle { state = assignedState(20, emptyList()) }
        var previousTop = input.fetchSemanticsNode().boundsInRoot.top
        val tolerance = with(compose.density) { 2.dp.toPx() }
        repeat(60) { frame ->
            compose.mainClock.advanceTimeByFrame()
            val top = input.fetchSemanticsNode().boundsInRoot.top
            if (frame >= 20) org.junit.Assert.assertTrue(
                "После сжатия строки отступ не должен исчезать скачком: ${previousTop - top}px",
                kotlin.math.abs(top - previousTop) <= tolerance
            )
            previousTop = top
        }
        compose.onNodeWithText("Иван").assertDoesNotExist()
    }

    @Test fun headingHasNoArrowAndOutsideTapClearsFocusWithoutBlockingOtherFields() {
        val initial = assignedState(20, emptyList()).let {
            it.copy(title = "Корм", data = it.data!!.copy(projectAssignees = listOf("Иван", "Анна")))
        }
        compose.setContent {
            var state by remember { mutableStateOf(initial) }
            MaterialTheme {
                TodoTaskDetailsSheet(state, { intent ->
                    val draft = state.assigneeName
                    state = TodoTaskDetailsReducer.reducer(state, intent)
                    if (intent is TodoTaskDetailsIntent.SelectAssignee)
                        state = TodoTaskDetailsReducer.reducer(state, TodoTaskDetailsIntent.AssigneeAdded(20, draft))
                }, remember { SnackbarHostState() })
            }
        }
        compose.onNodeWithContentDescription("Выбрать исполнителя").assertDoesNotExist()
        compose.onNodeWithText("Имя исполнителя").performScrollTo().performClick().assertIsFocused()
        compose.onNode(isPopup()).assertExists()
        nativeTap("Подробно")
        compose.onNode(isPopup()).assertDoesNotExist()
        compose.onNodeWithText("Имя исполнителя").assertIsNotFocused()
        compose.onNodeWithText("Имя исполнителя").performScrollTo().performClick().assertIsFocused()
        compose.onNodeWithContentDescription("Назначить исполнителя Иван").performClick()
        compose.onNode(isPopup()).assertDoesNotExist()
        nativeTap("Подробно")
        compose.onNodeWithText("Имя исполнителя").assertIsNotFocused()
        compose.onNodeWithText("Имя исполнителя").performScrollTo().performClick().assertIsFocused()
        compose.onNodeWithText("Имя исполнителя").performScrollTo().performClick().assertIsFocused()
        compose.onNode(isPopup()).assertExists()
        compose.onNodeWithText("Название").performScrollTo().performClick().assertIsFocused()
        compose.onNodeWithText("Имя исполнителя").assertIsNotFocused()
    }

    private fun nativeTap(text: String) {
        val node = compose.onNodeWithText(text).fetchSemanticsNode()
        val position = node.positionOnScreen
        onView(isRoot()).perform(GeneralClickAction(Tap.SINGLE, {
            floatArrayOf(position.x + node.size.width / 2f, position.y + node.size.height / 2f)
        }, Press.FINGER))
    }

    @Test fun assignedNamesShowFirstThreeAndExpansionResetsOnTaskSwitch() {
        var state by mutableStateOf(assignedState(20, listOf("Иван", "Мария", "Анна", "Пётр", "Олег")))
        compose.setContent { MaterialTheme { TodoAssigneesGroup(state, green_shamrock) {} } }
        compose.onNodeWithText("Анна").assertExists()
        compose.onNodeWithText("Пётр").assertDoesNotExist()
        compose.onNodeWithText("Показать все").performClick()
        compose.onNodeWithText("Пётр").assertExists()
        compose.onNodeWithText("Олег").assertExists()
        compose.onNodeWithText("Свернуть").performClick()
        compose.onNodeWithText("Олег").assertDoesNotExist()
        compose.onNodeWithText("Показать все").performClick()
        compose.runOnIdle { state = assignedState(21, listOf("Первый", "Второй", "Третий", "Четвёртый")) }
        compose.onNodeWithText("Четвёртый").assertDoesNotExist()
        compose.onNodeWithText("Показать все").assertExists()
    }

    @Test fun removedAssigneeExitsWithoutActiveActionAndNewAssigneeAnimatesIn() {
        compose.mainClock.autoAdvance = false
        var state by mutableStateOf(assignedState(20, listOf("Иван", "Мария")))
        compose.setContent {
            MaterialTheme {
                TodoAssigneesGroup(state, green_shamrock) { state = TodoTaskDetailsReducer.reducer(state, it) }
            }
        }
        compose.onNodeWithContentDescription("Убрать исполнителя Иван").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithContentDescription("Убрать исполнителя Иван").assertIsNotEnabled()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithText("Иван").assertDoesNotExist()
        compose.runOnIdle {
            val data = state.data!!
            state = state.copy(data = data.copy(item = data.item.copy(task = data.task.copy(assignees = listOf("Мария", "Анна")))))
        }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithText("Анна").assertExists()
        compose.onNodeWithText("Мария").assertExists()
        compose.onNodeWithContentDescription("Убрать исполнителя Мария").performClick()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithContentDescription("Убрать исполнителя Анна").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithContentDescription("Убрать исполнителя Анна").assertIsNotEnabled()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithText("Анна").assertDoesNotExist()
        compose.onNodeWithText("Мария").assertDoesNotExist()
    }

    @Test fun expansionReversesWhenCollapsedAndExpandedBeforeExitCompletes() {
        compose.mainClock.autoAdvance = false
        val state = assignedState(20, listOf("Иван", "Мария", "Анна", "Пётр"))
        compose.setContent { MaterialTheme { TodoAssigneesGroup(state, green_shamrock) {} } }
        compose.onNodeWithText("Показать все").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithText("Свернуть").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithText("Показать все").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(500)
        compose.onNodeWithText("Пётр").assertExists()
        compose.onNodeWithContentDescription("Убрать исполнителя Пётр").assertIsEnabled()
    }

    private fun assignedState(taskId: Long, names: List<String>): TodoTaskDetailsState {
        val task = DomainTodoTask(id = taskId, listId = 5, title = "Корм", statusId = 1, assignees = names)
        return TodoTaskDetailsState(taskId = taskId, data = TodoTaskDetailsData(
            item = DomainTodoTaskItem(task = task, status = null), parent = null,
            listTitle = "Хозяйство", listColorIndex = 0, idPT = 7, statuses = emptyList(),
            tags = emptyList(), subtasks = emptyList(), links = emptyList(), comments = emptyList()
        ))
    }

    @Test fun addSeveralNamesAndRemoveOneFromTask() = checkAddAndRemove(parentId = null)

    @Test fun addSeveralNamesAndRemoveOneFromSubtask() = checkAddAndRemove(parentId = 10)

    @Test fun selectExistingNameAfterSearchingExcludesAlreadyAssignedNames() {
        val task = DomainTodoTask(id = 20, listId = 5, title = "Корм", statusId = 1, assignees = listOf("Мария"))
        val data = TodoTaskDetailsData(
            item = DomainTodoTaskItem(task = task, status = null), parent = null,
            listTitle = "Хозяйство", listColorIndex = 0, idPT = 7, statuses = emptyList(),
            tags = emptyList(), subtasks = emptyList(), links = emptyList(), comments = emptyList(),
            projectAssignees = listOf("Мария", "Иван", "Анна")
        )
        compose.setContent {
            var state by remember { mutableStateOf(TodoTaskDetailsState(taskId = 20, data = data)) }
            MaterialTheme {
                TodoAssigneesGroup(state, green_shamrock) { intent ->
                    val draft = state.assigneeName
                    state = TodoTaskDetailsReducer.reducer(state, intent)
                    if (intent is TodoTaskDetailsIntent.SelectAssignee)
                        state = TodoTaskDetailsReducer.reducer(state, TodoTaskDetailsIntent.AssigneeAdded(20, draft))
                }
            }
        }
        compose.onNodeWithText("Имя исполнителя").performClick()
        compose.onNode(isPopup()).assertExists()
        compose.onNodeWithContentDescription("Назначить исполнителя Мария").assertDoesNotExist()
        compose.onNodeWithContentDescription("Назначить исполнителя Анна").assertExists()
        compose.onNodeWithText("Имя исполнителя").performTextInput("и")
        compose.onNodeWithText("Имя исполнителя").assertIsFocused().performTextInput("в")
        compose.onNodeWithContentDescription("Назначить исполнителя Анна").assertDoesNotExist()
        compose.onNodeWithContentDescription("Назначить исполнителя Иван").performClick()
        compose.onNodeWithContentDescription("Убрать исполнителя Иван").assertExists()
        compose.onNodeWithContentDescription("Убрать исполнителя Мария").assertExists()
        compose.onNodeWithContentDescription("Назначить исполнителя Иван").assertDoesNotExist()
        compose.onNode(isPopup()).assertDoesNotExist()
        compose.onNodeWithContentDescription("Добавить исполнителя").assertIsNotEnabled()
    }

    private fun checkAddAndRemove(parentId: Long?) {
        val task = DomainTodoTask(id = 20, listId = 5, title = "Корм", statusId = 1,
            parentId = parentId, assignees = listOf("Мария"))
        val parent = DomainTodoTask(id = 10, listId = 5, title = "Родитель", statusId = 1,
            assignees = listOf("Пётр"))
        val data = TodoTaskDetailsData(
            item = DomainTodoTaskItem(task = task, status = null), parent = parent.takeIf { parentId != null },
            listTitle = "Хозяйство", listColorIndex = 0, idPT = 7, statuses = emptyList(),
            tags = emptyList(), subtasks = emptyList(), links = emptyList(), comments = emptyList()
        )
        compose.setContent {
            var state by remember { mutableStateOf(TodoTaskDetailsState(taskId = 20, data = data)) }
            MaterialTheme {
                TodoAssigneesGroup(state, green_shamrock) { intent ->
                    val name = state.assigneeName
                    state = TodoTaskDetailsReducer.reducer(state, intent)
                    if (intent == TodoTaskDetailsIntent.AddAssignee)
                        state = TodoTaskDetailsReducer.reducer(state, TodoTaskDetailsIntent.AssigneeAdded(20, name))
                }
            }
        }
        compose.onNodeWithContentDescription("Добавить исполнителя").assertIsNotEnabled()
        compose.onNodeWithText("Имя исполнителя").performTextInput("Иван")
        compose.onNodeWithContentDescription("Добавить исполнителя").performClick()
        compose.onNodeWithText("Иван").assertExists()
        compose.onNodeWithText("Мария").assertExists()
        compose.onNodeWithText("Пётр").assertDoesNotExist()
        compose.onNodeWithContentDescription("Убрать исполнителя Мария").performClick()
        compose.onNodeWithText("Мария").assertDoesNotExist()
        compose.onNodeWithText("Иван").assertExists()
        compose.onNodeWithContentDescription("Добавить исполнителя").assertIsNotEnabled()
    }
}
