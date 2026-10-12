package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Сборные группы.
 * - animal_herd_table — ровно то, что Room создал бы из AnimalHerdTable, с индексом
 *   index_animal_herd_table_idPT.
 * - animal_table.herd_id — ссылка животного или обезличенной группы на сборную группу.
 *   Без DEFAULT и NOT NULL — как Room генерирует для `Long?` (FK ON DELETE SET NULL,
 *   индекс index_animal_table_herd_id).
 * В версиях до 7 сборных групп не было — существующие данные не меняются.
 */
internal val MIGRATION_7_8_SQL = listOf(
    "CREATE TABLE IF NOT EXISTS animal_herd_table (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "name TEXT NOT NULL, " +
            "note TEXT NOT NULL, " +
            "image_path TEXT, " +
            "icon INTEGER, " +
            "idPT INTEGER NOT NULL, " +
            "FOREIGN KEY(idPT) REFERENCES project_table(id) ON UPDATE NO ACTION ON DELETE CASCADE)",
    "CREATE INDEX IF NOT EXISTS index_animal_herd_table_idPT ON animal_herd_table (idPT)",
    "ALTER TABLE animal_table ADD COLUMN herd_id INTEGER REFERENCES animal_herd_table(id) ON UPDATE NO ACTION ON DELETE SET NULL",
    "CREATE INDEX IF NOT EXISTS index_animal_table_herd_id ON animal_table (herd_id)"
)

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_7_8_SQL.forEach(db::execSQL)
}
