package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Переработка: несколько товаров на партию. Колонки продукции переезжают из recycling_table
 * в новую recycling_product_table (по строке на каждую прежнюю переработку, recycling_id = _id),
 * recycling_table пересобирается без них. Старая таблица сначала переименовывается, чтобы
 * её DROP не задел каскадом товары: их FK ссылается уже на новую recycling_table.
 * Обе таблицы — ровно то, что Room создал бы из RecyclingTable / RecyclingProductTable.
 */
internal val MIGRATION_16_17_SQL = listOf(
    "ALTER TABLE recycling_table RENAME TO recycling_table_old",
    "CREATE TABLE IF NOT EXISTS recycling_table (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "day INTEGER NOT NULL, " +
            "month INTEGER NOT NULL, " +
            "year INTEGER NOT NULL, " +
            "note TEXT NOT NULL, " +
            "idPT INTEGER NOT NULL, " +
            "FOREIGN KEY(idPT) REFERENCES project_table(id) ON UPDATE NO ACTION ON DELETE CASCADE)",
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
            "FOREIGN KEY(recycling_id) REFERENCES recycling_table(_id) ON UPDATE NO ACTION ON DELETE CASCADE)",
    "INSERT INTO recycling_product_table (recycling_id, title, count, count_suffix, category, " +
            "ready_day, ready_month, ready_year, is_completed, add_id, idPT) " +
            "SELECT _id, title, count, count_suffix, category, " +
            "ready_day, ready_month, ready_year, is_completed, add_id, idPT FROM recycling_table_old",
    "DROP TABLE recycling_table_old",
    "CREATE INDEX IF NOT EXISTS index_recycling_table_idPT ON recycling_table (idPT)",
    "CREATE INDEX IF NOT EXISTS index_recycling_product_table_recycling_id " +
            "ON recycling_product_table (recycling_id)",
    "CREATE INDEX IF NOT EXISTS index_recycling_product_table_idPT ON recycling_product_table (idPT)"
)

val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_16_17_SQL.forEach(db::execSQL)
}
