package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Migration_9_10Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
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
            ),
            MIGRATION_9_10_SQL
        )
    }

    @Test
    fun `новые колонки animal_table без DEFAULT и NOT NULL — иначе схема не совпадёт с Room`() {
        val alters = MIGRATION_9_10_SQL.filter { it.startsWith("ALTER TABLE") }
        assertEquals(4, alters.size)
        alters.forEach {
            assertFalse(it, it.contains("DEFAULT", ignoreCase = true))
            assertFalse(it, it.contains("NOT NULL", ignoreCase = true))
        }
    }

    @Test
    fun `FK-колонки родителей с ON DELETE SET NULL`() {
        listOf("mother_id", "father_id").forEach { col ->
            val alter = MIGRATION_9_10_SQL.single { it.contains("ADD COLUMN $col ") }
            assertTrue(alter.endsWith("REFERENCES animal_table(id) ON UPDATE NO ACTION ON DELETE SET NULL"))
        }
    }

    @Test
    fun `таблица беременностей содержит три FK с нужными действиями`() {
        val create = MIGRATION_9_10_SQL.single { it.startsWith("CREATE TABLE") }
        assertTrue(create.contains("FOREIGN KEY(animal_id) REFERENCES animal_table(id) ON UPDATE NO ACTION ON DELETE CASCADE"))
        assertTrue(create.contains("FOREIGN KEY(sire_id) REFERENCES animal_table(id) ON UPDATE NO ACTION ON DELETE SET NULL"))
        assertTrue(create.contains("FOREIGN KEY(idPT) REFERENCES project_table(id) ON UPDATE NO ACTION ON DELETE CASCADE"))
        assertFalse(create.contains("DEFAULT", ignoreCase = true))
    }

    @Test
    fun `индексы только index_таблица_колонка, без частичных`() {
        val indices = MIGRATION_9_10_SQL.filter { it.startsWith("CREATE INDEX") }
        assertEquals(5, indices.size)
        indices.forEach {
            assertFalse(it, it.contains("UNIQUE", ignoreCase = true))
            assertFalse(it, it.contains("WHERE", ignoreCase = true))
            val match = Regex("""index_(\w+?)_(mother_id|father_id|animal_id|sire_id|idPT) ON (\w+) \((\w+)\)""").find(it)
            checkNotNull(match) { it }
            assertEquals(match.groupValues[1], match.groupValues[3])
            assertEquals(match.groupValues[2], match.groupValues[4])
        }
    }

    @Test
    fun `миграция с 9 на 10`() {
        assertEquals(9, MIGRATION_9_10.startVersion)
        assertEquals(10, MIGRATION_9_10.endVersion)
    }
}
