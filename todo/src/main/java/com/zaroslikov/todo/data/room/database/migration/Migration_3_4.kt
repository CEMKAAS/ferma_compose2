package com.zaroslikov.todo.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE todo_task_table ADD COLUMN repeat_period TEXT NOT NULL DEFAULT 'none'")
        db.execSQL("ALTER TABLE todo_task_table ADD COLUMN repeat_end_date TEXT")
        db.execSQL("ALTER TABLE todo_task_table ADD COLUMN recurrence_generated INTEGER NOT NULL DEFAULT 0")
    }
}
