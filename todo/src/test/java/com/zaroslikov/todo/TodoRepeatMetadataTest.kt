package com.zaroslikov.todo

import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.TodoRepeatPeriod
import com.zaroslikov.todo.ui.components.todoRepeatMetadataColor
import com.zaroslikov.todo.ui.theme.error_base
import com.zaroslikov.todo.ui.theme.gray_7
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoRepeatMetadataTest {
    private val now = LocalDateTime.of(2026, 11, 14, 12, 0)
    private val task = DomainTodoTask(listId = 1, title = "Корм", statusId = 1,
        dueDate = "2026-11-14", repeatPeriod = TodoRepeatPeriod.DAY)

    @Test fun dateOnlyBecomesRedOnFollowingDay() {
        assertEquals(gray_7, todoRepeatMetadataColor(task, false, now))
        assertEquals(error_base, todoRepeatMetadataColor(task.copy(dueDate = "2026-11-13"), false, now))
    }
    @Test fun timeCutoffAndCompletedTaskUseSameDeadlineRules() {
        assertEquals(gray_7, todoRepeatMetadataColor(task.copy(dueTime = "12:00"), false, now))
        assertEquals(error_base, todoRepeatMetadataColor(task.copy(dueTime = "11:59"), false, now))
        assertEquals(gray_7, todoRepeatMetadataColor(task.copy(dueDate = "2026-11-13"), true, now))
    }
}
