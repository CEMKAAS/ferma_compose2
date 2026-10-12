package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_14_15Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
                "ALTER TABLE animal_count_table ADD COLUMN females_count INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE animal_count_table ADD COLUMN males_count INTEGER NOT NULL DEFAULT 0"
            ),
            MIGRATION_14_15_SQL
        )
    }
}
