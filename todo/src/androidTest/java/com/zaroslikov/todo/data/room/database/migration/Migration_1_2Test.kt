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
 * Миграция 1→2 на настоящей SQLite: данные версии 1 (список, задачи с подзадачами, теги,
 * связи, комментарии) переживают миграцию, схема совпадает с тем, что ждёт Room
 * (`runMigrationsAndValidate`), а новые внешние ключи работают.
 */
@RunWith(AndroidJUnit4::class)
class Migration_1_2Test {

    private val dbName = "todo-migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TodoDatabase::class.java
    )

    @Test
    fun migrate1To2_keepsDataAndValidatesSchema() {
        helper.createDatabase(dbName, 1).use { db ->
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, color_index, position, created_at) VALUES (1, 7, 'Огород', 2, 0, '2026-01-01T09:00')")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title, position, default_code, is_done) VALUES (1, 7, '', 0, 'planned', 0)")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title, position, default_code, is_done) VALUES (3, 7, '', 2, 'done', 1)")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, parent_id, title, note, priority, status_id, due_date, position, created_at) VALUES (10, 1, NULL, 'Полить', 'утром', 'high', 1, '2026-02-01', 0, '2026-01-01T09:00')")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, parent_id, title, status_id, position, created_at, completed_at) VALUES (11, 1, 10, 'Огурцы', 3, 0, '2026-01-01T09:00', '2026-01-02T10:00')")
            db.execSQL("INSERT INTO todo_task_table (id, list_id, parent_id, title, status_id, position, created_at) VALUES (12, 1, NULL, 'Прополоть', 1, 1, '2026-01-01T09:00')")
            db.execSQL("INSERT INTO todo_tag_table (id, idPT, title, color_index) VALUES (5, 7, 'Срочно', 1)")
            db.execSQL("INSERT INTO todo_task_tag_table (task_id, tag_id) VALUES (10, 5)")
            db.execSQL("INSERT INTO todo_task_link_table (id, task_id, linked_task_id, type) VALUES (1, 10, 12, 'blocks')")
            db.execSQL("INSERT INTO todo_comment_table (id, task_id, text, created_at) VALUES (1, 10, 'Шланг в сарае', '2026-01-01T09:30')")
        }

        val db = helper.runMigrationsAndValidate(dbName, 2, true, MIGRATION_1_2)

        // Всё на месте: список с видом по умолчанию, задачи без подсписка, связанные строки
        db.query("SELECT title, color_index, view_mode FROM todo_list_table WHERE id = 1").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("Огород", c.getString(0))
            assertEquals(2, c.getInt(1))
            assertEquals("list", c.getString(2))
        }
        db.query("SELECT id, parent_id, title, note, priority, due_date, completed_at, section_id FROM todo_task_table ORDER BY id").use { c ->
            assertEquals(3, c.count)
            c.moveToNext()
            assertEquals(10L, c.getLong(0))
            assertTrue(c.isNull(1))
            assertEquals("Полить", c.getString(2))
            assertEquals("утром", c.getString(3))
            assertEquals("high", c.getString(4))
            assertEquals("2026-02-01", c.getString(5))
            assertTrue(c.isNull(7))
            c.moveToNext()
            assertEquals(11L, c.getLong(0))
            assertEquals(10L, c.getLong(1))
            assertEquals("2026-01-02T10:00", c.getString(6))
            assertTrue(c.isNull(7))
            c.moveToNext()
            assertEquals(12L, c.getLong(0))
            assertTrue(c.isNull(7))
        }
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_task_tag_table WHERE task_id = 10 AND tag_id = 5"))
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_task_link_table WHERE task_id = 10 AND linked_task_id = 12 AND type = 'blocks'"))
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_comment_table WHERE task_id = 10 AND text = 'Шланг в сарае'"))
        assertEquals(2, db.count("SELECT COUNT(*) FROM todo_status_table"))
        assertEquals(0, db.count("SELECT COUNT(*) FROM todo_section_table"))

        // Новые внешние ключи: подсписок удаляется — задача остаётся без подсписка;
        // список удаляется — подсписки уходят каскадом
        db.execSQL("PRAGMA foreign_keys = ON")
        db.execSQL("INSERT INTO todo_section_table (id, list_id, title, position) VALUES (1, 1, 'Теплица', 0)")
        db.execSQL("INSERT INTO todo_section_table (id, list_id, title, position) VALUES (2, 1, 'Грядки', 1)")
        db.execSQL("UPDATE todo_task_table SET section_id = 1 WHERE id = 10")
        db.execSQL("DELETE FROM todo_section_table WHERE id = 1")
        db.query("SELECT section_id FROM todo_task_table WHERE id = 10").use { c ->
            assertTrue(c.moveToFirst())
            assertTrue(c.isNull(0))
        }
        assertEquals(1, db.count("SELECT COUNT(*) FROM todo_comment_table"))
        db.execSQL("DELETE FROM todo_list_table WHERE id = 1")
        assertEquals(0, db.count("SELECT COUNT(*) FROM todo_section_table"))
        assertEquals(0, db.count("SELECT COUNT(*) FROM todo_task_table"))
        db.close()
    }

    private fun SupportSQLiteDatabase.count(sql: String): Int =
        query(sql).use { c: Cursor -> if (c.moveToFirst()) c.getInt(0) else -1 }
}
