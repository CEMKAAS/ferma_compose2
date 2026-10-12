package com.zaroslikov.domain.models.enums

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimalSpeciesTest {
    @Test fun codesAreUnique() =
        assertEquals(AnimalSpecies.entries.size, AnimalSpecies.entries.map { it.code }.toSet().size)

    @Test fun fromCodeRoundTrip() =
        AnimalSpecies.entries.forEach { assertEquals(it, AnimalSpecies.fromCode(it.code)) }

    @Test fun namesAndAliasesDoNotCollideBetweenSpecies() =
        AnimalSpecies.known.forEach { species ->
            (listOf(species.commonName, species.femaleName, species.maleName, species.groupName) + species.aliases)
                .forEach { assertEquals(it, species, AnimalSpecies.fromType(it)) }
        }

    @Test fun fromTypeIgnoresCaseSpacesAndYo() {
        assertEquals(AnimalSpecies.GOAT, AnimalSpecies.fromType("  козел "))
        assertEquals(AnimalSpecies.QUAIL, AnimalSpecies.fromType("ПЕРЕПЕЛКА"))
        assertEquals(AnimalSpecies.HORSE, AnimalSpecies.fromType("Лошадь"))
        assertEquals(AnimalSpecies.CHICKEN, AnimalSpecies.fromType("Куры"))
    }

    @Test fun incubatorGroupNamesAreRecognized() {
        assertEquals(AnimalSpecies.CHICKEN, AnimalSpecies.fromType("Курицы"))
        assertEquals(AnimalSpecies.GOOSE, AnimalSpecies.fromType("Гуси"))
        assertEquals(AnimalSpecies.QUAIL, AnimalSpecies.fromType("Перепела"))
        assertEquals(AnimalSpecies.TURKEY, AnimalSpecies.fromType("Индюки"))
        assertEquals(AnimalSpecies.DUCK, AnimalSpecies.fromType("Утки"))
    }

    @Test fun customTypeIsNotRecognized() {
        assertNull(AnimalSpecies.fromType("Нутрия"))
        assertNull(AnimalSpecies.fromType(""))
        assertNull(AnimalSpecies.fromType("   "))
    }

    @Test fun adaptTypeSwitchesNameBySex() {
        assertEquals("Бык", AnimalSpecies.adaptType("Корова", isGroup = false, sex = true))
        assertEquals("Корова", AnimalSpecies.adaptType("Бык", isGroup = false, sex = false))
        assertEquals("Петух", AnimalSpecies.adaptType("Курицы", isGroup = false, sex = true))
        assertEquals("Индоселезень", AnimalSpecies.adaptType("индоутка", isGroup = false, sex = true))
        assertEquals("Кобыла", AnimalSpecies.adaptType("Лошадь", isGroup = false, sex = false))
        assertEquals("Коровы", AnimalSpecies.adaptType("Бык", isGroup = true, sex = true))
    }

    @Test fun adaptTypeKeepsCustomTypesAndAliases() {
        assertEquals("Нутрия", AnimalSpecies.adaptType("Нутрия", isGroup = false, sex = true))
        assertEquals("Несушка", AnimalSpecies.adaptType("Несушка", isGroup = false, sex = true))
        assertEquals("Телёнок", AnimalSpecies.adaptType("Телёнок", isGroup = true, sex = false))
    }

    @Test fun sexOfOnlyForUnambiguousNames() {
        assertEquals(true, AnimalSpecies.sexOf("Бык"))
        assertEquals(false, AnimalSpecies.sexOf("крольчиха"))
        assertEquals(true, AnimalSpecies.sexOf("Петух"))
        // Общепринятое название вида пол не задаёт
        assertNull(AnimalSpecies.sexOf("Кролик"))
        assertNull(AnimalSpecies.sexOf("Корова"))
        assertNull(AnimalSpecies.sexOf("Индюк"))
        assertNull(AnimalSpecies.sexOf("Коровы"))
        assertNull(AnimalSpecies.sexOf("Несушка"))
        assertNull(AnimalSpecies.sexOf("Нутрия"))
    }

    @Test fun suggestionsContainSpeciesAndCustomTypesOnce() {
        val list = AnimalSpecies.suggestions(
            isGroup = false,
            sex = true,
            existingTypes = listOf("Корова", "Нутрия", "нутрия ", "", "Кролики", "Цыплята")
        )
        assertEquals(AnimalSpecies.known.map { it.maleName } + listOf("Нутрия", "Цыплята"), list.map { it.name })
        assertTrue(list.take(AnimalSpecies.known.size).all { it.isSystem })
        // «Цыплята» распознаётся как курица, но введён пользователем — не системный
        assertFalse(list.drop(AnimalSpecies.known.size).any { it.isSystem })
    }

    @Test fun birdsAreNotMammals() {
        assertTrue(AnimalSpecies.CHICKEN.isBird)
        assertFalse(AnimalSpecies.CHICKEN.isMammal)
        assertTrue(AnimalSpecies.COW.isMammal)
    }

    @Test fun unknownIsDefaultForCustomTypesAndCodes() {
        assertEquals(0, AnimalSpecies.UNKNOWN.code)
        assertEquals(AnimalSpecies.UNKNOWN, AnimalSpecies.resolve("Нутрия"))
        assertEquals(AnimalSpecies.COW, AnimalSpecies.resolve("Бык"))
        assertEquals(AnimalSpecies.UNKNOWN, AnimalSpecies.fromCode(999))
        assertFalse(AnimalSpecies.UNKNOWN in AnimalSpecies.known)
        assertFalse(AnimalSpecies.UNKNOWN.isMammal)
    }

    @Test fun typeEggMapsToBirdSpecies() =
        TypeEgg.entries.forEach {
            val species = AnimalSpecies.fromTypeEgg(it)
            assertTrue(species.isBird)
            assertTrue(species.isKnown)
        }
}
