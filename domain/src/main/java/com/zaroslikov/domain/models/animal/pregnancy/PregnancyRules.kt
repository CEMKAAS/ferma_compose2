package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.animal.group.MAX_SPLIT
import java.time.LocalDate

/** Почему нельзя отметить случку. */
enum class PregnancyBlock { NOT_MAMMAL, GROUP, MALE, ARCHIVED, ALREADY_ACTIVE }

object PregnancyRules {

    /** Не больше стольких живых детёнышей за роды — защита цикла вставки. */
    const val MAX_LITTER = MAX_SPLIT

    /** Секция «Беременность» вообще показывается (история видна и у архивных). */
    fun isApplicable(a: DomainAnimalTable): Boolean = a.species.isMammal && !a.group && !a.sex

    /** `null` — случку отметить можно. */
    fun startBlock(a: DomainAnimalTable, hasActive: Boolean): PregnancyBlock? = when {
        !a.species.isMammal -> PregnancyBlock.NOT_MAMMAL
        a.group -> PregnancyBlock.GROUP
        a.sex -> PregnancyBlock.MALE
        a.archive -> PregnancyBlock.ARCHIVED
        hasActive -> PregnancyBlock.ALREADY_ACTIVE
        else -> null
    }

    /** Самец того же проекта и вида, не группа; архивный допустим. */
    fun isValidSire(female: DomainAnimalTable, sire: DomainAnimalTable): Boolean =
        sire.id != female.id && sire.idPT == female.idPT && sire.species == female.species &&
                sire.sex && !sire.group

    /**
     * [INVALID] — дата пустая или не читается (формат dd.MM.yyyy);
     * [AFTER_END] — позже родов/прерывания (только для заметок завершённой беременности).
     */
    enum class DateError { INVALID, IN_FUTURE, BEFORE_FEMALE_BIRTH, BEFORE_MATING, AFTER_END }

    /** Дата случки: не в будущем и не раньше рождения самки. */
    fun matingDateError(mating: LocalDate, femaleBirth: LocalDate?, today: LocalDate): DateError? = when {
        mating.isAfter(today) -> DateError.IN_FUTURE
        femaleBirth != null && mating.isBefore(femaleBirth) -> DateError.BEFORE_FEMALE_BIRTH
        else -> null
    }

    /** Дата родов или прерывания: от даты случки до сегодня включительно. */
    fun endDateError(end: LocalDate, mating: LocalDate, today: LocalDate): DateError? = when {
        end.isAfter(today) -> DateError.IN_FUTURE
        end.isBefore(mating) -> DateError.BEFORE_MATING
        else -> null
    }

    /** Дата заметки: от случки до сегодня и не позже родов/прерывания ([end]), если беременность завершена. */
    fun noteDateError(date: LocalDate, mating: LocalDate, end: LocalDate?, today: LocalDate): DateError? = when {
        date.isAfter(today) -> DateError.IN_FUTURE
        date.isBefore(mating) -> DateError.BEFORE_MATING
        end != null && date.isAfter(end) -> DateError.AFTER_END
        else -> null
    }
}
