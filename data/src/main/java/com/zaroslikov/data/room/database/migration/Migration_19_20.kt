package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_19_20 = object : Migration(19, 20) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS recycling_interruption_table (
                _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                idPT INTEGER NOT NULL,
                recycling_id INTEGER,
                ended_current_cycle INTEGER NOT NULL,
                FOREIGN KEY (idPT) REFERENCES project_table(id) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY (recycling_id) REFERENCES recycling_table(_id) ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_recycling_interruption_table_idPT ON recycling_interruption_table (idPT)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_recycling_interruption_table_recycling_id ON recycling_interruption_table (recycling_id)")
    }
}
