package com.zaroslikov.fermacompose2.ui.start.settings

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.group.normalizedForImport
import com.zaroslikov.domain.models.animal.pregnancy.normalizedForImport
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.enums.TypeEgg
import com.zaroslikov.domain.models.table.DomainAnimalCount
import com.zaroslikov.domain.models.table.DomainAnimalHerd
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainProjectTable
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
    fun `в старых бэкапах нет сборных групп`() {
        listOf("3.0.0", "3.1.0", "3.1.1").forEach { version ->
            val backup = import(version)
            assertTrue("$version: сборные группы", backup.animalHerdTable.isEmpty())
            assertTrue("$version: нет животных", backup.animalTable.isNotEmpty())
            backup.animalTable.forEach { assertNull("$version: herdId", it.herdId) }
        }
    }

    @Test
    fun `сборная группа с членами и переводами переживает кодирование и декодирование`() {
        val herd = DomainAnimalHerd(id = 10, name = "Молочное стадо", note = "Летний выпас", currentIcon = AppIcon.ICONS_COW, idPT = 1)
        val cow = DomainAnimalTable(id = 11, name = "Зорька", herdId = 10, idPT = 1)
        val calves = DomainAnimalTable(id = 12, name = "Телята", group = true, herdId = 10, idPT = 1)
        val counts = listOf(
            DomainAnimalCount(id = 1, count = "5", date = "01.01.2026", version = AnimalCountVersion.TRANSFER_OUT, idAnimal = 12),
            DomainAnimalCount(id = 2, count = "1", date = "01.01.2026", version = AnimalCountVersion.TRANSFER_IN, idAnimal = 11),
        )
        val backup = BackupData(
            version = 8,
            animalHerdTable = listOf(herd),
            animalTable = listOf(cow, calves),
            animalCountTable = counts,
        )

        val decoded = BackupParser.parse(Json.encodeToString(backup))

        assertEquals(backup, decoded)
        val normalized = decoded.animalTable.normalizedForImport(decoded.animalHerdTable)
        assertEquals(listOf(10L, 10L), normalized.map { it.herdId })
        assertEquals(
            listOf(AnimalCountVersion.TRANSFER_OUT, AnimalCountVersion.TRANSFER_IN),
            decoded.animalCountTable.map { it.version }
        )
    }

    @Test
    fun `ссылка на отсутствующую сборную группу обнуляется при импорте`() {
        val orphan = DomainAnimalTable(id = 11, name = "Зорька", herdId = 99, idPT = 1)
        assertNull(listOf(orphan).normalizedForImport(emptyList()).single().herdId)
    }

    @Test
    fun `в старых бэкапах нет беременностей и родословной`() {
        listOf("3.0.0", "3.1.0", "3.1.1").forEach { version ->
            val backup = import(version)
            assertTrue("$version: беременности", backup.animalPregnancyTable.isEmpty())
            backup.animalTable.forEach {
                assertNull("$version: motherId", it.motherId)
                assertNull("$version: motherName", it.motherName)
                assertNull("$version: fatherId", it.fatherId)
                assertNull("$version: fatherName", it.fatherName)
            }
        }
    }

    @Test
    fun `беременности и родословная переживают кодирование и декодирование`() {
        val mother = DomainAnimalTable(id = 1, name = "Маруся", type = "Крольчиха", species = AnimalSpecies.RABBIT, idPT = 1)
        val father = DomainAnimalTable(id = 2, name = "Бублик", type = "Кролик", species = AnimalSpecies.RABBIT, sex = true, idPT = 1)
        val kid = DomainAnimalTable(
            id = 3, name = "Крольчонок 1", species = AnimalSpecies.RABBIT, idPT = 1,
            motherId = 1, motherName = "Маруся", fatherId = 2, fatherName = "Бублик"
        )
        val birth = DomainAnimalPregnancy(
            id = 1, animalId = 1, species = AnimalSpecies.RABBIT, sireId = 2, sireName = "Бублик",
            matingDate = "01.09.2026", status = PregnancyStatus.BIRTH, endDate = "02.10.2026",
            interruptReason = null, bornFemale = 1, bornMale = 0, stillborn = 1, note = "Окрол", idPT = 1
        )
        val interrupted = DomainAnimalPregnancy(
            id = 2, animalId = 1, species = AnimalSpecies.RABBIT, sireName = "Чужой",
            matingDate = "01.03.2026", status = PregnancyStatus.INTERRUPTED, endDate = "20.03.2026",
            interruptReason = PregnancyInterruptReason.FEMALE_LOST, idPT = 1
        )
        val backup = BackupData(
            version = 10,
            projectTable = listOf(DomainProjectTable(id = 1, title = "Ферма")),
            animalTable = listOf(mother, father, kid),
            animalPregnancyTable = listOf(birth, interrupted)
        )

        val json = Json.encodeToString(backup)
        val decoded = BackupParser.parse(json)

        assertEquals(backup, decoded)
        // Enum-ы пишутся стабильными кодами, а не именами
        assertTrue("код статуса BIRTH", json.contains("\"status\":1"))
        assertTrue("код статуса INTERRUPTED", json.contains("\"status\":2"))
        assertTrue("код причины FEMALE_LOST", json.contains("\"interruptReason\":4"))
        assertTrue("код вида RABBIT", json.contains("\"species\":1"))

        val animals = decoded.animalTable.normalizedForImport(decoded.animalHerdTable)
        assertEquals(kid, animals.single { it.id == 3L })
        val pregnancies = decoded.animalPregnancyTable.normalizedForImport(
            animals = decoded.animalTable,
            projectIds = decoded.projectTable.map { it.id }.toSet()
        )
        assertEquals(listOf(birth, interrupted), pregnancies)
    }

    @Test
    fun `снимок имени матери остаётся без самой матери`() {
        // Мать удалили до бэкапа: ссылки нет, имя осталось. Висячий отец обнуляется, имя тоже живёт.
        val kid = DomainAnimalTable(
            id = 5, name = "Ягнёнок", idPT = 1,
            motherId = null, motherName = "Зорька", fatherId = 99, fatherName = "Баран"
        )
        val backup = BackupData(version = 10, animalTable = listOf(kid))

        val decoded = BackupParser.parse(Json.encodeToString(backup))
        assertEquals(backup, decoded)

        val normalized = decoded.animalTable.normalizedForImport(decoded.animalHerdTable).single()
        assertNull(normalized.motherId)
        assertEquals("Зорька", normalized.motherName)
        assertNull(normalized.fatherId)
        assertEquals("Баран", normalized.fatherName)
    }

    @Test
    fun `беременности висячих самок при импорте отбрасываются`() {
        val female = DomainAnimalTable(id = 1, name = "Маруся", species = AnimalSpecies.RABBIT, idPT = 1)
        val pregnancies = listOf(
            DomainAnimalPregnancy(id = 1, animalId = 1, species = AnimalSpecies.RABBIT, sireId = 42, matingDate = "01.09.2026", idPT = 1),
            DomainAnimalPregnancy(id = 2, animalId = 7, species = AnimalSpecies.RABBIT, matingDate = "01.09.2026", idPT = 1)
        )
        val backup = BackupData(
            version = 10,
            projectTable = listOf(DomainProjectTable(id = 1)),
            animalTable = listOf(female),
            animalPregnancyTable = pregnancies
        )

        val decoded = BackupParser.parse(Json.encodeToString(backup))
        val normalized = decoded.animalPregnancyTable.normalizedForImport(
            animals = decoded.animalTable,
            projectIds = decoded.projectTable.map { it.id }.toSet()
        )

        assertEquals(listOf(1L), normalized.map { it.id })
        assertNull("висячий самец", normalized.single().sireId)
        assertEquals(PregnancyStatus.ACTIVE, normalized.single().status)
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