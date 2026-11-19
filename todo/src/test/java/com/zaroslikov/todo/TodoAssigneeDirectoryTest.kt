package com.zaroslikov.todo

import com.zaroslikov.todo.data.room.repository.sanitizedForImport
import com.zaroslikov.todo.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class TodoAssigneeDirectoryTest {
    @Test fun oldBackupDerivesFirstNameAndNewBackupKeepsAnExplicitlyEmptyName() {
        val legacy = Json.decodeFromString<DomainTodoBackup>("""{"assignees":[{"idPT":7,"name":"Иван Иванов"}]}""")
        assertEquals("Иван Иванов", legacy.assignees.single().profile.firstName)

        val cleared = legacy.copy(assignees = listOf(
            DomainTodoAssignee(7, "Иван Иванов", TodoAssigneeProfile())
        ))
        assertEquals(cleared, Json.decodeFromString<DomainTodoBackup>(Json.encodeToString(cleared)))
    }

    @Test fun backupKeepsUnusedNamesAndProjectIdentity() {
        val backup = DomainTodoBackup(assignees = listOf(DomainTodoAssignee(7, "Иван"), DomainTodoAssignee(8, "Иван")))
        assertEquals(backup, Json.decodeFromString<DomainTodoBackup>(Json.encodeToString(backup)))
        assertEquals(emptyList<DomainTodoAssignee>(), Json.decodeFromString<DomainTodoBackup>("{}").assignees)
    }

    @Test fun importClearsInvalidBirthDateAndPreservesOtherProfileFields() {
        val profile = TodoAssigneeProfile(birthday = "+999999999-01-01", phone = "+7 800 555-01-01")
        val backup = DomainTodoBackup(assignees = listOf(DomainTodoAssignee(7, "Анна", profile)))

        val restoredProfile = backup.sanitizedForImport(setOf(7)).assignees.single().profile

        assertEquals(profile.copy(birthday = ""), restoredProfile)
    }

    @Test fun importFiltersProjectsAndSeedsMissingDirectoryFromTasks() {
        val backup = DomainTodoBackup(
            lists = listOf(DomainTodoList(1, 7, "Первый"), DomainTodoList(2, 8, "Второй")),
            tasks = listOf(DomainTodoTask(1, 1, title = "Корм", statusId = 1, assignees = listOf("Анна", "ИВАН")),
                DomainTodoTask(2, 2, title = "Полив", statusId = 1, assignees = listOf("Пётр"))),
            assignees = listOf(DomainTodoAssignee(7, " Иван "), DomainTodoAssignee(7, "иван"),
                DomainTodoAssignee(7, ""), DomainTodoAssignee(8, "Пётр"), DomainTodoAssignee(99, "Чужой"))
        )
        val clean = backup.sanitizedForImport(setOf(7, 8))
        assertEquals(listOf(DomainTodoAssignee(7, "Иван"), DomainTodoAssignee(8, "Пётр"),
            DomainTodoAssignee(7, "Анна")), clean.assignees)
    }
}
