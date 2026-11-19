package com.zaroslikov.todo.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaroslikov.todo.data.room.converter.TodoConverters
import com.zaroslikov.todo.domain.model.normalizeTodoAssignees

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS todo_assignee_table (idPT INTEGER NOT NULL, name_key TEXT NOT NULL, name TEXT NOT NULL, PRIMARY KEY(idPT, name_key))")
        val converter = TodoConverters()
        db.query("SELECT l.idPT, t.assignees FROM todo_task_table t JOIN todo_list_table l ON l.id = t.list_id ORDER BY t.id").use { cursor ->
            while (cursor.moveToNext()) {
                val idPT = cursor.getLong(0)
                normalizeTodoAssignees(converter.toAssignees(cursor.getString(1))).forEach { name ->
                    db.execSQL("INSERT OR IGNORE INTO todo_assignee_table (idPT, name_key, name) VALUES (?, ?, ?)",
                        arrayOf<Any>(idPT, name.lowercase(), name))
                }
            }
        }
    }
}
