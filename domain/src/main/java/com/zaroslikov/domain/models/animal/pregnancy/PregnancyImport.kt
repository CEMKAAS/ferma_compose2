package com.zaroslikov.domain.models.animal.pregnancy

import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalPregnancyNote

/**
 * Приводит беременности из бэкапа к валидным значениям:
 * - убирает дубликаты id (остаётся первая строка);
 * - отбрасывает строки с висячей самкой или проектом самки вне [projectIds];
 * - [DomainAnimalPregnancy.idPT] принудительно = проект самки;
 * - обнуляет висячий [DomainAnimalPregnancy.sireId];
 * - счётчики приплода ограничиваются 0..[PregnancyRules.MAX_LITTER];
 * - ACTIVE у «самки», которая не может быть беременной (самец, группа, не млекопитающее),
 *   ACTIVE с видом, отличным от вида самки, и все ACTIVE самки, кроме самой новой (max id),
 *   переводятся в INTERRUPTED / [PregnancyInterruptReason.INPUT_ERROR];
 * - у завершённых записей вид выравнивается по самке.
 *
 * [animals] — животные из того же бэкапа (уже нормализованные или нет — неважно).
 */
fun List<DomainAnimalPregnancy>.normalizedForImport(
    animals: List<DomainAnimalTable>,
    projectIds: Set<Long>
): List<DomainAnimalPregnancy> {
    val animalById = animals.associateBy { it.id }
    val valid = distinctBy { it.id }.mapNotNull { p ->
        val female = animalById[p.animalId] ?: return@mapNotNull null
        if (female.idPT !in projectIds) return@mapNotNull null
        val sireId = p.sireId?.takeIf { it in animalById }
        val fixed = p.copy(
            idPT = female.idPT,
            sireId = sireId,
            bornFemale = p.bornFemale.coerceIn(0, PregnancyRules.MAX_LITTER),
            bornMale = p.bornMale.coerceIn(0, PregnancyRules.MAX_LITTER),
            stillborn = p.stillborn.coerceIn(0, PregnancyRules.MAX_LITTER)
        )
        when {
            fixed.status != PregnancyStatus.ACTIVE -> fixed.copy(species = female.species)
            !PregnancyRules.isApplicable(female) || fixed.species != female.species ->
                fixed.interruptedAsInputError()
            else -> fixed
        }
    }
    val keepActiveIds = valid
        .filter { it.status == PregnancyStatus.ACTIVE }
        .groupBy { it.animalId }
        .values
        .map { list -> list.maxOf { it.id } }
        .toSet()
    return valid.map { p ->
        if (p.status == PregnancyStatus.ACTIVE && p.id !in keepActiveIds) p.interruptedAsInputError() else p
    }
}

private fun DomainAnimalPregnancy.interruptedAsInputError() = copy(
    status = PregnancyStatus.INTERRUPTED,
    interruptReason = PregnancyInterruptReason.INPUT_ERROR,
    endDate = endDate ?: matingDate
)

/**
 * Заметки из бэкапа: без дубликатов id и только к беременностям из [pregnancyIds]
 * (уже нормализованных — висячие заметки FK не пропустит). Пустые заметки отбрасываются,
 * нечитаемое время сбрасывается (заметка остаётся без времени).
 */
fun List<DomainAnimalPregnancyNote>.normalizedNotesForImport(
    pregnancyIds: Set<Long>
): List<DomainAnimalPregnancyNote> =
    distinctBy { it.id }
        .filter { it.pregnancyId in pregnancyIds && it.note.isNotBlank() }
        .map { it.copy(time = runCatching { PregnancyDates.normalizeTime(it.time) }.getOrNull()) }
