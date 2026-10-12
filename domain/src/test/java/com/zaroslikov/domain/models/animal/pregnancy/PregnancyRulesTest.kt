package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.AnimalSpecies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PregnancyRulesTest {
    private val female = DomainAnimalTable(id = 1, species = AnimalSpecies.RABBIT, sex = false, idPT = 10)
    private val male = DomainAnimalTable(id = 2, species = AnimalSpecies.RABBIT, sex = true, idPT = 10)
    private val today = LocalDate.of(2026, 10, 6)

    @Test fun startAllowed() = assertNull(PregnancyRules.startBlock(female, hasActive = false))

    @Test fun startBlocks() {
        assertEquals(PregnancyBlock.MALE, PregnancyRules.startBlock(male, false))
        assertEquals(PregnancyBlock.GROUP, PregnancyRules.startBlock(female.copy(group = true), false))
        assertEquals(
            PregnancyBlock.NOT_MAMMAL,
            PregnancyRules.startBlock(female.copy(species = AnimalSpecies.CHICKEN), false)
        )
        assertEquals(
            PregnancyBlock.NOT_MAMMAL,
            PregnancyRules.startBlock(female.copy(species = AnimalSpecies.UNKNOWN), false)
        )
        assertEquals(PregnancyBlock.ARCHIVED, PregnancyRules.startBlock(female.copy(archive = true), false))
        assertEquals(PregnancyBlock.ALREADY_ACTIVE, PregnancyRules.startBlock(female, true))
    }

    @Test fun applicability() {
        assertTrue(PregnancyRules.isApplicable(female))
        assertTrue(PregnancyRules.isApplicable(female.copy(archive = true)))
        assertFalse(PregnancyRules.isApplicable(male))
        assertFalse(PregnancyRules.isApplicable(female.copy(group = true)))
        assertFalse(PregnancyRules.isApplicable(female.copy(species = AnimalSpecies.DUCK)))
    }

    @Test fun validSire() {
        assertTrue(PregnancyRules.isValidSire(female, male))
        assertTrue(PregnancyRules.isValidSire(female, male.copy(archive = true)))
        assertFalse(PregnancyRules.isValidSire(female, male.copy(idPT = 11)))
        assertFalse(PregnancyRules.isValidSire(female, male.copy(species = AnimalSpecies.PIG)))
        assertFalse(PregnancyRules.isValidSire(female, male.copy(sex = false)))
        assertFalse(PregnancyRules.isValidSire(female, male.copy(group = true)))
        assertFalse(PregnancyRules.isValidSire(female, female.copy(sex = true)))
    }

    @Test fun matingDateErrors() {
        val birth = LocalDate.of(2026, 1, 1)
        assertNull(PregnancyRules.matingDateError(today, birth, today))
        assertNull(PregnancyRules.matingDateError(birth, birth, today))
        assertNull(PregnancyRules.matingDateError(today, null, today))
        assertEquals(
            PregnancyRules.DateError.IN_FUTURE,
            PregnancyRules.matingDateError(today.plusDays(1), birth, today)
        )
        assertEquals(
            PregnancyRules.DateError.BEFORE_FEMALE_BIRTH,
            PregnancyRules.matingDateError(birth.minusDays(1), birth, today)
        )
    }

    @Test fun endDateErrors() {
        val mating = LocalDate.of(2026, 9, 1)
        assertNull(PregnancyRules.endDateError(mating, mating, today))
        assertNull(PregnancyRules.endDateError(today, mating, today))
        assertEquals(
            PregnancyRules.DateError.IN_FUTURE,
            PregnancyRules.endDateError(today.plusDays(1), mating, today)
        )
        assertEquals(
            PregnancyRules.DateError.BEFORE_MATING,
            PregnancyRules.endDateError(mating.minusDays(1), mating, today)
        )
    }

    @Test fun maxLitter() = assertEquals(200, PregnancyRules.MAX_LITTER)
}
