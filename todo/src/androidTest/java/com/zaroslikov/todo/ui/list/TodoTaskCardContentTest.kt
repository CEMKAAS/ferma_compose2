package com.zaroslikov.todo.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.zaroslikov.todo.domain.model.DomainTodoSection
import com.zaroslikov.todo.domain.model.DomainTodoTag
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import com.zaroslikov.todo.domain.model.TodoPriority
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.ui.components.TodoReminderRow
import com.zaroslikov.todo.util.TodoDayLabels
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TodoTaskCardContentTest {
    @get:Rule val compose = createComposeRule()

    private val section = DomainTodoSection(id = 1, listId = 1, title = "Утро")
    private val item = DomainTodoTaskItem(
        task = DomainTodoTask(
            id = 1, listId = 1, sectionId = section.id, title = "Корм", statusId = 1,
            dueDate = "2026-11-13", dueTime = "09:30", repeatPeriod = TodoRepeatPeriod.WEEK,
            assignees = listOf("Иван", "Мария"), note = "Подготовить корм", priority = TodoPriority.MEDIUM
        ),
        status = null,
        tags = listOf(DomainTodoTag(idPT = 1, title = "Ферма")),
        commentCount = 1
    )

    @Test fun cardShowsDescriptionBeforeMetadata() {
        val rows = listOf(TodoListRow(item, isDone = false, isSubtask = false))
        val state = TodoListState(isLoading = false, groups = listOf(TodoListGroup(section, rows, cards = rows)))
        compose.setContent {
            MaterialTheme { TodoListColumns(state, Color.Green, onIntent = {}, header = {}) }
        }
        assertContentOrder()
    }

    @Test fun listRowShowsDescriptionAndListBeforeMetadata() {
        compose.setContent {
            MaterialTheme {
                Box(Modifier.fillMaxSize()) {
                    TodoReminderRow(
                        item = item,
                        isDone = false,
                        accent = Color.Green,
                        labels = TodoDayLabels("Сегодня", "Завтра", "Вчера"),
                        onToggle = {},
                        onClick = {},
                        animateSize = false,
                        listTitle = "Хозяйство"
                    )
                }
            }
        }
        assertContentOrder(listTitle = "Хозяйство")
    }

    private fun assertContentOrder(listTitle: String? = null) {
        val nodes = compose.onAllNodes(hasAnyAncestor(isRoot()), useUnmergedTree = true)
            .fetchSemanticsNodes()
        val dueMatcher = hasText("09:30", substring = true)
        val elements = buildList {
            add(hasText("Корм"))
            add(hasText("Подготовить корм"))
            if (listTitle != null) add(hasText(listTitle))
            add(dueMatcher)
            add(hasText("Иван, Мария"))
            add(hasContentDescription("Комментарии: 1"))
            add(hasText("Ферма"))
        }
        val bounds = elements.map { matcher -> nodes.single(matcher::matches).boundsInRoot }
        val due = nodes.single(dueMatcher::matches).boundsInRoot
        val repeatIcon = nodes.single(hasContentDescription("Повтор")::matches).boundsInRoot
        val repeat = nodes.single(hasText("Каждую неделю")::matches).boundsInRoot
        assertTrue("Срок, значок и подпись повтора должны быть видны", listOf(due, repeatIcon, repeat).none { it.isEmpty })
        assertTrue("Повтор должен быть справа от срока", due.right <= repeatIcon.left && repeatIcon.right <= repeat.left)
        assertTrue("Срок и повтор должны быть на одной строке", due.top == repeat.top)
        val priority = nodes.single(hasText("!!")::matches).boundsInRoot
        assertTrue("Все элементы карточки должны быть видны", (bounds + priority).none { it.isEmpty })
        val title = bounds.first()
        assertTrue("Приоритет должен быть перед названием", priority.right <= title.left)
        assertTrue("Приоритет и название должны быть в одной строке", priority.top == title.top)
        bounds.zipWithNext().forEach { (above, below) ->
            assertTrue("Блоки карточки должны отображаться друг под другом", above.bottom <= below.top)
        }
    }
}
