package com.zaroslikov.data.room.database.migration

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Migration_16_17Test {

    @Test
    fun `миграция совпадает с эталоном построчно`() {
        assertEquals(
            listOf(
                "ALTER TABLE recycling_table RENAME TO recycling_table_old",
                "CREATE TABLE IF NOT EXISTS recycling_table (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "day INTEGER NOT NULL, " +
                        "month INTEGER NOT NULL, " +
                        "year INTEGER NOT NULL, " +
                        "note TEXT NOT NULL, " +
                        "idPT INTEGER NOT NULL, " +
                        "FOREIGN KEY(idPT) REFERENCES project_table(id) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE)",
                "INSERT INTO recycling_table (_id, day, month, year, note, idPT) " +
                        "SELECT _id, day, month, year, note, idPT FROM recycling_table_old",
                "CREATE TABLE IF NOT EXISTS recycling_product_table (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "recycling_id INTEGER NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "count REAL NOT NULL, " +
                        "count_suffix INTEGER NOT NULL, " +
                        "category TEXT, " +
                        "ready_day INTEGER, " +
                        "ready_month INTEGER, " +
                        "ready_year INTEGER, " +
                        "is_completed INTEGER NOT NULL, " +
                        "add_id INTEGER, " +
                        "idPT INTEGER NOT NULL, " +
                        "FOREIGN KEY(recycling_id) REFERENCES recycling_table(_id) " +
                        "ON UPDATE NO ACTION ON DELETE CASCADE)",
                "INSERT INTO recycling_product_table (recycling_id, title, count, count_suffix, category, " +
                        "ready_day, ready_month, ready_year, is_completed, add_id, idPT) " +
                        "SELECT _id, title, count, count_suffix, category, " +
                        "ready_day, ready_month, ready_year, is_completed, add_id, idPT FROM recycling_table_old",
                "DROP TABLE recycling_table_old",
                "CREATE INDEX IF NOT EXISTS index_recycling_table_idPT ON recycling_table (idPT)",
                "CREATE INDEX IF NOT EXISTS index_recycling_product_table_recycling_id " +
                        "ON recycling_product_table (recycling_id)",
                "CREATE INDEX IF NOT EXISTS index_recycling_product_table_idPT " +
                        "ON recycling_product_table (idPT)"
            ),
            MIGRATION_16_17_SQL
        )
    }

    @Test
    fun `старая таблица удаляется только после переноса продукции`() {
        val productCopy = MIGRATION_16_17_SQL.indexOfFirst { it.startsWith("INSERT INTO recycling_product_table") }
        val productTable = MIGRATION_16_17_SQL.indexOfFirst { it.startsWith("CREATE TABLE IF NOT EXISTS recycling_product_table") }
        val newRecycling = MIGRATION_16_17_SQL.indexOfFirst { it.startsWith("CREATE TABLE IF NOT EXISTS recycling_table") }
        val drop = MIGRATION_16_17_SQL.indexOf("DROP TABLE recycling_table_old")
        // FK товаров должен ссылаться на уже новую recycling_table, иначе DROP удалит их каскадом
        assertTrue(newRecycling < productTable)
        assertTrue(productCopy in 0 until drop)
    }
}
