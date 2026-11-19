package com.zaroslikov.todo

import com.zaroslikov.todo.data.room.mapper.toDomain
import com.zaroslikov.todo.data.room.mapper.toTable
import com.zaroslikov.todo.data.room.converter.TodoConverters
import com.zaroslikov.todo.domain.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import org.junit.Assert.*
import org.junit.Test

class TodoRepeatPersistenceTest {
    private val task = DomainTodoTask(listId = 1, title = "Корм", statusId = 1, dueDate = "2026-11-14",
        repeatPeriod = TodoRepeatPeriod.WEEK, repeatEndDate = "2026-12-01", recurrenceGenerated = true)

    @Test fun mapperAndBackupPreserveRepeatAndGenerationMarker() {
        assertEquals(task, task.toTable().toDomain())
        val json = Json.encodeToString(task)
        assertTrue(json.contains("\"week\""))
        assertEquals(task, Json.decodeFromString<DomainTodoTask>(json))
    }
    @Test fun oldBackupDefaultsToNoRepeat() {
        val old = Json.decodeFromString<DomainTodoTask>("""{"listId":1,"title":"Корм","statusId":1}""")
        assertEquals(TodoRepeatPeriod.NONE, old.repeatPeriod)
        assertNull(old.repeatEndDate)
        assertFalse(old.recurrenceGenerated)
    }
    @Test fun periodUsesStableCodesAndUnknownCodeDisablesRepeat() {
        val converters = TodoConverters()
        TodoRepeatPeriod.entries.forEach { assertEquals(it, converters.toRepeatPeriod(converters.fromRepeatPeriod(it))) }
        assertEquals(TodoRepeatPeriod.NONE, converters.toRepeatPeriod("unknown"))
    }
}
