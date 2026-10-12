package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * История поголовья сборной группы: animal_count_table.herd_id — группа, в составе которой
 * сделана запись. Без DEFAULT и NOT NULL — как Room генерирует для `Long?` (FK ON DELETE SET NULL,
 * индекс index_animal_count_table_herd_id). Существующие записи остаются вне групп.
 */
internal val MIGRATION_12_13_SQL = listOf(
    "ALTER TABLE animal_count_table ADD COLUMN herd_id INTEGER REFERENCES animal_herd_table(id) ON UPDATE NO ACTION ON DELETE SET NULL",
    "CREATE INDEX IF NOT EXISTS index_animal_count_table_herd_id ON animal_count_table (herd_id)"
)

val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_12_13_SQL.forEach(db::execSQL)
}
