package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class PregnancyDayNoteTest {
    private val mating = LocalDate.of(2026, 9, 1)
    private val today = LocalDate.of(2026, 10, 6)

    @Test fun dayOfIsInverseOfDateOfDay() {
        assertEquals(1, PregnancyCalculator.dayOf(mating, mating))
        assertEquals(36, PregnancyCalculator.dayOf(mating, today))
        assertEquals(36, PregnancyCalculator.dayOf(mating, PregnancyCalculator.dateOfDay(mating, 36)))
        assertEquals(0, PregnancyCalculator.dayOf(mating, mating.minusDays(1)))
    }

    @Test fun activeNoteDateFromMatingToToday() {
        assertNull(PregnancyRules.noteDateError(mating, mating, null, today))
        assertNull(PregnancyRules.noteDateError(today, mating, null, today))
        assertEquals(
            PregnancyRules.DateError.IN_FUTURE,
            PregnancyRules.noteDateError(today.plusDays(1), mating, null, today)
        )
        assertEquals(
            PregnancyRules.DateError.BEFORE_MATING,
            PregnancyRules.noteDateError(mating.minusDays(1), mating, null, today)
        )
    }

    @Test fun finishedNoteDateNotAfterEnd() {
        val end = LocalDate.of(2026, 10, 1)
        assertNull(PregnancyRules.noteDateError(end, mating, end, today))
        assertEquals(
            PregnancyRules.DateError.AFTER_END,
            PregnancyRules.noteDateError(end.plusDays(1), mating, end, today)
        )
    }

    @Test fun timeIsOptionalAndNormalized() {
        assertNull(PregnancyDates.normalizeTime(null))
        assertNull(PregnancyDates.normalizeTime("  "))
        assertEquals("07:05", PregnancyDates.normalizeTime(" 07:05 "))
        assertThrows(IllegalArgumentException::class.java) { PregnancyDates.normalizeTime("25:00") }
    }

    @Test fun importDropsUnreadableTime() {
        val notes = listOf(
            DomainAnimalPregnancyNote(id = 1, pregnancyId = 10, date = "01.09.2026", time = "08:30", note = "а"),
            DomainAnimalPregnancyNote(id = 2, pregnancyId = 10, date = "01.09.2026", time = "утро", note = "б")
        )
        val r = notes.normalizedNotesForImport(setOf(10))
        assertEquals(listOf("08:30", null), r.map { it.time })
    }

    @Test fun importKeepsOnlyNotesOfKnownPregnancies() {
        val notes = listOf(
            DomainAnimalPregnancyNote(id = 1, pregnancyId = 10, date = "01.09.2026", note = "ок"),
            DomainAnimalPregnancyNote(id = 1, pregnancyId = 10, date = "02.09.2026", note = "дубликат"),
            DomainAnimalPregnancyNote(id = 2, pregnancyId = 99, date = "01.09.2026", note = "висячая"),
            DomainAnimalPregnancyNote(id = 3, pregnancyId = 10, date = "01.09.2026", note = "  ")
        )
        val r = notes.normalizedNotesForImport(setOf(10))
        assertEquals(listOf(1L), r.map { it.id })
        assertEquals("ок", r.single().note)
    }
}
