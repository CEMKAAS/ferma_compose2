package com.zaroslikov.todo.ui.assignees

import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoAssigneesReduceTest {
    private val ivan = DomainTodoAssignee(7, "Иван", TodoAssigneeProfile(firstName = "Иван", phone = "+7 123"))
    private val anna = DomainTodoAssignee(7, "Анна")
    private val loaded = todoAssigneesReduce(
        TodoAssigneesState(),
        TodoAssigneesIntent.Loaded(listOf(ivan, anna))
    )

    @Test
    fun allowsEmptyProfileAndRejectsDuplicateFullName() {
        val opened = todoAssigneesReduce(loaded, TodoAssigneesIntent.OpenForm())
        assertTrue(todoAssigneesReduce(opened, TodoAssigneesIntent.Save("Исполнитель")).isSaving)

        val edited = todoAssigneesReduce(opened, TodoAssigneesIntent.ProfileChanged(TodoAssigneeProfile(firstName = " иВАН ")))
        val submitted = todoAssigneesReduce(edited, TodoAssigneesIntent.Save("Исполнитель"))

        assertFalse(submitted.isSaving)
        assertEquals(R.string.todo_assignee_exists, submitted.form?.error)
    }

    @Test
    fun allowsCaseOnlyRenameAndPreservesDraftOnFailure() {
        val opened = todoAssigneesReduce(loaded, TodoAssigneesIntent.OpenForm("Иван"))
        val profile = ivan.profile.copy(firstName = "ИВАН", email = "ivan@example.org")
        val edited = todoAssigneesReduce(opened, TodoAssigneesIntent.ProfileChanged(profile))
        val submitted = todoAssigneesReduce(edited, TodoAssigneesIntent.Save("Исполнитель"))

        assertTrue(submitted.isSaving)
        val failed = todoAssigneesReduce(submitted, TodoAssigneesIntent.SaveFailed(R.string.todo_msg_error))
        assertFalse(failed.isSaving)
        assertEquals(profile, failed.form?.profile)
        assertEquals(R.string.todo_msg_error, failed.form?.error)
        assertNull(todoAssigneesReduce(submitted, TodoAssigneesIntent.Saved).form)
    }

    @Test
    fun opensDetailsThenSeedsEditorAndDiscardsCanceledChanges() {
        val viewing = todoAssigneesReduce(loaded, TodoAssigneesIntent.OpenProfile("Иван"))
        assertEquals(ivan, viewing.viewedAssignee)
        assertNull(viewing.form)

        val editing = todoAssigneesReduce(viewing, TodoAssigneesIntent.OpenForm("Иван"))
        assertEquals(ivan.profile, editing.form?.profile)
        val edited = todoAssigneesReduce(editing, TodoAssigneesIntent.ProfileChanged(TodoAssigneeProfile()))
        val dismissed = todoAssigneesReduce(edited, TodoAssigneesIntent.Dismiss)
        assertNull(dismissed.form)
        assertEquals(ivan, dismissed.viewedAssignee)
    }
}
