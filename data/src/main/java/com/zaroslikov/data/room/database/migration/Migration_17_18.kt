package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Переработка: время готовности товара (часы и минуты) к дате готовности и время начала партии
 * (для прогресса по времени). У старых строк остаётся null — это начало дня (00:00).
 */
internal val MIGRATION_17_18_SQL = listOf(
    "ALTER TABLE recycling_product_table ADD COLUMN ready_hour INTEGER",
    "ALTER TABLE recycling_product_table ADD COLUMN ready_minute INTEGER",
    "ALTER TABLE recycling_table ADD COLUMN start_hour INTEGER",
    "ALTER TABLE recycling_table ADD COLUMN start_minute INTEGER"
)

val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) = MIGRATION_17_18_SQL.forEach(db::execSQL)
}
