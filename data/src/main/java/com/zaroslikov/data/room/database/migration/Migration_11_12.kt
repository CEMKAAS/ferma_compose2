package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Заметки к беременности по дням: animal_pregnancy_note_table — ровно то, что Room создал бы
 * из AnimalPregnancyNoteTable (FK на animal_pregnancy_table, ON DELETE CASCADE).
 * Существующие данные не меняются.
 */
internal val MIGRATION_11_12_SQL = listOf(
    "CREATE TABLE IF NOT EXISTS animal_pregnancy_note_table (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "pregnancy_id INTEGER NOT NULL, " +
            "date TEXT NOT NULL, " +
            "time TEXT, " +
            "note TEXT NOT NULL, " +
            "FOREIGN KEY(pregnancy_id) REFERENCES animal_pregnancy_table(id) ON UPDATE NO ACTION ON DELETE CASCADE)",
    "CREATE INDEX IF NOT EXISTS index_animal_pregnancy_note_table_pregnancy_id ON animal_pregnancy_note_table (pregnancy_id)"
)

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_11_12_SQL.forEach(db::execSQL)
}
