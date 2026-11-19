package com.zaroslikov.todo.ui.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.unit.dp
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class TodoListSearchScrollTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var barState: TopAppBarState

    @Test
    fun searchStaysCollapsedAfterUpwardSwipeAndReturnsBeforeReachingListStart() {
        lateinit var listState: LazyListState
        compose.setContent {
            listState = rememberLazyListState()
            SearchScaffold(query = "Полить") { modifier ->
                LazyColumn(modifier = modifier, state = listState) {
                    items(40) { index ->
                        Text("Задача $index", Modifier.fillMaxWidth().height(56.dp))
                    }
                }
            }
        }

        val search = compose.onNodeWithContentDescription("Поиск задач")
        search.assertIsDisplayed().assertIsNotFocused()
        swipeVertically(start = 0.85f, end = 0.15f)
        assertCollapsedFraction(1f)
        search.assertIsNotDisplayed()

        swipeVertically(start = 0.35f, end = 0.55f)
        search.assertIsDisplayed().assertTextContains("Полить")
        assertCollapsedFraction(0f)
        compose.runOnIdle { assertTrue(listState.firstVisibleItemIndex > 0) }
    }

    @Test
    fun horizontalColumnScrollLeavesSearchVisible() {
        showColumns()
        compose.onNodeWithTag("content").performTouchInput { swipeLeft() }
        val scrollRange = compose.onNode(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.HorizontalScrollAxisRange),
            useUnmergedTree = true
        ).fetchSemanticsNode().config[SemanticsProperties.HorizontalScrollAxisRange]
        compose.runOnIdle { assertTrue(scrollRange.value() > 0f) }
        compose.onNodeWithContentDescription("Поиск задач").assertIsDisplayed()
        assertCollapsedFraction(0f)
    }

    @Test
    fun cardsWithinColumnCollapseAndRevealSearchOnVerticalScroll() {
        showColumns()
        swipeVertically(start = 0.85f, end = 0.25f)
        assertCollapsedFraction(1f)
        swipeVertically(start = 0.35f, end = 0.60f)
        compose.onNodeWithContentDescription("Поиск задач").assertIsDisplayed()
        assertCollapsedFraction(0f)
    }

    @Test
    fun downwardSwipeRevealsSearchWhenColumnsHaveNoMatches() {
        showColumns(noMatches = true)
        compose.runOnIdle { barState.heightOffset = barState.heightOffsetLimit }
        assertCollapsedFraction(1f)

        swipeVertically(start = 0.35f, end = 0.60f)

        compose.onNodeWithContentDescription("Поиск задач").assertIsDisplayed()
        assertCollapsedFraction(0f)
    }

    private fun showColumns(noMatches: Boolean = false) {
        val groups = (1L..4L).map { sectionId ->
            val section = DomainTodoSection(id = sectionId, listId = 1, title = "Столбец $sectionId")
            val rows = (1L..20L).map { number ->
                val task = DomainTodoTask(
                    id = sectionId * 100 + number,
                    listId = 1,
                    sectionId = sectionId,
                    title = "Карточка $sectionId.$number",
                    statusId = 1
                )
                TodoListRow(DomainTodoTaskItem(task, status = null), isDone = false, isSubtask = false)
            }
            TodoListGroup(section, rows, cards = rows)
        }
        val state = if (noMatches) TodoListState(isLoading = false, query = "Нет совпадений")
        else TodoListState(isLoading = false, groups = groups)
        compose.setContent {
            SearchScaffold(query = state.query) { modifier ->
                TodoListColumns(state, Color.Green, onIntent = {}, header = {}, modifier = modifier)
            }
        }
    }

    @Composable
    private fun SearchScaffold(query: String = "", content: @Composable (Modifier) -> Unit) {
        barState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(barState)
        MaterialTheme {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = { TodoListSearchBar(query, Color.Green, scrollBehavior, onQueryChange = {}) }
            ) { padding ->
                content(Modifier.fillMaxSize().padding(padding).testTag("content"))
            }
        }
    }

    private fun swipeVertically(start: Float, end: Float) {
        compose.onNodeWithTag("content").performTouchInput {
            swipe(Offset(width * 0.25f, height * start), Offset(width * 0.25f, height * end), 350)
        }
    }

    private fun assertCollapsedFraction(expected: Float) {
        compose.runOnIdle { assertEquals(expected, barState.collapsedFraction, 0.01f) }
    }
}
