package com.zaroslikov.todo.ui.task_details

import com.zaroslikov.todo.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class TodoRepeatReduceTest {
    private fun state(date: String? = "2026-11-14"): TodoTaskDetailsState {
        val task = DomainTodoTask(id = 1, listId = 1, title = "Корм", statusId = 1, dueDate = date)
        return TodoTaskDetailsState(taskId = 1, data = TodoTaskDetailsData(
            DomainTodoTaskItem(task, null), null, "Список", 0, 1,
            emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
        ))
    }
    private fun reduce(state: TodoTaskDetailsState, vararg intents: TodoTaskDetailsIntent) =
        intents.fold(state, TodoTaskDetailsReducer::reducer)

    @Test fun repeatRequiresDateAndDismissDiscardsDraft() {
        assertNull(reduce(state(null), TodoTaskDetailsIntent.OpenRepeatSheet).repeatDraft)
        val edited = reduce(state(), TodoTaskDetailsIntent.OpenRepeatSheet,
            TodoTaskDetailsIntent.RepeatPeriodChange(TodoRepeatPeriod.WEEK))
        assertEquals(TodoRepeatPeriod.NONE, edited.data!!.task.repeatPeriod)
        assertNull(reduce(edited, TodoTaskDetailsIntent.CloseRepeatSheet).repeatDraft)
    }
    @Test fun savePersistsAndDisablingDateClearsRepeat() {
        val saved = reduce(state(), TodoTaskDetailsIntent.OpenRepeatSheet,
            TodoTaskDetailsIntent.RepeatPeriodChange(TodoRepeatPeriod.DAY),
            TodoTaskDetailsIntent.RepeatEndChange("2026-12-01"), TodoTaskDetailsIntent.SaveRepeat)
        assertEquals(TodoRepeatPeriod.DAY, saved.data!!.task.repeatPeriod)
        assertEquals("2026-12-01", saved.data.task.repeatEndDate)
        assertNull(saved.repeatDraft)
        val cleared = reduce(saved, TodoTaskDetailsIntent.SetDueDate(null)).data!!.task
        assertEquals(TodoRepeatPeriod.NONE, cleared.repeatPeriod)
        assertNull(cleared.repeatEndDate)
    }
    @Test fun invalidEndStaysInDraftAndMovingDueDateNormalizesEnd() {
        val invalid = reduce(state(), TodoTaskDetailsIntent.OpenRepeatSheet,
            TodoTaskDetailsIntent.RepeatPeriodChange(TodoRepeatPeriod.MONTH),
            TodoTaskDetailsIntent.RepeatEndChange("2026-11-01"), TodoTaskDetailsIntent.SaveRepeat)
        assertNotNull(invalid.repeatDraft)
        assertTrue(invalid.repeatDraft!!.hasError)
        assertEquals(TodoRepeatPeriod.NONE, invalid.data!!.task.repeatPeriod)
        val saved = reduce(invalid, TodoTaskDetailsIntent.RepeatEndChange("2026-12-01"), TodoTaskDetailsIntent.SaveRepeat)
        val moved = reduce(saved, TodoTaskDetailsIntent.SetDueDate("2026-12-10"))
        assertEquals("2026-12-10", moved.data!!.task.repeatEndDate)
    }
}
