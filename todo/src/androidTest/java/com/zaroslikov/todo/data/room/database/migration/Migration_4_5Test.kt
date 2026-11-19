package com.zaroslikov.todo.data.room.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class Migration_4_5Test {
    @get:Rule val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), TodoDatabase::class.java)

    @Test fun migrationKeepsTasksSubtasksAndHistoryWithEmptyAssignees() {
        val name = "todo-assignees-migration"
        helper.createDatabase(name, 4).use { db ->
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, created_at) VALUES (1, 7, 'Куры', '2026-11-14T10:00')")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title) VALUES (1, 7, 'В планах')")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, title, status_id, created_at, repeat_period) VALUES (1, 1, 'Корм', 1, '2026-11-14T09:00', 'day')")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, parent_id, title, status_id, created_at) VALUES (2, 1, 1, 'Склад', 1, '2026-11-14T09:00')")
            db.execSQL("INSERT INTO todo_comment_table (id, task_id, text, created_at) VALUES (1, 1, 'Мешок', '2026-11-14T09:00')")
        }
        helper.runMigrationsAndValidate(name, 5, true, MIGRATION_4_5).use { db ->
            db.query("SELECT title, parent_id, repeat_period, assignees FROM todo_task_table ORDER BY id").use {
                assertTrue(it.moveToFirst())
                assertEquals("Корм", it.getString(0))
                assertTrue(it.isNull(1))
                assertEquals("day", it.getString(2))
                assertEquals("[]", it.getString(3))
                assertTrue(it.moveToNext())
                assertEquals("Склад", it.getString(0))
                assertEquals(1L, it.getLong(1))
                assertEquals("[]", it.getString(3))
            }
            db.query("SELECT text FROM todo_comment_table WHERE task_id = 1").use {
                assertTrue(it.moveToFirst())
                assertEquals("Мешок", it.getString(0))
            }
        }
    }
}
