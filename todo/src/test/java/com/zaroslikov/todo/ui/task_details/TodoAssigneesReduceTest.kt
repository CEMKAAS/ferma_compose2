package com.zaroslikov.todo.ui.task_details

import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.DomainTodoTaskItem
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoAssigneesReduceTest {
    private fun loaded(parentId: Long? = null): TodoTaskDetailsState {
        val task = DomainTodoTask(id = 20, listId = 5, title = "Корм", statusId = 1, parentId = parentId)
        val data = TodoTaskDetailsData(
            item = DomainTodoTaskItem(task = task, status = null), parent = null,
            listTitle = "Хозяйство", listColorIndex = 0, idPT = 7, statuses = emptyList(),
            tags = emptyList(), subtasks = emptyList(), links = emptyList(), comments = emptyList()
        )
        return TodoTaskDetailsReducer.reducer(TodoTaskDetailsState(taskId = 20), TodoTaskDetailsIntent.Loaded(data))
    }

    private fun reduce(state: TodoTaskDetailsState, vararg intents: TodoTaskDetailsIntent) =
        intents.fold(state, TodoTaskDetailsReducer::reducer)

    @Test fun `several assignees are trimmed and duplicates and blanks are ignored`() {
        val state = reduce(loaded(),
            TodoTaskDetailsIntent.AssigneeNameChange(" Иван "), TodoTaskDetailsIntent.AddAssignee,
            TodoTaskDetailsIntent.AssigneeNameChange("Анна"), TodoTaskDetailsIntent.AddAssignee,
            TodoTaskDetailsIntent.AssigneeNameChange("иван"), TodoTaskDetailsIntent.AddAssignee,
            TodoTaskDetailsIntent.AssigneeNameChange(" "), TodoTaskDetailsIntent.AddAssignee)
        assertEquals(listOf("Иван", "Анна"), state.data!!.task.assignees)
    }

    @Test fun `removing one assignee preserves others on a subtask`() {
        val state = reduce(loaded(parentId = 10),
            TodoTaskDetailsIntent.AssigneeNameChange("Иван"), TodoTaskDetailsIntent.AddAssignee,
            TodoTaskDetailsIntent.AssigneeNameChange("Анна"), TodoTaskDetailsIntent.AddAssignee,
            TodoTaskDetailsIntent.RemoveAssignee("Иван"))
        assertEquals(listOf("Анна"), state.data!!.task.assignees)
        assertEquals(10L, state.data!!.task.parentId)
    }

    @Test fun `navigation clears unfinished name while database refresh preserves it`() {
        val state = reduce(loaded(), TodoTaskDetailsIntent.AssigneeNameChange("Черновик"))
        assertEquals("Черновик", reduce(state, TodoTaskDetailsIntent.Loaded(state.data!!)).assigneeName)
        assertEquals("", reduce(state, TodoTaskDetailsIntent.NavigateToTask(21)).assigneeName)
        assertEquals("", reduce(state, TodoTaskDetailsIntent.Close).assigneeName)
    }

    @Test fun `save result clears only its own task and unchanged input`() {
        val submitted = reduce(loaded(), TodoTaskDetailsIntent.AssigneeNameChange("Иван"), TodoTaskDetailsIntent.AddAssignee)
        assertEquals("Иван", submitted.assigneeName)
        assertEquals("", reduce(submitted, TodoTaskDetailsIntent.AssigneeAdded(20, "Иван")).assigneeName)
        val edited = reduce(submitted, TodoTaskDetailsIntent.AssigneeNameChange("Анна"))
        assertEquals("Анна", reduce(edited, TodoTaskDetailsIntent.AssigneeAdded(20, "Иван")).assigneeName)
        assertEquals("Анна", reduce(edited, TodoTaskDetailsIntent.AssigneeAdded(21, "Анна")).assigneeName)
    }

    @Test fun `directory suggestions search names and exclude assigned names`() {
        val state = loaded().let { it.copy(data = it.data!!.copy(projectAssignees = listOf("Иван", "Анна", "Иванов"))) }
        val assigned = reduce(state, TodoTaskDetailsIntent.AssigneeNameChange("Иван"), TodoTaskDetailsIntent.AddAssignee,
            TodoTaskDetailsIntent.AssigneeNameChange("ИВ"))
        assertEquals(listOf("Иванов"), assigned.assigneeCandidates)
        assertEquals(listOf("Анна", "Иванов"), reduce(assigned, TodoTaskDetailsIntent.AssigneeNameChange("")).assigneeCandidates)
    }

    @Test fun `existing name selection assigns immediately and rejects unknown names`() {
        val state = loaded().let { it.copy(data = it.data!!.copy(projectAssignees = listOf("Иван", "Анна"))) }
        val selected = reduce(state, TodoTaskDetailsIntent.OpenAssigneeChoices, TodoTaskDetailsIntent.SelectAssignee("Анна"))
        assertEquals(listOf("Анна"), selected.data!!.task.assignees)
        assertEquals(false, selected.showAssigneeChoices)
        assertEquals(selected, reduce(selected, TodoTaskDetailsIntent.SelectAssignee("Другой проект")))
    }

    @Test fun `navigation never exposes former project directory or late data`() {
        val state = loaded().let { it.copy(data = it.data!!.copy(projectAssignees = listOf("Иван"))) }
        val navigated = reduce(state, TodoTaskDetailsIntent.OpenAssigneeChoices, TodoTaskDetailsIntent.NavigateToTask(21))
        assertEquals(emptyList<String>(), navigated.assigneeCandidates)
        assertEquals(false, navigated.showAssigneeChoices)
        assertEquals(navigated, reduce(navigated, TodoTaskDetailsIntent.Loaded(state.data!!)))
        val otherProject = state.data!!.copy(item = state.data.item.copy(task = state.data.task.copy(id = 21)),
            idPT = 8, projectAssignees = listOf("Анна"))
        assertEquals(listOf("Анна"), reduce(navigated, TodoTaskDetailsIntent.Loaded(otherProject)).assigneeCandidates)
    }
}
