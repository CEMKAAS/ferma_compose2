package com.zaroslikov.todo.data.room.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class Migration_3_4Test {
    @get:Rule val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), TodoDatabase::class.java)

    @Test fun migrationPreservesTaskHistoryAndAddsDisabledRepeat() {
        val name = "todo-repeat-migration"
        helper.createDatabase(name, 3).use { db ->
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, created_at) VALUES (1, 7, 'Куры', '2026-11-14T10:00')")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title) VALUES (1, 7, '')")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, title, status_id, due_date, due_time, created_at) VALUES (1, 1, 'Корм', 1, '2026-11-14', '10:00', '2026-11-14T09:00')")
            db.execSQL("INSERT INTO todo_comment_table (id, task_id, text, created_at) VALUES (1, 1, 'Мешок', '2026-11-14T09:00')")
        }
        helper.runMigrationsAndValidate(name, 4, true, MIGRATION_3_4).use { db ->
            db.query("SELECT title, due_date, due_time, repeat_period, repeat_end_date, recurrence_generated FROM todo_task_table WHERE id = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals("Корм", cursor.getString(0))
                assertEquals("2026-11-14", cursor.getString(1))
                assertEquals("10:00", cursor.getString(2))
                assertEquals("none", cursor.getString(3))
                assertTrue(cursor.isNull(4))
                assertEquals(0, cursor.getInt(5))
            }
            db.query("SELECT text FROM todo_comment_table WHERE task_id = 1").use {
                assertTrue(it.moveToFirst())
                assertEquals("Мешок", it.getString(0))
            }
        }
    }
}
