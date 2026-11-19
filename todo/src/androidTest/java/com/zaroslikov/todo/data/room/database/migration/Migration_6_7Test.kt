package com.zaroslikov.todo.data.room.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import com.zaroslikov.todo.data.room.database.TodoDatabase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class Migration_6_7Test {
    @get:Rule val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), TodoDatabase::class.java)

    @Test fun migrationMarksLastTaskListInEachProject() {
        val name = "todo-last-task-destination-migration"
        helper.createDatabase(name, 6).use { db ->
            db.execSQL(
                """
                INSERT INTO todo_list_table (id, idPT, title, created_at) VALUES
                    (1, 7, 'Куры', '2026-11-14T10:00'), (2, 7, 'Огород', '2026-11-14T10:00'),
                    (3, 8, 'Грядки', '2026-11-14T10:00'), (4, 9, 'Пустой', '2026-11-14T10:00')
                """.trimIndent()
            )
            db.execSQL(
                "INSERT INTO todo_status_table (id, idPT, title) VALUES (1, 7, 'В планах'), (2, 8, 'В планах')"
            )
            db.execSQL(
                """
                INSERT INTO todo_task_table (id, list_id, title, status_id, created_at) VALUES
                    (5, 1, 'Корм', 1, '2026-11-14T10:00'), (6, 2, 'Полив', 1, '2026-11-14T10:00'),
                    (7, 3, 'Рассада', 2, '2026-11-14T10:00')
                """.trimIndent()
            )
        }

        helper.runMigrationsAndValidate(name, 7, true, MIGRATION_6_7).use { db ->
            val lists = mutableMapOf<Long, Int>()
            db.query("SELECT id, is_last_task_destination FROM todo_list_table").use { cursor ->
                while (cursor.moveToNext()) lists[cursor.getLong(0)] = cursor.getInt(1)
            }
            assertEquals(mapOf(1L to 0, 2L to 1, 3L to 1, 4L to 0), lists)
        }
    }
}
