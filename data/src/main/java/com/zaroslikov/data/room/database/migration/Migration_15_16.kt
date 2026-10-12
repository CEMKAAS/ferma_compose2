package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Переработка: recycling_table — ровно то, что Room создал бы из RecyclingTable
 * (FK на project_table, ON DELETE CASCADE), и write_off_table.recycling_id — ссылка строки
 * списания сырья на переработку (без FK, с индексом). Существующие данные не меняются.
 */
internal val MIGRATION_15_16_SQL = listOf(
    "CREATE TABLE IF NOT EXISTS recycling_table (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "title TEXT NOT NULL, " +
            "count REAL NOT NULL, " +
            "count_suffix INTEGER NOT NULL, " +
            "category TEXT, " +
            "day INTEGER NOT NULL, " +
            "month INTEGER NOT NULL, " +
            "year INTEGER NOT NULL, " +
            "ready_day INTEGER, " +
            "ready_month INTEGER, " +
            "ready_year INTEGER, " +
            "is_completed INTEGER NOT NULL, " +
            "add_id INTEGER, " +
            "note TEXT NOT NULL, " +
            "idPT INTEGER NOT NULL, " +
            "FOREIGN KEY(idPT) REFERENCES project_table(id) ON UPDATE NO ACTION ON DELETE CASCADE)",
    "CREATE INDEX IF NOT EXISTS index_recycling_table_idPT ON recycling_table (idPT)",
    "ALTER TABLE write_off_table ADD COLUMN recycling_id INTEGER",
    "CREATE INDEX IF NOT EXISTS index_write_off_table_recycling_id ON write_off_table (recycling_id)"
)

val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_15_16_SQL.forEach(db::execSQL)
}
