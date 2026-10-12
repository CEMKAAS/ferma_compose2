package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PregnancyNormsTest {

    @Test fun mammalsHaveNormsBirdsAndUnknownDont() {
        AnimalSpecies.entries.forEach { s ->
            if (s.isMammal) assertNotNull(s.name, PregnancyNorms.of(s)) else assertNull(s.name, PregnancyNorms.of(s))
        }
        assertEquals(AnimalSpecies.entries.count { it.isMammal }, PregnancyNorms.all.size)
    }

    @Test fun termsMatchRequirements() {
        val expected = mapOf(
            AnimalSpecies.RABBIT to Triple(31, 28, 35),
            AnimalSpecies.PIG to Triple(114, 110, 118),
            AnimalSpecies.COW to Triple(283, 270, 295),
            AnimalSpecies.SHEEP to Triple(147, 142, 152),
            AnimalSpecies.GOAT to Triple(150, 145, 155),
            AnimalSpecies.HORSE to Triple(340, 320, 360),
        )
        expected.forEach { (s, t) ->
            val n = PregnancyNorms.of(s)!!
            assertEquals(s.name, t, Triple(n.gestationDays, n.minDays, n.maxDays))
        }
    }

    @Test fun gestationWithinRange() = PregnancyNorms.all.forEach {
        assertTrue(it.species.name, it.minDays <= it.gestationDays && it.gestationDays <= it.maxDays)
    }

    @Test fun stageKeysUniqueAndNotBlank() = PregnancyNorms.all.forEach { n ->
        val keys = n.stages.map { it.key }
        assertTrue(n.species.name, keys.none { it.isBlank() })
        assertEquals(n.species.name, keys.size, keys.toSet().size)
    }

    @Test fun stagesSortedAndWithinBounds() = PregnancyNorms.all.forEach { n ->
        assertEquals(n.species.name, n.stages.sortedBy { it.dayFrom }, n.stages)
        n.stages.forEach {
            assertTrue("${n.species} ${it.key}", it.dayFrom in 0..it.dayTo && it.dayTo <= n.maxDays + 1)
            assertTrue("${n.species} ${it.key}", it.title.isNotBlank() && it.description.isNotBlank())
        }
    }

    @Test fun commonStagesPresent() = PregnancyNorms.all.forEach { n ->
        val byKey = n.stages.associateBy { it.key }
        val window = byKey.getValue("birth_window")
        assertEquals(n.minDays to n.maxDays, window.dayFrom to window.dayTo)
        assertEquals(PregnancyStageKind.BIRTH_WINDOW, window.kind)
        val birth = byKey.getValue("birth")
        assertEquals(n.gestationDays, birth.dayFrom)
        assertEquals(PregnancyStageKind.BIRTH, birth.kind)
        val overdue = byKey.getValue("overdue")
        assertEquals(n.maxDays + 1, overdue.dayFrom)
        assertEquals(PregnancyStageKind.OVERDUE, overdue.kind)
    }

    @Test fun speciesStagesFromTable() {
        fun days(s: AnimalSpecies, key: String) =
            PregnancyNorms.of(s)!!.stages.single { it.key == key }.let { it.dayFrom to it.dayTo }
        assertEquals(10 to 14, days(AnimalSpecies.RABBIT, "palpation"))
        assertEquals(25 to 27, days(AnimalSpecies.RABBIT, "nest"))
        assertEquals(107 to 109, days(AnimalSpecies.PIG, "farrowing_pen"))
        assertEquals(223 to 223, days(AnimalSpecies.COW, "dry_off"))
        assertEquals(133 to 133, days(AnimalSpecies.SHEEP, "lambing_prep"))
        assertEquals(90 to 105, days(AnimalSpecies.GOAT, "dry_off"))
        assertEquals(310 to 310, days(AnimalSpecies.HORSE, "foaling_prep"))
    }

    @Test fun termsAndTipsFilled() = PregnancyNorms.all.forEach { n ->
        with(n.terms) {
            assertTrue(listOf(pregnantAdjective, birthName, youngSingular, youngPlural).none { it.isBlank() })
        }
        assertTrue(n.species.name, n.tips.size in 2..3)
        assertEquals("Сукрольна", PregnancyNorms.of(AnimalSpecies.RABBIT)!!.terms.pregnantAdjective)
    }

    @Test fun resorptionOnlyForRabbit() = PregnancyNorms.all.forEach { n ->
        assertEquals(
            n.species.name,
            n.species == AnimalSpecies.RABBIT,
            PregnancyInterruptReason.RESORPTION in n.interruptReasons
        )
        assertTrue(PregnancyInterruptReason.OTHER in n.interruptReasons)
    }
}
