package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Пол в обезличенной группе: animal_table.females_count / males_count — сколько в ней самок
 * и самцов; остальное поголовье — пол не известен. Как Room генерирует для `Int` с
 * defaultValue = "0". Существующие группы — 0 и 0 (весь пол не известен).
 */
internal val MIGRATION_13_14_SQL = listOf(
    "ALTER TABLE animal_table ADD COLUMN females_count INTEGER NOT NULL DEFAULT 0",
    "ALTER TABLE animal_table ADD COLUMN males_count INTEGER NOT NULL DEFAULT 0"
)

val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_13_14_SQL.forEach(db::execSQL)
}
