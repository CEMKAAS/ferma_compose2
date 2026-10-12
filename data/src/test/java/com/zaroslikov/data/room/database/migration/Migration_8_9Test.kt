package com.zaroslikov.data.room.database.migration

import com.zaroslikov.domain.models.enums.AnimalSpecies
import org.junit.Assert.assertEquals
import org.junit.Test

class Migration_8_9Test {

    @Test
    fun `колонка NOT NULL DEFAULT 0 — как у Room`() {
        assertEquals(
            "ALTER TABLE animal_table ADD COLUMN species INTEGER NOT NULL DEFAULT 0",
            MIGRATION_8_9_SQL
        )
        assertEquals(0, AnimalSpecies.UNKNOWN.code)
    }

    @Test
    fun `вид заполняется по существующим типам, свои типы остаются UNKNOWN`() {
        assertEquals(
            mapOf(
                "Курицы" to AnimalSpecies.CHICKEN.code,
                "бык" to AnimalSpecies.COW.code,
                "Несушка" to AnimalSpecies.CHICKEN.code,
            ),
            speciesCodesByType(listOf("Курицы", "бык", "Несушка", "Нутрия", ""))
        )
    }

    @Test
    fun `миграция с 8 на 9`() {
        assertEquals(8, MIGRATION_8_9.startVersion)
        assertEquals(9, MIGRATION_8_9.endVersion)
    }
}
