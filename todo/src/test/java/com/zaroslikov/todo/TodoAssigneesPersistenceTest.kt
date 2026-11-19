package com.zaroslikov.todo

import com.zaroslikov.todo.data.room.converter.TodoConverters
import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.mapper.toTable
import com.zaroslikov.todo.data.room.repository.sanitizedForImport
import com.zaroslikov.todo.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class TodoAssigneesPersistenceTest {
    @Test fun namesAreTrimmedAndDeduplicatedIgnoringCase() {
        assertEquals(listOf("Иван", "Анна"), normalizeTodoAssignees(listOf(" Иван ", "", "иван", "Анна", "  ", "АННА")))
    }

    @Test fun mapperConverterAndBackupPreserveNamesWithPunctuation() {
        val task = DomainTodoTask(listId = 1, title = "Корм", statusId = 1,
            assignees = listOf("Иван, старший", "Анна \"А\"", "Пётр\nИванов"))
        assertEquals(task, task.toTable().toDomain())
        val converters = TodoConverters()
        assertEquals(task.assignees, converters.toAssignees(converters.fromAssignees(task.assignees)))
        assertEquals(task, Json.decodeFromString<DomainTodoTask>(Json.encodeToString(task)))
    }

    @Test fun oldBackupDefaultsToNoAssignees() {
        val task = Json.decodeFromString<DomainTodoTask>("""{"listId":1,"title":"Корм","statusId":1}""")
        assertEquals(emptyList<String>(), task.assignees)
    }

    @Test fun importedNamesAreNormalized() {
        val backup = DomainTodoBackup(
            lists = listOf(DomainTodoList(id = 1, idPT = 7, title = "Куры")),
            tasks = listOf(DomainTodoTask(id = 1, listId = 1, title = "Корм", statusId = 1,
                assignees = listOf(" Иван ", "ИВАН", "", "Анна")))
        )
        assertEquals(listOf("Иван", "Анна"), backup.sanitizedForImport(setOf(7)).tasks.single().assignees)
    }
}
