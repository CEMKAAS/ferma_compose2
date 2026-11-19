package com.zaroslikov.todo.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE todo_task_table ADD COLUMN assignees TEXT NOT NULL DEFAULT '[]'")
    }
}
