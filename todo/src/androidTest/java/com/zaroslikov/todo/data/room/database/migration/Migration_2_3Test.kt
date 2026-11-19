package com.zaroslikov.todo.data.room.database.migration

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Миграция 2→3 (значок и закрепление списков) на настоящей SQLite: списки, подсписки и задачи
 * версии 2 остаются на месте, у списков — значок 'list' и `is_pinned = 0`, схема совпадает
 * с тем, что ждёт Room. Цепочка 1→2→3 — тоже.
 */
@RunWith(AndroidJUnit4::class)
class Migration_2_3Test {

    private val dbName = "todo-migration-2-3-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TodoDatabase::class.java
    )

    @Test
    fun migrate2To3_keepsDataAndValidatesSchema() {
        helper.createDatabase(dbName, 2).use { db ->
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, color_index, position, created_at, view_mode) VALUES (1, 7, 'Огород', 2, 1, '2026-01-01T09:00', 'columns')")
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, color_index, position, created_at) VALUES (2, 7, 'Куры', 3, 0, '2026-01-02T09:00')")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title, position, default_code, is_done) VALUES (1, 7, '', 0, 'planned', 0)")
            db.execSQL("INSERT INTO todo_section_table (id, list_id, title, position, is_collapsed) VALUES (1, 1, 'Теплица', 0, 1)")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, parent_id, section_id, title, status_id, position, created_at) VALUES (10, 1, NULL, 1, 'Полить', 1, 0, '2026-01-01T09:00')")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, parent_id, title, status_id, position, created_at) VALUES (11, 1, 10, 'Огурцы', 1, 0, '2026-01-01T09:00')")
            db.execSQL("INSERT INTO todo_comment_table (id, task_id, text, created_at) VALUES (1, 10, 'Шланг в сарае', '2026-01-01T09:30')")
        }

        val db = helper.runMigrationsAndValidate(dbName, 3, true, MIGRATION_2_3)

        db.query("SELECT id, title, color_index, position, view_mode, icon, is_pinned FROM todo_list_table ORDER BY id").use { c ->
            assertEquals(2, c.count)
            c.moveToNext()
            assertEquals("Огород", c.getString(1))
            assertEquals(2, c.getInt(2))
            assertEquals(1, c.getInt(3))
            assertEquals("columns", c.getString(4))
            assertEquals("list", c.getString(5))
            assertEquals(0, c.getInt(6))
            c.moveToNext()
            assertEquals("Куры", c.getString(1))
            assertEquals(0, c.getInt(3))
            assertEquals("list", c.getString(5))
            assertEquals(0, c.getInt(6))
        }
        assertEquals(2, db.count("SELECT COUNT(*) FROM todo_task_table WHERE list_id = 1"))
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_task_table WHERE id = 10 AND section_id = 1"))
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_section_table WHERE is_collapsed = 1"))
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_comment_table"))

        // Новые колонки пишутся, каскад списка по-прежнему работает
        db.execSQL("UPDATE todo_list_table SET icon = 'eggs', is_pinned = 1 WHERE id = 2")
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_list_table WHERE icon = 'eggs' AND is_pinned = 1"))
        db.execSQL("PRAGMA foreign_keys = ON")
        db.execSQL("DELETE FROM todo_list_table WHERE id = 1")
        assertEquals(0, db.count("SELECT COUNT(*) FROM todo_task_table"))
        assertEquals(0, db.count("SELECT COUNT(*) FROM todo_section_table"))
        db.close()
    }

    @Test
    fun migrate1To3_chain() {
        helper.createDatabase(dbName, 1).use { db ->
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, color_index, position, created_at) VALUES (1, 7, 'Огород', 2, 0, '2026-01-01T09:00')")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title, position, default_code, is_done) VALUES (1, 7, '', 0, 'planned', 0)")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, parent_id, title, status_id, position, created_at) VALUES (10, 1, NULL, 'Полить', 1, 0, '2026-01-01T09:00')")
        }

        val db = helper.runMigrationsAndValidate(dbName, 3, true, MIGRATION_1_2, MIGRATION_2_3)

        db.query("SELECT title, view_mode, icon, is_pinned FROM todo_list_table WHERE id = 1").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("Огород", c.getString(0))
            assertEquals("list", c.getString(1))
            assertEquals("list", c.getString(2))
            assertEquals(0, c.getInt(3))
        }
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_task_table WHERE id = 10 AND section_id IS NULL"))
        db.close()
    }

    private fun SupportSQLiteDatabase.count(sql: String): Int =
        query(sql).use { c: Cursor -> if (c.moveToFirst()) c.getInt(0) else -1 }
}
