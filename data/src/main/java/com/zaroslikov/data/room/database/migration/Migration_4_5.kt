package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
    ALTER TABLE write_off_table
    ADD COLUMN product_origin INTEGER DEFAULT 0
    """.trimIndent()
        )

        db.execSQL(
            """
    ALTER TABLE sale_table
    ADD COLUMN product_origin INTEGER DEFAULT 0
    """.trimIndent()
        )
    }
}