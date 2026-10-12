package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Пол в операциях обезличенной группы: animal_count_table.females_count / males_count — сколько
 * из количества записи самок и самцов. Как Room генерирует для `Int` с defaultValue = "0".
 * Существующие записи — 0 и 0 (пол не указан).
 */
internal val MIGRATION_14_15_SQL = listOf(
    "ALTER TABLE animal_count_table ADD COLUMN females_count INTEGER NOT NULL DEFAULT 0",
    "ALTER TABLE animal_count_table ADD COLUMN males_count INTEGER NOT NULL DEFAULT 0"
)

val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_14_15_SQL.forEach(db::execSQL)
}
