package com.zaroslikov.domain.models.enums

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Иконка уходит в БД и в бэкап, поэтому её значения обязаны быть стабильными.
 * Тесты закрепляют таблицы идентификаторов ресурсов выпущенных сборок: если они поедут,
 * пользователи снова потеряют свои иконки при обновлении.
 */
class AppIconTest {

    // Значения сняты с реальных сборок; egg = outline_egg_24, cow = icons_cow.
    private val egg300 = 0x7f0801df
    private val cow300 = 0x7f08012a
    private val egg310 = 0x7f0801e3
    private val cow310 = 0x7f08012c
    private val egg311 = 0x7f080170
    private val cow311 = 0x7f0800eb

    @Test
    fun `коды иконок уникальны`() {
        val codes = AppIcon.entries.map { it.code }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `код иконки читается обратно`() {
        AppIcon.entries.forEach { icon ->
            assertEquals(icon, AppIcon.fromStoredValue(icon.code))
        }
    }

    @Test
    fun `значения сборки 3_1_0 переводятся по её таблице`() {
        // Именно egg310 ронял стартовый экран в 3.1.1: такого ресурса там уже нет.
        val table = LegacyAppIcons.tableFor(listOf(cow310, egg310))

        assertEquals(AppIcon.ICONS_COW, table[cow310])
        assertEquals(AppIcon.EGG, table[egg310])
    }

    @Test
    fun `значения сборки 3_0_0 переводятся по её таблице`() {
        val table = LegacyAppIcons.tableFor(listOf(cow300, egg300))

        assertEquals(AppIcon.ICONS_COW, table[cow300])
        assertEquals(AppIcon.EGG, table[egg300])
    }

    @Test
    fun `значения сборки 3_1_1 переводятся по её таблице`() {
        val table = LegacyAppIcons.tableFor(listOf(cow311, egg311))

        assertEquals(AppIcon.ICONS_COW, table[cow311])
        assertEquals(AppIcon.EGG, table[egg311])
    }

    @Test
    fun `одно число в разных сборках означает разные иконки`() {
        // cow310 в сборке 3.0.0 — это icons_farming_2, а не icons_cow.
        assertEquals(AppIcon.ICONS_COW, LegacyAppIcons.tableFor(listOf(cow310, egg310))[cow310])
        assertEquals(
            AppIcon.ICONS_FARMING_2,
            LegacyAppIcons.tableFor(listOf(cow310, egg300))[cow310]
        )
    }

    @Test
    fun `спорное значение без подсказок не разбирается`() {
        // Само по себе cow310 может быть и icons_cow, и icons_farming_2 — нужен контекст.
        assertNull(AppIcon.fromStoredValue(cow310))
        assertNull(LegacyAppIcons.UNAMBIGUOUS[cow310])
    }

    @Test
    fun `бесспорные значения разбираются всегда`() {
        // 0x7f0800b2 — chiken во всех сборках, кроме 3.1.1, где его нет вовсе.
        assertEquals(AppIcon.CHICK, AppIcon.fromStoredValue(0x7f0800b2))
        assertEquals(AppIcon.EGG, AppIcon.fromStoredValue(egg310))
    }

    @Test
    fun `неизвестное значение не распознаётся`() {
        assertNull(AppIcon.fromStoredValue(0))
        assertNull(AppIcon.fromStoredValue(0x7f080000))
        assertNull(LegacyAppIcons.tableFor(listOf(egg310))[0x7f080000])
    }

    @Test
    fun `пустая база не мешает переводу`() {
        assertEquals(LegacyAppIcons.UNAMBIGUOUS, LegacyAppIcons.tableFor(emptyList()))
    }

    @Test
    fun `бэкап со старым идентификатором ресурса читается`() {
        val restored = Json.decodeFromString<IconHolder>("""{"icon":$egg310}""")
        assertEquals(AppIcon.EGG, restored.icon)
    }

    @Test
    fun `иконка сериализуется своим кодом`() {
        val json = Json.encodeToString(IconHolder.serializer(), IconHolder(AppIcon.EGG))
        assertEquals("""{"icon":${AppIcon.EGG.code}}""", json)
    }

    @Test
    fun `отсутствие иконки переживает сериализацию`() {
        val json = Json.encodeToString(IconHolder.serializer(), IconHolder(null))
        assertEquals("""{"icon":null}""", json)
        assertNull(Json.decodeFromString<IconHolder>(json).icon)
    }

    @Serializable
    private data class IconHolder(
        @Serializable(with = AppIconAsCodeSerializer::class)
        val icon: AppIcon?
    )
}