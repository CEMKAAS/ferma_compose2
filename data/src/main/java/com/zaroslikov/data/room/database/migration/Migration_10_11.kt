package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Порода животного: необязательная колонка animal_table.breed.
 * Без DEFAULT и NOT NULL — как Room генерирует для `String?` (прецедент — MIGRATION_9_10).
 * Существующие данные не меняются: у всех животных порода NULL.
 */
internal val MIGRATION_10_11_SQL = listOf(
    "ALTER TABLE animal_table ADD COLUMN breed TEXT"
)

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_10_11_SQL.forEach(db::execSQL)
}
