package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_13_14Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
                "ALTER TABLE animal_table ADD COLUMN females_count INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE animal_table ADD COLUMN males_count INTEGER NOT NULL DEFAULT 0"
            ),
            MIGRATION_13_14_SQL
        )
    }
}
