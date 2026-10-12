package com.zaroslikov.domain.models.animal.pregnancy

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class StageStatus { DONE, CURRENT, UPCOMING }

enum class PregnancyPhase { IN_PROGRESS, BIRTH_WINDOW, OVERDUE }

/**
 * Состояние беременности на дату. Дни беременности нумеруются с 1: день случки — 1-й,
 * день N приходится на дату случки + (N − 1) (см. [PregnancyCalculator.dateOfDay]).
 * В тех же единицах заданы сроки и этапы в [PregnancyNorms].
 */
data class PregnancyProgress(
    /** День беременности, не меньше 1. */
    val day: Int,
    val gestationDays: Int,
    val matingDate: LocalDate,
    val expectedDate: LocalDate,
    val earliestDate: LocalDate,
    val latestDate: LocalDate,
    /** Дней до ожидаемой даты; может быть < 0. */
    val daysLeft: Int,
    /** Доля срока 0..1. */
    val fraction: Float,
    /** Треть срока 1..3. */
    val trimester: Int,
    /** Последний этап с dayFrom <= day. */
    val currentStage: PregnancyStage?,
    /** Первый этап с dayFrom > day. */
    val nextStage: PregnancyStage?,
    val phase: PregnancyPhase,
    /** Дней сверх [latestDate], не меньше 0. */
    val overdueDays: Int
)

/** Напоминание об этапе: [date] — дата дня [PregnancyStage.dayFrom]. */
data class PregnancyReminder(val stage: PregnancyStage, val date: LocalDate)

object PregnancyCalculator {

    fun expectedDate(norms: SpeciesPregnancyNorms, matingDate: LocalDate): LocalDate =
        dateOfDay(matingDate, norms.gestationDays)

    /** Дата N-го дня беременности: день случки — 1-й. */
    fun dateOfDay(matingDate: LocalDate, day: Int): LocalDate = matingDate.plusDays(day - 1L)

    /** День беременности, на который приходится [date] (обратное к [dateOfDay]); до случки — меньше 1. */
    fun dayOf(matingDate: LocalDate, date: LocalDate): Int =
        ChronoUnit.DAYS.between(matingDate, date).toInt() + 1

    fun progress(norms: SpeciesPregnancyNorms, matingDate: LocalDate, today: LocalDate): PregnancyProgress {
        val day = maxOf(0L, ChronoUnit.DAYS.between(matingDate, today)).toInt() + 1
        val expected = expectedDate(norms, matingDate)
        val latest = dateOfDay(matingDate, norms.maxDays)
        val phase = when {
            day < norms.minDays -> PregnancyPhase.IN_PROGRESS
            day <= norms.maxDays -> PregnancyPhase.BIRTH_WINDOW
            else -> PregnancyPhase.OVERDUE
        }
        return PregnancyProgress(
            day = day,
            gestationDays = norms.gestationDays,
            matingDate = matingDate,
            expectedDate = expected,
            earliestDate = dateOfDay(matingDate, norms.minDays),
            latestDate = latest,
            daysLeft = ChronoUnit.DAYS.between(today, expected).toInt(),
            fraction = (day.toFloat() / norms.gestationDays).coerceIn(0f, 1f),
            trimester = ((day - 1) * 3 / norms.gestationDays + 1).coerceIn(1, 3),
            currentStage = norms.stages.lastOrNull { it.dayFrom <= day },
            nextStage = norms.stages.firstOrNull { it.dayFrom > day },
            phase = phase,
            overdueDays = maxOf(0L, ChronoUnit.DAYS.between(latest, today)).toInt()
        )
    }

    fun stageStatus(stage: PregnancyStage, day: Int): StageStatus = when {
        day > stage.dayTo -> StageStatus.DONE
        day >= stage.dayFrom -> StageStatus.CURRENT
        else -> StageStatus.UPCOMING
    }

    /** Только этапы с notify; дата — день dayFrom. */
    fun reminders(norms: SpeciesPregnancyNorms, matingDate: LocalDate): List<PregnancyReminder> =
        norms.stages
            .filter { it.notify }
            .map { PregnancyReminder(it, dateOfDay(matingDate, it.dayFrom)) }

    /**
     * Напоминания, которые ещё имеет смысл поставить на [today].
     *
     * [includeStarted] = false (перепланирование при старте, правка без смены даты): только этапы
     * с исходной датой (дата дня dayFrom) не раньше [today]. Уже идущий этап ставился раньше —
     * повторно не ставим, иначе многодневный этап напоминал бы при каждом запуске.
     *
     * [includeStarted] = true (новая случка или смена даты случки): дополнительно этап, который
     * уже начался, но не закончился (ретроспективная случка), переносится на [today];
     * закончившиеся этапы пропускаются. Время показа выбирает планировщик.
     */
    fun upcomingReminders(
        norms: SpeciesPregnancyNorms,
        matingDate: LocalDate,
        today: LocalDate,
        includeStarted: Boolean
    ): List<PregnancyReminder> =
        if (includeStarted) {
            reminders(norms, matingDate)
                .filter { !dateOfDay(matingDate, it.stage.dayTo).isBefore(today) }
                .map { if (it.date.isBefore(today)) it.copy(date = today) else it }
        } else {
            reminders(norms, matingDate).filter { !it.date.isBefore(today) }
        }
}
