package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_15_16Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
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
                        "FOREIGN KEY(idPT) REFERENCES project_table(id) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE)",
                "CREATE INDEX IF NOT EXISTS index_recycling_table_idPT ON recycling_table (idPT)",
                "ALTER TABLE write_off_table ADD COLUMN recycling_id INTEGER",
                "CREATE INDEX IF NOT EXISTS index_write_off_table_recycling_id " +
                        "ON write_off_table (recycling_id)"
            ),
            MIGRATION_15_16_SQL
        )
    }
}
