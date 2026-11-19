package com.zaroslikov.todo.ui.assignees

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.Espresso.pressBack
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodoAssigneeProfileUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun cardShowsProfileAndOpensEditor() {
        val profile = TodoAssigneeProfile(
            firstName = "Анна",
            lastName = "Орлова",
            birthday = "1991-02-13",
            description = "Ведёт списки"
        )
        val state = mutableStateOf(TodoAssigneesState(
            isLoading = false,
            assignees = listOf(DomainTodoAssignee(7, "Анна Орлова", profile))
        ))
        compose.setContent {
            MaterialTheme {
                TodoAssigneesContent(state.value, onIntent = {
                    state.value = todoAssigneesReduce(state.value, it)
                }, navigateBack = {})
            }
        }

        compose.onNodeWithText("Анна Орлова").performClick()
        compose.onNodeWithText("Фамилия").assertIsDisplayed()
        compose.onNodeWithText("13.02.1991").assertIsDisplayed()
        compose.onNodeWithText("Ведёт списки").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Редактировать").performClick()
        compose.onNodeWithText("Изменить исполнителя").assertIsDisplayed()
        compose.onNodeWithText("Сохранить").assertIsEnabled()
        compose.runOnIdle { assertEquals(profile, state.value.form?.profile) }
    }

    @Test fun emptyProfileCanBeSaved() {
        val state = mutableStateOf(TodoAssigneesState(isLoading = false))
        compose.setContent {
            MaterialTheme {
                TodoAssigneesContent(state.value, onIntent = {
                    state.value = todoAssigneesReduce(state.value, it)
                }, navigateBack = {})
            }
        }

        compose.onNodeWithText("Добавить исполнителя").performClick()
        compose.onNodeWithText("Сохранить").assertIsEnabled().performClick()
        compose.runOnIdle {
            assertTrue(state.value.isSaving)
            assertEquals(TodoAssigneeProfile(), state.value.form?.profile)
        }
    }

    @Test fun birthdayFieldOpensCalendarWithoutTextEntry() {
        openEmptyProfileEditor()

        compose.onNodeWithContentDescription("Выбрать дату рождения")
            .performScrollTo()
            .assertHasClickAction()
            .assert(!hasSetTextAction())
            .performTouchInput { click() }

        compose.onNodeWithText("ОК").assertIsDisplayed()
        compose.onNodeWithContentDescription("Switch to text input mode").assertDoesNotExist()
    }

    @Test fun saveIsHiddenWhileTheKeyboardIsOpen() {
        openEmptyProfileEditor()

        compose.onNodeWithText("Сохранить").assertIsDisplayed()
        compose.onNodeWithText("Имя").performTouchInput { click() }
        compose.waitUntil(timeoutMillis = 5_000) {
            compose.onAllNodesWithText("Сохранить").fetchSemanticsNodes().isEmpty()
        }
        pressBack()

        compose.onNodeWithText("Сохранить").assertIsDisplayed()
    }

    @Test fun extraDigitDoesNotClearThePhoneMask() {
        val state = openEmptyProfileEditor()
        val phone = compose.onNodeWithText("Номер телефона").performScrollTo()
        phone.performTextInput("9123456789")
        phone.assertTextContains("+7 (912) 345-67-89")

        phone.performTextInput("0")

        phone.assertTextContains("+7 (912) 345-67-89")
        compose.runOnIdle { assertEquals("9123456789", state.value.form?.profile?.phone) }
    }

    private fun openEmptyProfileEditor(): State<TodoAssigneesState> {
        val state = mutableStateOf(TodoAssigneesState(isLoading = false))
        compose.setContent {
            MaterialTheme {
                TodoAssigneesContent(state.value, onIntent = {
                    state.value = todoAssigneesReduce(state.value, it)
                }, navigateBack = {})
            }
        }
        compose.onNodeWithText("Добавить исполнителя").performClick()
        return state
    }
}
