package com.zaroslikov.todo.ui.assignees

import androidx.compose.ui.text.AnnotatedString
import com.zaroslikov.todo.R
import com.zaroslikov.todo.domain.model.DomainTodoAssignee
import com.zaroslikov.todo.domain.model.TodoAssigneeProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoAssigneeContactInputTest {
    @Test fun russianPhoneRecognizesLocalAndTrunkNumbersAndInternationalPrefixStaysLiteral() {
        for (value in listOf("9123456789", "89123456789", "79123456789", "+7 (912) 345-67-89")) {
            assertEquals("+7 (912) 345-67-89", formatTodoAssigneePhone(sanitizeTodoAssigneePhone(value)))
        }
        assertEquals("+7 (812) 345-67-89", formatTodoAssigneePhone("8123456789"))
        assertEquals("+442079460018", formatTodoAssigneePhone(sanitizeTodoAssigneePhone("+44 20 7946 0018")))
        assertEquals("", formatTodoAssigneePhone(""))
        assertEquals("9123", formatTodoAssigneePhone("9123"))
        assertEquals("раб. 42", formatTodoAssigneePhone("раб. 42"))
    }

    @Test fun extraPhoneDigitsAreRejectedWithoutChangingExistingDigits() {
        val overflowingEdits = listOf(
            "9123456789" to "91230456789",
            "89123456789" to "891234567890",
            "+79123456789" to "+7 (912) 345-67-890",
            "+123456789012345" to "+1234567890123456"
        )
        for ((phone, draft) in overflowingEdits) {
            val initial = TodoAssigneeProfile(phone = phone)
            val editing = edit(initial.copy(phone = draft), initial)
            assertEquals(phone, editing.form?.profile?.phone)
            assertTrue(todoAssigneesReduce(editing, TodoAssigneesIntent.Save("Исполнитель")).isSaving)
        }

        val blank = TodoAssigneeProfile()
        val pasted = edit(blank.copy(phone = "+7 (912) 345-67-890"), blank)
        assertEquals("", pasted.form?.profile?.phone)
    }

    @Test fun oldOverlongPhoneStaysUnchangedOnOtherEditsAndCanBeShortened() {
        val legacy = TodoAssigneeProfile(phone = "+7912345678901")
        val otherEdit = edit(legacy.copy(description = "Обновлено"), legacy)
        assertEquals(legacy.phone, otherEdit.form?.profile?.phone)
        assertTrue(todoAssigneesReduce(otherEdit, TodoAssigneesIntent.Save("Исполнитель")).isSaving)

        val shortened = legacy.copy(phone = legacy.phone.dropLast(1))
        val editing = edit(shortened, legacy)
        assertEquals(shortened.phone, editing.form?.profile?.phone)
        assertFalse(todoAssigneesReduce(editing, TodoAssigneesIntent.Save("Исполнитель")).isSaving)

        val corrected = todoAssigneesReduce(editing, TodoAssigneesIntent.ProfileChanged(
            legacy.copy(phone = "+79123456789")
        ))
        assertEquals("+79123456789", corrected.form?.profile?.phone)
        assertTrue(todoAssigneesReduce(corrected, TodoAssigneesIntent.Save("Исполнитель")).isSaving)
    }

    @Test fun phoneCursorOffsetsRoundTripOnPrefixesAndSeparators() {
        for (raw in listOf("", "+", "+7", "+79123456789", "89123456789", "9123456789", "+442079460018", "доб. 55")) {
            val result = TodoAssigneePhoneTransformation.filter(AnnotatedString(raw))
            val offsets = result.offsetMapping
            assertEquals(formatTodoAssigneePhone(raw), result.text.text)
            assertEquals((0..raw.length).toList(),
                (0..raw.length).map { offsets.transformedToOriginal(offsets.originalToTransformed(it)) })
            val originals = (0..result.text.length).map(offsets::transformedToOriginal)
            assertTrue(originals.all { it in 0..raw.length })
            assertTrue(originals.zipWithNext().all { (left, right) -> left <= right })
        }

        val partial = TodoAssigneePhoneTransformation.filter(AnnotatedString("9123"))
        assertEquals("+7 (912) 3", partial.text.text)
        assertEquals(4, partial.offsetMapping.originalToTransformed(0))
        assertEquals(3, partial.offsetMapping.transformedToOriginal(8))
    }

    @Test fun emailSanitizerPreservesAddressSymbolsAndDuplicateAtSignsFailValidation() {
        assertEquals("A.B+корм@example-mail.рф", sanitizeTodoAssigneeEmail(" A.B+корм @ example-mail.рф "))
        val typed = edit(TodoAssigneeProfile(email = "name @@ example.com"), TodoAssigneeProfile())
        assertEquals("name@@example.com", typed.form?.profile?.email)
        val invalid = todoAssigneesReduce(typed, TodoAssigneesIntent.Save("Исполнитель"))
        assertFalse(invalid.isSaving)
        assertEquals(R.string.todo_assignee_email_invalid, invalid.form?.emailError)
    }

    @Test fun incompleteChangedContactsPreventSavingAndOptionalEmptyContactsCanBeSaved() {
        val form = edit(
            TodoAssigneeProfile(phone = "9123", email = "person@example"),
            TodoAssigneeProfile()
        )
        val invalid = todoAssigneesReduce(form, TodoAssigneesIntent.Save("Исполнитель"))
        assertFalse(invalid.isSaving)
        assertEquals(R.string.todo_assignee_phone_invalid, invalid.form?.phoneError)
        assertEquals(R.string.todo_assignee_email_invalid, invalid.form?.emailError)

        val cleared = todoAssigneesReduce(invalid, TodoAssigneesIntent.ProfileChanged(TodoAssigneeProfile()))
        val saved = todoAssigneesReduce(cleared, TodoAssigneesIntent.Save("Исполнитель"))
        assertTrue(saved.isSaving)
        assertNull(saved.form?.phoneError)
        assertNull(saved.form?.emailError)
    }

    @Test fun changedContactsAreSanitizedAndUneditedLegacyContactsAreNotRewritten() {
        val legacy = TodoAssigneeProfile(phone = "доб. 55", email = "old internal address")
        val original = edit(legacy.copy(description = "Обновлено"), legacy)
        assertEquals(legacy.phone, original.form?.profile?.phone)
        assertEquals(legacy.email, original.form?.profile?.email)
        assertTrue(todoAssigneesReduce(original, TodoAssigneesIntent.Save("Исполнитель")).isSaving)

        val changed = edit(legacy.copy(phone = "+44 20 7946 0018", email = " name+tag @example.com "), legacy)
        assertEquals("+442079460018", changed.form?.profile?.phone)
        assertEquals("name+tag@example.com", changed.form?.profile?.email)
        assertTrue(todoAssigneesReduce(changed, TodoAssigneesIntent.Save("Исполнитель")).isSaving)
    }

    private fun edit(draft: TodoAssigneeProfile, initial: TodoAssigneeProfile): TodoAssigneesState {
        val loaded = TodoAssigneesState(assignees = listOf(DomainTodoAssignee(7, "Иван", initial)))
        val opened = todoAssigneesReduce(loaded, TodoAssigneesIntent.OpenForm("Иван"))
        return todoAssigneesReduce(opened, TodoAssigneesIntent.ProfileChanged(draft))
    }
}
