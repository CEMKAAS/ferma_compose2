package com.zaroslikov.todo

import com.zaroslikov.todo.data.room.converter.TodoConverters
import com.zaroslikov.todo.domain.model.DomainTodoBackup
import com.zaroslikov.todo.domain.model.DomainTodoLink
import com.zaroslikov.todo.domain.model.DomainTodoStatus
import com.zaroslikov.todo.domain.model.DomainTodoTask
import com.zaroslikov.todo.domain.model.TodoDefaultStatus
import com.zaroslikov.todo.domain.model.TodoLinkType
import com.zaroslikov.todo.domain.model.TodoPriority
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoConvertersTest {

    private val converters = TodoConverters()

    @Test
    fun enums_roundTripThroughCode() {
        TodoPriority.entries.forEach {
            assertEquals(it, converters.toPriority(converters.fromPriority(it)))
        }
        TodoLinkType.entries.forEach {
            assertEquals(it, converters.toLinkType(converters.fromLinkType(it)))
        }
        TodoDefaultStatus.entries.forEach {
            assertEquals(it, converters.toDefaultStatus(converters.fromDefaultStatus(it)))
        }
        assertNull(converters.fromDefaultStatus(null))
        assertNull(converters.toDefaultStatus(null))
    }

    @Test
    fun codes_areStable() {
        // Коды лежат в БД и бэкапах — менять их нельзя
        assertEquals(listOf("none", "low", "medium", "high"), TodoPriority.entries.map { converters.fromPriority(it) })
        assertEquals(listOf("relates", "blocks"), TodoLinkType.entries.map { converters.fromLinkType(it) })
        assertEquals(
            listOf("planned", "in_progress", "done"),
            TodoDefaultStatus.entries.map { converters.fromDefaultStatus(it) }
        )
    }

    @Test
    fun unknownCodes_fallBackToSafeDefaults() {
        assertEquals(TodoPriority.NONE, converters.toPriority("urgent"))
        assertEquals(TodoLinkType.RELATES, converters.toLinkType("duplicates"))
        assertNull(converters.toDefaultStatus("archived"))
    }

    @Test
    fun defaultStatus_onlyDoneIsDone() {
        assertEquals(listOf(TodoDefaultStatus.DONE), TodoDefaultStatus.entries.filter { it.isDone })
        assertEquals(listOf(0, 1, 2), TodoDefaultStatus.entries.map { it.position })
    }

    @Test
    fun backup_serializesEnumsAsCodes() {
        val backup = DomainTodoBackup(
            statuses = listOf(
                DomainTodoStatus(id = 1, idPT = 7, title = "В планах", defaultCode = TodoDefaultStatus.PLANNED),
                DomainTodoStatus(id = 2, idPT = 7, title = "Ждёт")
            ),
            tasks = listOf(DomainTodoTask(id = 3, listId = 1, title = "Полить", priority = TodoPriority.HIGH, statusId = 1)),
            links = listOf(DomainTodoLink(id = 1, taskId = 3, linkedTaskId = 4, type = TodoLinkType.BLOCKS))
        )
        val json = Json.encodeToString(DomainTodoBackup.serializer(), backup)
        assertTrue(json.contains("\"defaultCode\":\"planned\""))
        assertTrue(json.contains("\"priority\":\"high\""))
        assertTrue(json.contains("\"type\":\"blocks\""))
        assertEquals(backup, Json.decodeFromString(DomainTodoBackup.serializer(), json))
    }

    @Test
    fun backup_unknownEnumCodes_doNotAbortImport() {
        // Бэкап из новой версии с кодами, которых эта версия не знает
        val json = """{
            "statuses":[{"id":1,"idPT":7,"title":"Архив","defaultCode":"archived"},
                        {"id":2,"idPT":7,"title":"Своё","defaultCode":null}],
            "tasks":[{"id":3,"listId":1,"title":"Т","priority":"urgent","statusId":1}],
            "links":[{"id":1,"taskId":3,"linkedTaskId":4,"type":"duplicates"}]
        }"""
        val backup = Json.decodeFromString(DomainTodoBackup.serializer(), json)
        assertNull(backup.statuses[0].defaultCode)
        assertNull(backup.statuses[1].defaultCode)
        assertEquals(TodoPriority.NONE, backup.tasks.single().priority)
        assertEquals(TodoLinkType.RELATES, backup.links.single().type)
    }

    @Test
    fun backup_readsEmptyObject() {
        assertEquals(DomainTodoBackup(), Json.decodeFromString(DomainTodoBackup.serializer(), "{}"))
    }
}
