package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_17_18Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
                "ALTER TABLE recycling_product_table ADD COLUMN ready_hour INTEGER",
                "ALTER TABLE recycling_product_table ADD COLUMN ready_minute INTEGER",
                "ALTER TABLE recycling_table ADD COLUMN start_hour INTEGER",
                "ALTER TABLE recycling_table ADD COLUMN start_minute INTEGER"
            ),
            MIGRATION_17_18_SQL
        )
    }
}
