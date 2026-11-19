package com.zaroslikov.todo.ui.smart

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.zaroslikov.todo.domain.model.DomainTodoProjectTask
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoListIcon
import com.zaroslikov.todo.domain.model.TodoSmartList
import java.time.LocalDateTime
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodoSmartListLabelTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun todayShowsListNameWithIconBeforeSchedule() {
        assertListLabel(TodoSmartList.TODAY)
    }

    @Test
    fun scheduledShowsListNameWithIconBeforeSchedule() {
        assertListLabel(TodoSmartList.SCHEDULED)
    }

    private fun assertListLabel(kind: TodoSmartList) {
        val now = LocalDateTime.of(2026, 3, 15, 8, 0)
        val task = DomainTodoProjectTask(
            item = DomainTodoTaskItem(
                task = DomainTodoTask(
                    id = 1,
                    listId = 2,
                    parentId = 3,
                    title = "Проверить капельницы",
                    statusId = 0,
                    dueDate = now.toLocalDate().toString(),
                    dueTime = "09:30"
                ),
                status = null
            ),
            listTitle = "Огород",
            listColorIndex = 0,
            parentTitle = "Настроить полив",
            listIcon = TodoListIcon.PLANTS
        )
        val state = todoSmartReduce(TodoSmartState(kind = kind), TodoSmartIntent.DataLoaded(listOf(task), now))
        compose.setContent {
            TodoSmartBody(
                state = state,
                listState = rememberLazyListState(),
                onIntent = {},
                onTapTail = {},
                onOpen = {}
            )
        }

        val schedule = compose.onNodeWithText("09:30", substring = true, useUnmergedTree = true)
            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val parent = compose.onNodeWithText("Настроить полив", substring = true, useUnmergedTree = true)
            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot
        val listLabel = compose.onNodeWithText("Огород", useUnmergedTree = true)
            .assertIsDisplayed().fetchSemanticsNode().boundsInRoot

        assertTrue("Название списка должно быть перед родительской задачей", listLabel.bottom <= parent.top)
        assertTrue("Срок должен отображаться после родительской задачи", parent.bottom <= schedule.top)
        assertTrue("Перед названием списка должно быть место под значок", listLabel.left > schedule.left)
    }
}
