package com.zaroslikov.data.room.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.LegacyAppIcons

/**
 * Переводит сохранённые иконки с идентификаторов ресурсов на стабильные коды [AppIcon].
 *
 * До этой версии в `project_table.currentIcon` и `animal_table.icon` лежал `R.drawable.*`,
 * который пересобирается вместе с приложением. После обновления зависимостей в 3.1.1 старые
 * значения указывали в пустоту, и `painterResource` падал с `Resources$NotFoundException`.
 *
 * Схема таблиц не меняется — колонки остаются INTEGER, меняется смысл значений.
 * Что не удалось опознать, обнуляется: экран покажет иконку по умолчанию,
 * пользователь сможет выбрать её заново.
 */
val MIGRATION_6_7 = object : Migration(6, 7) {

    private val columns = listOf(
        "project_table" to "currentIcon",
        "animal_table" to "icon",
    )

    override fun migrate(db: SupportSQLiteDatabase) {
        // Номера ресурсов разных сборок пересекаются, поэтому таблицу перевода выбираем
        // по всей базе сразу — так же, как это делает импорт бэкапа.
        val stored = columns.flatMap { (table, column) -> db.readIcons(table, column) }
        val legacyIds = LegacyAppIcons.tableFor(stored)

        columns.forEach { (table, column) ->
            db.execSQL(iconMigrationSql(table, column, legacyIds))
        }
    }

    private fun SupportSQLiteDatabase.readIcons(table: String, column: String): List<Int> =
        query("SELECT DISTINCT $column FROM $table WHERE $column IS NOT NULL").use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.getInt(0))
            }
        }
}

/**
 * До версии 7 в колонке мог лежать только идентификатор ресурса или 0 — признак
 * «выбрана своя картинка». Поэтому всё неопознанное обнуляется.
 */
internal fun iconMigrationSql(
    table: String,
    column: String,
    legacyIds: Map<Int, AppIcon>
): String {
    val cases = legacyIds.entries.joinToString(separator = " ") {
        "WHEN ${it.key} THEN ${it.value.code}"
    }
    return """
        UPDATE $table SET $column = CASE $column
            $cases
            ELSE NULL
        END
        WHERE $column IS NOT NULL
    """.trimIndent()
}