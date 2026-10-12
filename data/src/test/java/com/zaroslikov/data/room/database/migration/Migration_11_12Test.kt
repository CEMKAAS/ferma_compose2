package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_11_12Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
                "CREATE TABLE IF NOT EXISTS animal_pregnancy_note_table (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "pregnancy_id INTEGER NOT NULL, " +
                        "date TEXT NOT NULL, " +
                        "time TEXT, " +
                        "note TEXT NOT NULL, " +
                        "FOREIGN KEY(pregnancy_id) REFERENCES animal_pregnancy_table(id) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE)",
                "CREATE INDEX IF NOT EXISTS index_animal_pregnancy_note_table_pregnancy_id " +
                        "ON animal_pregnancy_note_table (pregnancy_id)"
            ),
            MIGRATION_11_12_SQL
        )
    }
}
