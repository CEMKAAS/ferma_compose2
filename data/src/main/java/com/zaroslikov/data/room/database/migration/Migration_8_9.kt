package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaroslikov.domain.models.enums.AnimalSpecies

/**
 * Вид животного: `animal_table.species` — код [AnimalSpecies], 0 = UNKNOWN.
 * DEFAULT 0 совпадает с `@ColumnInfo(defaultValue = "0")` в AnimalTable.
 */
internal const val MIGRATION_8_9_SQL =
    "ALTER TABLE animal_table ADD COLUMN species INTEGER NOT NULL DEFAULT 0"

/** Код вида для каждого распознанного типа; свои типы (UNKNOWN) не попадают — им хватает DEFAULT. */
internal fun speciesCodesByType(types: List<String>): Map<String, Int> =
    types.mapNotNull { type -> AnimalSpecies.fromType(type)?.let { type to it.code } }.toMap()

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(MIGRATION_8_9_SQL)
        // Заполняем вид по уже введённым типам: «Курицы», «Бык» → код вида
        val types = mutableListOf<String>()
        db.query("SELECT DISTINCT type FROM animal_table").use { cursor ->
            while (cursor.moveToNext()) cursor.getString(0)?.let(types::add)
        }
        speciesCodesByType(types).forEach { (type, code) ->
            db.execSQL("UPDATE animal_table SET species = ? WHERE type = ?", arrayOf<Any>(code, type))
        }
    }
}
