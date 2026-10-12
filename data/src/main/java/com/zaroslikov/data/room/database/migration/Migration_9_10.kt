package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Беременность и родословная.
 * - animal_table: mother_id / father_id (FK на animal_table, ON DELETE SET NULL) и снимки имён
 *   mother_name / father_name. Без DEFAULT и NOT NULL — как Room генерирует для `Long?`/`String?`
 *   (прецедент — MIGRATION_7_8).
 * - animal_pregnancy_table — ровно то, что Room создал бы из AnimalPregnancyTable, с индексами
 *   index_animal_pregnancy_table_*. Частичного уникального индекса «одна активная на самку» нет:
 *   Room сверяет индексы, правило проверяется в AnimalPregnancyDao.
 * Существующие данные не меняются.
 */
internal val MIGRATION_9_10_SQL = listOf(
    "ALTER TABLE animal_table ADD COLUMN mother_id INTEGER REFERENCES animal_table(id) ON UPDATE NO ACTION ON DELETE SET NULL",
    "ALTER TABLE animal_table ADD COLUMN mother_name TEXT",
    "ALTER TABLE animal_table ADD COLUMN father_id INTEGER REFERENCES animal_table(id) ON UPDATE NO ACTION ON DELETE SET NULL",
    "ALTER TABLE animal_table ADD COLUMN father_name TEXT",
    "CREATE INDEX IF NOT EXISTS index_animal_table_mother_id ON animal_table (mother_id)",
    "CREATE INDEX IF NOT EXISTS index_animal_table_father_id ON animal_table (father_id)",
    "CREATE TABLE IF NOT EXISTS animal_pregnancy_table (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "animal_id INTEGER NOT NULL, " +
            "species INTEGER NOT NULL, " +
            "sire_id INTEGER, " +
            "sire_name TEXT, " +
            "mating_date TEXT NOT NULL, " +
            "status INTEGER NOT NULL, " +
            "end_date TEXT, " +
            "interrupt_reason INTEGER, " +
            "born_female INTEGER NOT NULL, " +
            "born_male INTEGER NOT NULL, " +
            "stillborn INTEGER NOT NULL, " +
            "note TEXT NOT NULL, " +
            "idPT INTEGER NOT NULL, " +
            "FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON UPDATE NO ACTION ON DELETE CASCADE, " +
            "FOREIGN KEY(sire_id) REFERENCES animal_table(id) ON UPDATE NO ACTION ON DELETE SET NULL, " +
            "FOREIGN KEY(idPT) REFERENCES project_table(id) ON UPDATE NO ACTION ON DELETE CASCADE)",
    "CREATE INDEX IF NOT EXISTS index_animal_pregnancy_table_animal_id ON animal_pregnancy_table (animal_id)",
    "CREATE INDEX IF NOT EXISTS index_animal_pregnancy_table_sire_id ON animal_pregnancy_table (sire_id)",
    "CREATE INDEX IF NOT EXISTS index_animal_pregnancy_table_idPT ON animal_pregnancy_table (idPT)"
)

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_9_10_SQL.forEach(db::execSQL)
}
