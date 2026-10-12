package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_12_13Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
                "ALTER TABLE animal_count_table ADD COLUMN herd_id INTEGER " +
                        "REFERENCES animal_herd_table(id) ON UPDATE NO ACTION ON DELETE SET NULL",
                "CREATE INDEX IF NOT EXISTS index_animal_count_table_herd_id ON animal_count_table (herd_id)"
            ),
            MIGRATION_12_13_SQL
        )
    }
}
