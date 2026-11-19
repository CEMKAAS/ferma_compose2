package com.zaroslikov.todo

import com.zaroslikov.todo.data.room.converter.TodoConverters
import com.zaroslikov.todo.data.room.repository.sanitizedForImport
import com.zaroslikov.todo.domain.model.DomainTodoBackup
import com.zaroslikov.todo.domain.model.DomainTodoList
import com.zaroslikov.todo.domain.model.TodoListIcon
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Значки и закрепление списков: коды в БД и бэкапе, старые бэкапы и неизвестные коды. */
class TodoListIconTest {

    private val converters = TodoConverters()
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun codes_areStableAndUnique() {
        // Коды лежат в БД и бэкапах — менять их нельзя
        assertEquals(
            listOf(
                "list", "animals", "plants", "flowers", "machinery", "eggs",
                "shopping", "repair", "home", "water", "money", "calendar"
            ),
            TodoListIcon.entries.map { it.code }
        )
        assertEquals(TodoListIcon.LIST, TodoListIcon.DEFAULT)
    }

    @Test
    fun fromCode_roundTripsAndFallsBackToDefault() {
        TodoListIcon.entries.forEach {
            assertEquals(it, TodoListIcon.fromCode(it.code))
            assertEquals(it, converters.toListIcon(converters.fromListIcon(it)))
        }
        assertEquals(TodoListIcon.LIST, TodoListIcon.fromCode("tractor_2077"))
        assertEquals(TodoListIcon.LIST, TodoListIcon.fromCode(""))
        assertEquals(TodoListIcon.LIST, TodoListIcon.fromCode(null))
        assertEquals(TodoListIcon.LIST, converters.toListIcon("unknown"))
    }

    @Test
    fun backup_writesIconCodeAndPin_andReadsThemBack() {
        val backup = DomainTodoBackup(
            lists = listOf(
                DomainTodoList(id = 1, idPT = 7, title = "Куры", icon = TodoListIcon.EGGS, isPinned = true),
                DomainTodoList(id = 2, idPT = 7, title = "Огород")
            )
        )
        val text = Json.encodeToString(DomainTodoBackup.serializer(), backup)
        assertTrue(text.contains("\"icon\":\"eggs\""))
        assertTrue(text.contains("\"isPinned\":true"))
        assertEquals(backup, json.decodeFromString(DomainTodoBackup.serializer(), text))
    }

    @Test
    fun backup_oldVersionWithoutFields_getsDefaults() {
        // Бэкап до версии БД 3: ни значка, ни закрепления
        val text = """{"lists":[{"id":1,"idPT":7,"title":"Огород","colorIndex":2,"position":0,"createdAt":"2026-01-01T09:00"}]}"""
        val list = json.decodeFromString(DomainTodoBackup.serializer(), text).lists.single()
        assertEquals(TodoListIcon.LIST, list.icon)
        assertFalse(list.isPinned)
    }

    @Test
    fun backup_unknownIconCode_becomesDefault_andSurvivesSanitize() {
        val text = """{"lists":[{"id":1,"idPT":7,"title":"Пасека","icon":"bee","isPinned":true}]}"""
        val list = json.decodeFromString(DomainTodoBackup.serializer(), text)
            .sanitizedForImport(setOf(7)).lists.single()
        assertEquals(TodoListIcon.LIST, list.icon)
        assertTrue(list.isPinned)
    }
}
