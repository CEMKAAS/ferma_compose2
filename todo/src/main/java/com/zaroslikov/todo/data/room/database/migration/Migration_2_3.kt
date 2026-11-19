package com.zaroslikov.todo.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Значки и закрепление списков: у `todo_list_table` — `icon` (стабильный код `TodoListIcon`,
 * по умолчанию 'list') и `is_pinned`. Только ADD COLUMN — таблица не пересоздаётся, задачи
 * списков (FK CASCADE) не трогаются. Колонки — ровно то, что ждёт Room (`schemas/.../3.json`).
 */
internal val MIGRATION_2_3_SQL = listOf(
    "ALTER TABLE `todo_list_table` ADD COLUMN `icon` TEXT NOT NULL DEFAULT 'list'",
    "ALTER TABLE `todo_list_table` ADD COLUMN `is_pinned` INTEGER NOT NULL DEFAULT 0"
)

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_2_3_SQL.forEach(db::execSQL)
}
