package com.zaroslikov.fermacompose2.ui.start.settings

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.enums.TypeEgg
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Импорт на экране настроек читает файл дефолтным [Json] — без `ignoreUnknownKeys`,
 * поэтому любое расхождение полей ломает чтение целиком.
 *
 * Фикстуры в `src/test/resources/backup/` сняты кодом самих релизов (3.0.0, 3.1.0, 3.1.1):
 * тот же класс `BackupData`, тот же сериализатор, реальные идентификаторы ресурсов
 * в иконках. Так что тест проверяет ровно то, что произойдёт с файлом пользователя.
 */
class BackupImportCompatibilityTest {

    private fun load(version: String): String =
        checkNotNull(javaClass.getResourceAsStream("/backup/backup-$version.json")) {
            "нет фикстуры backup-$version.json"
        }.bufferedReader().readText()

    private fun import(version: String): BackupData = BackupParser.parse(load(version))

    @Test
    fun `бэкап 3_0_0 читается`() = assertCommonData(import("3.0.0"))

    @Test
    fun `бэкап 3_1_0 читается`() = assertCommonData(import("3.1.0"))

    @Test
    fun `бэкап 3_1_1 читается`() = assertCommonData(import("3.1.1"))

    @Test
    fun `иконки старых версий восстанавливаются`() {
        listOf("3.0.0", "3.1.0", "3.1.1").forEach { version ->
            val backup = import(version)

            assertEquals("$version: иконка хозяйства", AppIcon.ICONS_COW, backup.projectTable[0].currentIcon)
            assertEquals("$version: иконка инкубатора", AppIcon.EGG, backup.projectTable[1].currentIcon)
            assertEquals("$version: иконка животного", AppIcon.CHICKEN, backup.animalTable[0].currentIcon)
            // У животного с фотографией иконки не было и быть не должно.
            assertNull("$version: иконка животного с фото", backup.animalTable[1].currentIcon)
            assertEquals("$version: фото животного", "QkFTRTY0", backup.animalTable[1].imagePath)
        }
    }

    @Test
    fun `в файлах старых версий лежат именно идентификаторы ресурсов`() {
        // Страховка от «фикстура сгенерирована новым кодом». Заодно видно, почему
        // иконку нельзя перевести по одному значению: outline_egg_24 в каждой сборке свой.
        val eggResourceId = mapOf(
            "3.0.0" to 0x7f0801df,
            "3.1.0" to 0x7f0801e3,
            "3.1.1" to 0x7f080170,
        )
        eggResourceId.forEach { (version, id) ->
            assertTrue(
                "$version: в файле нет старого id иконки инкубатора",
                load(version).contains("\"currentIcon\":$id")
            )
        }
    }

    @Test
    fun `шаблоны появляются с 3_1_0`() {
        // template_table завели миграцией 5→6, в файле 3.0.0 такого поля нет.
        assertTrue(import("3.0.0").templateTable.isEmpty())

        listOf("3.1.0", "3.1.1").forEach { version ->
            val template = import(version).templateTable.single()
            assertEquals(TemplateType.SALE, template.templateType)
            assertEquals("Шаблон", template.nameTemplate)
            assertEquals(360.0, template.priceAll!!, 0.0)
            assertEquals(ProductOrigin.ADD, template.productOrigin)
            assertTrue(template.isPinned)
        }
    }

    @Test
    fun `device_id появляется с 3_1_0`() {
        assertEquals("", import("3.0.0").appSettingsTable.single().deviceId)

        listOf("3.1.0", "3.1.1").forEach { version ->
            assertEquals("device-1", import(version).appSettingsTable.single().deviceId)
        }
    }

    @Test
    fun `прочитанный бэкап переживает повторное сохранение`() {
        listOf("3.0.0", "3.1.0", "3.1.1").forEach { version ->
            val backup = import(version)
            val again = Json.decodeFromString<BackupData>(Json.encodeToString(backup))
            assertEquals("$version: данные изменились после пересохранения", backup, again)
        }
    }

    /** Всё, что есть во всех трёх версиях, должно приехать без потерь. */
    private fun assertCommonData(backup: BackupData) {
        val farm = backup.projectTable[0]
        assertEquals("Ферма", farm.title)
        assertEquals("01.01.2026", farm.date)
        assertTrue(farm.mode)
        assertNull(farm.imagePath)

        val incubatorProject = backup.projectTable[1]
        assertTrue(incubatorProject.archive)
        assertEquals("QkFTRTY0", incubatorProject.imagePath)

        val settings = backup.settingsTable.single()
        assertEquals(Suffix.RUBLE, settings.currencySuffix)
        assertEquals(Suffix.CENTIMETERS, settings.linearSuffix)

        assertEquals("08:00", backup.timeNotificationProjectTable.single().time)

        val add = backup.addTable.single()
        assertEquals("Яйцо", add.title)
        assertEquals(10.0, add.count, 0.0)
        assertEquals(Suffix.PIECES, add.countSuffix)
        assertEquals(1L, add.animalId)

        val sale = backup.saleTable.single()
        assertEquals(ProductOrigin.ADD, sale.productOrigin)
        assertEquals(50.0, sale.priceAll!!, 0.0)
        assertEquals("Иван", sale.buyer)

        val writeOff = backup.writeOff.single()
        assertTrue(writeOff.status)
        assertNull(writeOff.priceSuffix)

        val expenses = backup.expensesTable.single()
        assertTrue(expenses.isFood)
        assertEquals(Suffix.KILOGRAM_DAY, expenses.feedFoodSuffix)
        assertEquals(13, expenses.foodDesignedDay)
        assertEquals("20.08.2026", expenses.lastDayFood)

        assertEquals("Заметка", backup.noteTable.single().title)
        assertEquals(50.0, backup.expensesAnimal.single().percentExpenses, 0.0)

        val animal = backup.animalTable[0]
        assertEquals("Курица", animal.name)
        assertEquals(Suffix.GRAM_DAY, animal.foodDaySuffix)
        assertEquals(100.0, animal.foodDay, 0.0)

        val groupAnimal = backup.animalTable[1]
        assertTrue(groupAnimal.group)
        assertTrue(groupAnimal.archive)
        assertEquals("06.06.2025", groupAnimal.dateFactory)

        assertEquals(AnimalCountVersion.ADD, backup.animalCountTable.single().version)
        assertEquals("2", backup.animalWeightTable.single().weight)
        assertEquals(Suffix.CENTIMETERS, backup.animalSizeTable.single().suffix)
        assertEquals("01.02.2026", backup.animalVaccinationTable.single().nextVaccination)

        val incubator = backup.incubatorTable.single()
        assertEquals(48, incubator.capacity)
        assertTrue(incubator.isAutoRotation)

        val bookmark = backup.bookmarkTable.single()
        assertEquals(TypeEgg.DUCKS, bookmark.type)
        assertEquals(20, bookmark.count)
        assertEquals(7.0, bookmark.chickPrice!!, 0.0)

        assertEquals("37.5", backup.incubatorParameters.single().temp)
        assertEquals("09:00", backup.timeNotificationIncubatorTable.single().time)
        assertEquals("Семён", backup.profileTable.single().name)
    }
}