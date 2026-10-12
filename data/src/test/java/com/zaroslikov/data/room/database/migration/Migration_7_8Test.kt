package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Migration_7_8Test {

    @Test
    fun `таблица сборных групп создаётся раньше ссылки на неё`() {
        val create = MIGRATION_7_8_SQL.indexOfFirst { it.startsWith("CREATE TABLE IF NOT EXISTS animal_herd_table") }
        val alter = MIGRATION_7_8_SQL.indexOfFirst { it.startsWith("ALTER TABLE animal_table ADD COLUMN herd_id") }
        assertTrue(create >= 0 && alter > create)
    }

    @Test
    fun `herd_id с FK SET NULL на сборную группу и индекс как у Room`() {
        assertTrue(
            "ALTER TABLE animal_table ADD COLUMN herd_id INTEGER REFERENCES animal_herd_table(id) ON UPDATE NO ACTION ON DELETE SET NULL"
                    in MIGRATION_7_8_SQL
        )
        assertTrue("CREATE INDEX IF NOT EXISTS index_animal_table_herd_id ON animal_table (herd_id)" in MIGRATION_7_8_SQL)
        assertTrue("CREATE INDEX IF NOT EXISTS index_animal_herd_table_idPT ON animal_herd_table (idPT)" in MIGRATION_7_8_SQL)
    }

    @Test
    fun `сборная группа удаляется вместе с проектом`() {
        val create = MIGRATION_7_8_SQL.first { it.startsWith("CREATE TABLE IF NOT EXISTS animal_herd_table") }
        assertTrue(create.contains("FOREIGN KEY(idPT) REFERENCES project_table(id) ON UPDATE NO ACTION ON DELETE CASCADE"))
    }

    @Test
    fun `колонка herd_id без DEFAULT и NOT NULL — иначе схема не совпадёт с Room`() {
        val alter = MIGRATION_7_8_SQL.first { it.startsWith("ALTER TABLE animal_table") }
        assertFalse(alter.contains("DEFAULT", ignoreCase = true))
        assertFalse(alter.contains("NOT NULL", ignoreCase = true))
    }

    @Test
    fun `старой связи group_id больше нет`() =
        assertFalse(MIGRATION_7_8_SQL.any { it.contains("group_id") })

    @Test
    fun `миграция с 7 на 8`() {
        assertEquals(7, MIGRATION_7_8.startVersion)
        assertEquals(8, MIGRATION_7_8.endVersion)
    }
}
