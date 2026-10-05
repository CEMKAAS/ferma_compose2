package com.zaroslikov.data.room.database.migration

import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.LegacyAppIcons
import org.junit.Assert.assertTrue
import org.junit.Test

class Migration_6_7Test {

    /** Иконки хозяйства и инкубатора из сборки 3.1.0 — то, что лежит в базе у большинства. */
    private val legacyIds = LegacyAppIcons.tableFor(listOf(0x7f08012c, 0x7f0801e3))

    @Test
    fun `в запросе есть перевод всех значений выбранной таблицы`() {
        val sql = iconMigrationSql("project_table", "currentIcon", legacyIds)

        legacyIds.forEach { (resourceId, icon) ->
            assertTrue(
                "нет перевода ресурса $resourceId в ${icon.name}",
                sql.contains("WHEN $resourceId THEN ${icon.code}")
            )
        }
    }

    @Test
    fun `запрос переводит иконку инкубатора, на которой падала 3_1_1`() {
        val sql = iconMigrationSql("project_table", "currentIcon", legacyIds)

        assertTrue(sql.contains("WHEN ${0x7f0801e3} THEN ${AppIcon.EGG.code}"))
    }

    @Test
    fun `запрос обновляет нужную колонку и обнуляет неопознанное`() {
        val sql = iconMigrationSql("animal_table", "icon", legacyIds)

        assertTrue(sql.startsWith("UPDATE animal_table SET icon = CASE icon"))
        assertTrue(sql.contains("ELSE NULL"))
        assertTrue(sql.trimEnd().endsWith("WHERE icon IS NOT NULL"))
    }
}