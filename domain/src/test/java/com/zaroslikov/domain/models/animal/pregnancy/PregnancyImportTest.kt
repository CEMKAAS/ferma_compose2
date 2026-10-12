package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PregnancyImportTest {
    private val female = DomainAnimalTable(id = 1, species = AnimalSpecies.RABBIT, sex = false, idPT = 10)
    private val male = DomainAnimalTable(id = 2, species = AnimalSpecies.RABBIT, sex = true, idPT = 10)
    private val animals = listOf(female, male)
    private val projects = setOf(10L)

    private fun p(
        id: Long,
        animalId: Long = 1,
        status: PregnancyStatus = PregnancyStatus.ACTIVE,
        sireId: Long? = null,
        idPT: Long = 10
    ) = DomainAnimalPregnancy(
        id = id, animalId = animalId, species = AnimalSpecies.RABBIT, sireId = sireId,
        matingDate = "01.10.2026", status = status, idPT = idPT
    )

    private fun List<DomainAnimalPregnancy>.norm(a: List<DomainAnimalTable> = animals) =
        normalizedForImport(a, projects)

    @Test fun validRowUnchanged() {
        val list = listOf(p(1, sireId = 2), p(2, status = PregnancyStatus.BIRTH))
        assertEquals(list, list.norm())
    }

    @Test fun danglingAnimalDropped() = assertEquals(listOf(5L), listOf(p(4, animalId = 99), p(5)).norm().map { it.id })

    @Test fun animalInUnknownProjectDropped() {
        val other = female.copy(id = 3, idPT = 20)
        assertTrue(listOf(p(1, animalId = 3)).norm(animals + other).isEmpty())
    }

    @Test fun idPTForcedToFemaleProject() = assertEquals(10L, listOf(p(1, idPT = 77)).norm().single().idPT)

    @Test fun danglingSireReset() = assertNull(listOf(p(1, sireId = 99)).norm().single().sireId)

    @Test fun twoActiveKeepNewest() {
        val r = listOf(p(3), p(7)).norm()
        assertEquals(PregnancyStatus.ACTIVE, r.single { it.id == 7L }.status)
        val old = r.single { it.id == 3L }
        assertEquals(PregnancyStatus.INTERRUPTED, old.status)
        assertEquals(PregnancyInterruptReason.INPUT_ERROR, old.interruptReason)
        assertEquals("01.10.2026", old.endDate)
    }

    @Test fun finishedNotTouchedByActiveRule() {
        val r = listOf(p(3, status = PregnancyStatus.BIRTH), p(7)).norm()
        assertEquals(PregnancyStatus.BIRTH, r.single { it.id == 3L }.status)
        assertEquals(PregnancyStatus.ACTIVE, r.single { it.id == 7L }.status)
    }

    @Test fun duplicateIdsKeepFirst() {
        val r = listOf(p(1), p(1, status = PregnancyStatus.BIRTH)).norm()
        assertEquals(1, r.size)
        assertEquals(PregnancyStatus.ACTIVE, r.single().status)
    }

    @Test fun activeOnNonFemaleBecomesInputError() {
        val group = female.copy(id = 3, group = true)
        val bird = female.copy(id = 4, species = AnimalSpecies.CHICKEN)
        val r = listOf(p(1, animalId = 2), p(2, animalId = 3), p(3, animalId = 4))
            .norm(animals + group + bird)
        assertEquals(3, r.size)
        r.forEach {
            assertEquals(PregnancyStatus.INTERRUPTED, it.status)
            assertEquals(PregnancyInterruptReason.INPUT_ERROR, it.interruptReason)
        }
    }

    @Test fun activeWithOtherSpeciesBecomesInputError() {
        val r = listOf(p(1).copy(species = AnimalSpecies.PIG)).norm().single()
        assertEquals(PregnancyStatus.INTERRUPTED, r.status)
        assertEquals(PregnancyInterruptReason.INPUT_ERROR, r.interruptReason)
    }

    @Test fun finishedSpeciesAlignedToFemale() {
        val r = listOf(p(1, status = PregnancyStatus.BIRTH).copy(species = AnimalSpecies.PIG)).norm().single()
        assertEquals(PregnancyStatus.BIRTH, r.status)
        assertEquals(AnimalSpecies.RABBIT, r.species)
    }

    @Test fun litterCountersClamped() {
        val r = listOf(
            p(1, status = PregnancyStatus.BIRTH)
                .copy(bornFemale = -3, bornMale = PregnancyRules.MAX_LITTER + 50, stillborn = Int.MAX_VALUE)
        ).norm().single()
        assertEquals(0, r.bornFemale)
        assertEquals(PregnancyRules.MAX_LITTER, r.bornMale)
        assertEquals(PregnancyRules.MAX_LITTER, r.stillborn)
    }

    @Test fun emptyBackup() = assertTrue(emptyList<DomainAnimalPregnancy>().norm().isEmpty())
}
