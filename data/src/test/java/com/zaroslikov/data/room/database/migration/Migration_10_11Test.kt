package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_10_11Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf("ALTER TABLE animal_table ADD COLUMN breed TEXT"),
            MIGRATION_10_11_SQL
        )
    }
}
