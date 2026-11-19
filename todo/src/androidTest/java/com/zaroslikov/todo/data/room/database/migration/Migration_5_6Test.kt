package com.zaroslikov.todo.data.room.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class Migration_5_6Test {
    @get:Rule val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), TodoDatabase::class.java)

    @Test fun migrationSeedsProjectDirectoriesFromTasksAndSubtasks() {
        val name = "todo-assignee-directory-migration"
        helper.createDatabase(name, 5).use { db ->
            db.execSQL("INSERT INTO todo_list_table (id, idPT, title, created_at) VALUES (1, 7, 'Куры', '2026-11-14T10:00'), (2, 8, 'Огород', '2026-11-14T10:00')")
            db.execSQL("INSERT INTO todo_status_table (id, idPT, title) VALUES (1, 7, 'В планах'), (2, 8, 'В планах')")
            val insert = "INSERT INTO todo_task_table (id, list_id, parent_id, title, status_id, created_at, assignees) VALUES (?, ?, ?, ?, ?, ?, ?)"
            db.execSQL(insert, arrayOf<Any?>(1, 1, null, "Корм", 1, "2026-11-14T09:00", """[" Иван ", "Анна", "ИВАН", ""]"""))
            db.execSQL(insert, arrayOf<Any>(2, 1, 1, "Склад", 1, "2026-11-14T09:00", """["Пётр"]"""))
            db.execSQL(insert, arrayOf<Any?>(3, 2, null, "Полив", 2, "2026-11-14T09:00", """["Иван", "Мария"]"""))
        }
        helper.runMigrationsAndValidate(name, 6, true, MIGRATION_5_6).use { db ->
            val found = mutableMapOf<Long, MutableSet<String>>()
            db.query("SELECT idPT, name FROM todo_assignee_table").use { cursor ->
                while (cursor.moveToNext()) found.getOrPut(cursor.getLong(0)) { mutableSetOf() }.add(cursor.getString(1))
            }
            assertEquals(setOf("Иван", "Анна", "Пётр"), found[7L])
            assertEquals(setOf("Иван", "Мария"), found[8L])
            db.query("SELECT COUNT(*) FROM todo_task_table").use {
                assertTrue(it.moveToFirst())
                assertEquals(3, it.getInt(0))
            }
        }
    }
}
