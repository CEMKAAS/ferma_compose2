package com.zaroslikov.todo.ui.task_details

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso.pressBack
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import org.junit.Rule
import org.junit.Test

class TodoTagsSheetTest {
    @get:Rule val compose = createComposeRule()

    private val feed = DomainTodoTag(id = 10, idPT = 7, title = "Корм")
    private val repair = DomainTodoTag(id = 11, idPT = 7, title = "Ремонт")
    private val data = TodoTaskDetailsData(
        item = DomainTodoTaskItem(
            task = DomainTodoTask(id = 20, listId = 5, title = "Купить корм", statusId = 1),
            status = null, tags = listOf(feed)
        ),
        parent = null, listTitle = "Хозяйство", listColorIndex = 1, idPT = 7,
        statuses = emptyList(), tags = listOf(feed, repair),
        subtasks = emptyList(), links = emptyList(), comments = emptyList()
    )

    @Test
    fun savedTagCardOpensPickerWithoutChangingSelection() {
        showDetails()
        compose.onNodeWithContentDescription("Корм").performScrollTo().performClick()
        tag("Корм").assertIsSelected()
        tag("Ремонт").assertIsNotSelected()
        pressBack()
        compose.onNodeWithContentDescription("Корм").assertIsDisplayed()
    }

    @Test
    fun saveAppliesSelectionAndReopeningKeepsSavedTags() {
        showDetails()
        compose.onNodeWithText("Теги").performScrollTo().performClick()
        tag("Корм").assertIsSelected()
        tag("Ремонт").assertIsNotSelected().performClick()
        tag("Ремонт").assertIsSelected()
        tag("Корм").performClick().assertIsNotSelected()
        compose.onNodeWithText("Сохранить").performClick()
        compose.onNodeWithContentDescription("Ремонт").assertIsDisplayed()
        compose.onNodeWithText("Теги").performClick()
        tag("Ремонт").assertIsSelected()
        tag("Корм").assertIsNotSelected()
    }

    @Test
    fun backDiscardsSelectionAndResetWithoutSaving() {
        showDetails()
        compose.onNodeWithText("Теги").performScrollTo().performClick()
        tag("Ремонт").performClick()
        tag("Корм").performClick()
        pressBack()
        compose.onNodeWithText("Теги").performClick()
        tag("Корм").assertIsSelected()
        tag("Ремонт").assertIsNotSelected()
        compose.onNodeWithText("Сброс").performClick()
        tag("Корм").assertIsNotSelected()
        pressBack()
        compose.onNodeWithText("Теги").performClick()
        tag("Корм").assertIsSelected()
        tag("Ремонт").assertIsNotSelected()
    }

    @Test
    fun cancellingNewTagReturnsToPicker() {
        showDetails()
        compose.onNodeWithText("Теги").performScrollTo().performClick()
        compose.onNodeWithText("Новый тег").performClick()
        compose.onNodeWithText("Создать").performScrollTo().assertIsDisplayed()
        pressBack()
        tag("Корм").assertIsSelected()
        tag("Ремонт").assertIsNotSelected()
        compose.onNodeWithText("Новый тег").assertIsDisplayed()
    }

    @Test
    fun resetClearsSelectionAndCloseReturnsToDetails() {
        showDetails()
        compose.onNodeWithText("Теги").performScrollTo().performClick()
        tag("Ремонт").performClick()
        compose.onNodeWithText("Сброс").performClick()
        tag("Корм").assertIsNotSelected()
        tag("Ремонт").assertIsNotSelected()
        compose.onNodeWithText("Сохранить").performClick()
        compose.onNodeWithText("Подробно").assertIsDisplayed()
        compose.onNodeWithText("Сброс").assertDoesNotExist()
        compose.onNodeWithText("Теги").performClick()
        tag("Корм").assertIsNotSelected()
        tag("Ремонт").assertIsNotSelected()
    }

    @Test
    fun headerCrossClosesPickerAndDetailsAndNewTagFollowsTags() {
        showDetails()
        compose.onNodeWithText("Теги").performScrollTo().performClick()
        tag("Новый тег").assertIsDisplayed()
        val lastTag = tag("Ремонт").fetchSemanticsNode().boundsInRoot
        val newTag = tag("Новый тег").fetchSemanticsNode().boundsInRoot
        org.junit.Assert.assertTrue(newTag.top > lastTag.top || newTag.left > lastTag.left)
        compose.onNodeWithContentDescription("Закрыть все меню").performClick()
        compose.onNodeWithText("Подробно").assertDoesNotExist()
        compose.onNodeWithText("Сброс").assertDoesNotExist()
    }

    @Test
    fun footerRemainsVisibleWithManyTags() {
        val manyTags = (1L..100L).map { DomainTodoTag(id = it, idPT = 7, title = "Тег $it") }
        compose.setContent {
            MaterialTheme {
                TodoTagsSheet(data = data.copy(tags = manyTags), onIntent = {})
            }
        }
        compose.onNodeWithText("Сброс").assertIsDisplayed()
        compose.onNodeWithText("Сохранить").assertIsDisplayed()
        tag("Новый тег").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Сброс").assertIsDisplayed()
        compose.onNodeWithText("Сохранить").assertIsDisplayed()
    }

    private fun tag(title: String) = compose.onNode(
        hasText(title) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)
    )

    private fun showDetails() {
        compose.setContent {
            var state by remember {
                mutableStateOf(TodoTaskDetailsState(taskId = 20, data = data, title = data.task.title))
            }
            MaterialTheme {
                if (state.isOpen)
                TodoTaskDetailsSheet(
                    state = state,
                    onIntent = { state = TodoTaskDetailsReducer.reducer(state, it) },
                    snackbarHostState = remember { SnackbarHostState() }
                )
            }
        }
    }
}
