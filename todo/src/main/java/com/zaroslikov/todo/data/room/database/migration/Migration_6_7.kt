package com.zaroslikov.todo.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE todo_list_table ADD COLUMN is_last_task_destination INTEGER NOT NULL DEFAULT 0")
        // В прежней версии известен только текущий список последней сохранившейся задачи.
        db.execSQL(
            """
            UPDATE todo_list_table SET is_last_task_destination = 1
            WHERE id IN (
                SELECT task.list_id FROM todo_task_table task
                WHERE task.id IN (
                    SELECT MAX(t.id) FROM todo_task_table t
                    JOIN todo_list_table l ON l.id = t.list_id
                    GROUP BY l.idPT
                )
            )
            """.trimIndent()
        )
    }
}
