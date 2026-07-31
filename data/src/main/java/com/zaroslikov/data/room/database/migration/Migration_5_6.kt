package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS template_table(
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                template_type INTEGER NOT NULL,
                name_template TEXT NOT NULL,
                title TEXT,
                count REAL,
                count_suffix INTEGER,
                price REAL,
                price_all REAL,
                price_suffix INTEGER,
                category TEXT,
                is_date INTEGER NOT NULL,
                animal_id INTEGER,
                animal_name TEXT,
                buyer TEXT,
                note TEXT,
                write_off_status INTEGER,
                product_origin INTEGER,
                is_pinned INTEGER NOT NULL,
                is_multi_project_template INTEGER NOT NULL,
                idPT INTEGER NOT NULL
            )
        """.trimIndent()
        )

        db.execSQL("CREATE INDEX IF NOT EXISTS index_template_table_idPT ON template_table(idPT)")

        val deviceId = UUID.randomUUID().toString()
        db.execSQL(
            """
            ALTER TABLE app_settings_table
            ADD COLUMN device_id INTEGER DEFAULT '$deviceId'
    """.trimIndent()
        )

    }

}