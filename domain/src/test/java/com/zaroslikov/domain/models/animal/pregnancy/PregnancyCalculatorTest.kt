package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.enums.AnimalSpecies
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class PregnancyCalculatorTest {
    private val rabbit = PregnancyNorms.of(AnimalSpecies.RABBIT)!!
    private val cow = PregnancyNorms.of(AnimalSpecies.COW)!!
    private val mating = LocalDate.of(2026, 10, 1)

    private fun d(s: String) = PregnancyDates.parseOrNull(s)!!

    // Дни беременности нумеруются с 1: день случки (01.10) — 1-й, день N — 01.10 + (N − 1).

    @Test fun rabbitDates() {
        val p = PregnancyCalculator.progress(rabbit, mating, mating)
        assertEquals(d("31.10.2026"), p.expectedDate) // 31-й день
        assertEquals(d("28.10.2026"), p.earliestDate) // 28-й день
        assertEquals(d("04.11.2026"), p.latestDate)   // 35-й день
        assertEquals(30, p.daysLeft)
        assertEquals(1, p.day)
        assertEquals(PregnancyPhase.IN_PROGRESS, p.phase)
    }

    @Test fun expectedDateIsGestationDay() {
        val p = PregnancyCalculator.progress(rabbit, mating, d("31.10.2026"))
        assertEquals(31, p.day)
        assertEquals(p.expectedDate, d("31.10.2026"))
        assertEquals(0, p.daysLeft)
        assertEquals("birth", p.currentStage?.key)
    }

    @Test fun rabbitDay11() {
        val p = PregnancyCalculator.progress(rabbit, mating, d("11.10.2026"))
        assertEquals(11, p.day)
        assertEquals("palpation", p.currentStage?.key)
        assertEquals("nest", p.nextStage?.key)
        assertEquals(1, p.trimester)
        assertEquals(PregnancyPhase.IN_PROGRESS, p.phase)
    }

    @Test fun rabbitBirthWindow() {
        val p = PregnancyCalculator.progress(rabbit, mating, d("30.10.2026"))
        assertEquals(PregnancyPhase.BIRTH_WINDOW, p.phase)
        assertEquals("birth_window", p.currentStage?.key)
        assertEquals(3, p.trimester)
        assertEquals(0, p.overdueDays)
    }

    @Test fun rabbitOverdue() {
        val p = PregnancyCalculator.progress(rabbit, mating, d("05.11.2026"))
        assertEquals(36, p.day)
        assertEquals(PregnancyPhase.OVERDUE, p.phase)
        assertEquals(1, p.overdueDays)
        assertEquals(-5, p.daysLeft)
        assertEquals("overdue", p.currentStage?.key)
        assertNull(p.nextStage)
    }

    @Test fun cow() {
        assertEquals(mating.plusDays(282), PregnancyCalculator.expectedDate(cow, mating))
        val dryOff = PregnancyCalculator.reminders(cow, mating).single { it.stage.key == "dry_off" }
        assertEquals(mating.plusDays(222), dryOff.date)
    }

    @Test fun dateOfDay() {
        assertEquals(mating, PregnancyCalculator.dateOfDay(mating, 1))
        assertEquals(d("10.10.2026"), PregnancyCalculator.dateOfDay(mating, 10))
    }
    @Test fun stageStatusBoundaries() {
        val palpation = rabbit.stages.single { it.key == "palpation" } // 10..14
        assertEquals(StageStatus.UPCOMING, PregnancyCalculator.stageStatus(palpation, 9))
        assertEquals(StageStatus.CURRENT, PregnancyCalculator.stageStatus(palpation, 10))
        assertEquals(StageStatus.CURRENT, PregnancyCalculator.stageStatus(palpation, 14))
        assertEquals(StageStatus.DONE, PregnancyCalculator.stageStatus(palpation, 15))
    }

    @Test fun remindersOnlyNotifyStagesWithDates() {
        val norms = rabbit.copy(stages = rabbit.stages.map { if (it.key == "nest") it.copy(notify = false) else it })
        val r = PregnancyCalculator.reminders(norms, mating)
        assertEquals(norms.stages.filter { it.notify }.map { it.key }, r.map { it.stage.key })
        assertTrue(r.none { it.stage.key == "nest" })
        r.forEach { assertEquals(mating.plusDays(it.stage.dayFrom - 1L), it.date) }
        assertEquals(d("10.10.2026"), r.first { it.stage.key == "palpation" }.date)
    }

    @Test fun fractionClamped() {
        assertEquals(1f / 31, PregnancyCalculator.progress(rabbit, mating, mating).fraction, 1e-6f)
        assertEquals(1f, PregnancyCalculator.progress(rabbit, mating, d("01.01.2027")).fraction, 0f)
        val part = PregnancyCalculator.progress(cow, mating, mating.plusDays(100)).fraction
        assertTrue(part > 0f && part < 1f)
    }

    @Test fun matingInFutureGivesDayOne() {
        val p = PregnancyCalculator.progress(rabbit, mating, mating.minusDays(5))
        assertEquals(1, p.day)
        assertEquals(1f / 31, p.fraction, 1e-6f)
        assertEquals(1, p.trimester)
        assertNull(p.currentStage)
        assertEquals(0, p.overdueDays)
    }

    @Test fun upcomingRemindersRetroactive() {
        // день 13: palpation (10–14) уже начался — переносится на сегодня; дат в прошлом нет
        val today = mating.plusDays(12)
        val r = PregnancyCalculator.upcomingReminders(rabbit, mating, today, includeStarted = true)
        assertEquals("palpation", r.first().stage.key)
        assertEquals(today, r.first().date)
        assertTrue(r.none { it.date.isBefore(today) })
        // день 16: palpation закончился — пропущен
        val later = PregnancyCalculator.upcomingReminders(rabbit, mating, mating.plusDays(15), includeStarted = true)
        assertTrue(later.none { it.stage.key == "palpation" })
        assertEquals("nest", later.first().stage.key)
    }

    @Test fun upcomingRemindersWithoutStartedSkipsRunningStage() {
        // день 13: palpation (10–14) идёт, но уже ставился раньше — повторно не ставится
        val today = mating.plusDays(12)
        val r = PregnancyCalculator.upcomingReminders(rabbit, mating, today, includeStarted = false)
        assertTrue(r.none { it.stage.key == "palpation" })
        assertEquals("nest", r.first().stage.key)
        assertEquals(mating.plusDays(24), r.first().date)
        // остальные — по исходным датам, без переноса
        r.forEach { assertEquals(mating.plusDays(it.stage.dayFrom - 1L), it.date) }
        // и на следующий день тоже
        val next = PregnancyCalculator.upcomingReminders(rabbit, mating, today.plusDays(1), includeStarted = false)
        assertTrue(next.none { it.stage.key == "palpation" })
    }

    @Test fun upcomingRemindersStageStartingTodayInBothModes() {
        // день 10: palpation начинается сегодня — исходная дата = today, есть в обоих режимах
        val today = mating.plusDays(9)
        listOf(true, false).forEach { started ->
            val r = PregnancyCalculator.upcomingReminders(rabbit, mating, today, includeStarted = started)
            assertEquals("palpation", r.first().stage.key)
            assertEquals(today, r.first().date)
        }
    }

    @Test fun upcomingRemindersFutureMatingSameInBothModes() {
        val today = mating.minusDays(1)
        assertEquals(
            PregnancyCalculator.upcomingReminders(rabbit, mating, today, includeStarted = true),
            PregnancyCalculator.upcomingReminders(rabbit, mating, today, includeStarted = false)
        )
    }

    @Test fun datesRoundTrip() {
        assertEquals("01.10.2026", PregnancyDates.format(mating))
        assertEquals(mating, PregnancyDates.parseOrNull(" 01.10.2026 "))
        assertNull(PregnancyDates.parseOrNull("2026-10-01"))
        assertNull(PregnancyDates.parseOrNull(""))
    }
}
