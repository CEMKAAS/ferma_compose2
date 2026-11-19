package com.zaroslikov.todo.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Подсписки и вид «столбцы»: новая `todo_section_table`, у списков — `view_mode`, у задач —
 * `section_id` с внешним ключом на подсписок (`ON DELETE SET NULL`).
 *
 * `todo_task_table` не пересоздаётся: миграция идёт в транзакции, где `PRAGMA foreign_keys`
 * не выключить, и DROP старой таблицы каскадом снёс бы комментарии, связи и теги задач.
 * SQLite разрешает `ADD COLUMN ... REFERENCES` без значения по умолчанию (NULL), а
 * `PRAGMA foreign_key_list` для такой колонки совпадает с тем, что ждёт Room.
 * Таблица подсписков и индексы — ровно то, что Room создал бы из сущностей (см. `schemas/.../2.json`).
 */
internal val MIGRATION_1_2_SQL = listOf(
    "CREATE TABLE IF NOT EXISTS `todo_section_table` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`list_id` INTEGER NOT NULL, " +
            "`title` TEXT NOT NULL, " +
            "`position` INTEGER NOT NULL DEFAULT 0, " +
            "`is_collapsed` INTEGER NOT NULL DEFAULT 0, " +
            "FOREIGN KEY(`list_id`) REFERENCES `todo_list_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
    "CREATE INDEX IF NOT EXISTS `index_todo_section_table_list_id` ON `todo_section_table` (`list_id`)",
    "ALTER TABLE `todo_list_table` ADD COLUMN `view_mode` TEXT NOT NULL DEFAULT 'list'",
    "ALTER TABLE `todo_task_table` ADD COLUMN `section_id` INTEGER " +
            "REFERENCES `todo_section_table`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL",
    "CREATE INDEX IF NOT EXISTS `index_todo_task_table_section_id` ON `todo_task_table` (`section_id`)"
)

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_1_2_SQL.forEach(db::execSQL)
}
