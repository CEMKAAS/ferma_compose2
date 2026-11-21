package com.zaroslikov.fermacompose2.data.calendar

import com.zaroslikov.calendar.domain.CalendarEvent
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyCalculator
import com.zaroslikov.domain.models.animal.pregnancy.PregnancyNorms
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainRecyclingWithProducts
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

internal data class CalendarAnimal(val name: String, val archived: Boolean)

private val legacyDate = DateTimeFormatter.ofPattern("d.M.uuuu", Locale.ROOT)
    .withResolverStyle(ResolverStyle.STRICT)

private val storedTime = DateTimeFormatter.ofPattern("H:mm", Locale.ROOT)
    .withResolverStyle(ResolverStyle.STRICT)

internal fun calendarDate(raw: String?): LocalDate? {
    val value = raw?.trim()?.takeIf(String::isNotEmpty) ?: return null
    val formatter = if ('.' in value) legacyDate else DateTimeFormatter.ISO_LOCAL_DATE
    return validDateTime { LocalDate.parse(value, formatter) }?.takeIf { it.year in 1..9999 }
}

internal fun calendarTime(raw: String?): LocalTime? =
    raw?.trim()?.let { validDateTime { LocalTime.parse(it, storedTime) } }

private inline fun <T> validDateTime(block: () -> T): T? =
    try {
        block()
    } catch (_: DateTimeException) {
        null
    }

internal fun vaccinationEvents(
    vaccinations: List<DomainAnimalVaccination>,
    animals: Map<Long, CalendarAnimal>
): List<CalendarEvent> {
    val dated = vaccinations.mapNotNull { dose -> calendarDate(dose.date)?.let { dose to it } }
    val latest = dated.groupBy { (dose, _) ->
        dose.idAnimal to dose.vaccination.trim().lowercase(Locale.ROOT)
    }.values.mapNotNull { doses ->
        doses.maxWithOrNull(compareBy({ it.second }, { it.first.id }))?.first
    }
    return buildList {
        dated.forEach { (dose, date) ->
            val animal = animals[dose.idAnimal] ?: return@forEach
            add(CalendarEvent(
                id = "vaccination:${dose.id}",
                kind = CalendarEventKind.VACCINATION,
                title = "Прививка · ${dose.vaccination}",
                date = date,
                note = eventNote(animal.name, dose.note)
            ))
        }
        latest.forEach { dose ->
            val animal = animals[dose.idAnimal]?.takeUnless { it.archived } ?: return@forEach
            val date = calendarDate(dose.nextVaccination) ?: return@forEach
            add(CalendarEvent(
                id = "vaccination-next:${dose.id}",
                kind = CalendarEventKind.NEXT_VACCINATION,
                title = "Следующая прививка · ${dose.vaccination}",
                date = date,
                note = animal.name
            ))
        }
    }
}

internal fun birthEvents(
    pregnancies: List<DomainAnimalPregnancy>,
    animals: Map<Long, CalendarAnimal>
): List<CalendarEvent> = pregnancies.mapNotNull { pregnancy ->
    val animal = animals[pregnancy.animalId] ?: return@mapNotNull null
    val (date, title, kind) = when (pregnancy.status) {
        PregnancyStatus.ACTIVE -> {
            val mating = calendarDate(pregnancy.matingDate) ?: return@mapNotNull null
            val norms = PregnancyNorms.of(pregnancy.species) ?: return@mapNotNull null
            Triple(
                PregnancyCalculator.expectedDate(norms, mating),
                "Ожидаемые роды · ${animal.name}",
                CalendarEventKind.EXPECTED_BIRTH,
            )
        }
        PregnancyStatus.BIRTH -> {
            val actual = calendarDate(pregnancy.endDate) ?: return@mapNotNull null
            Triple(actual, "Роды · ${animal.name}", CalendarEventKind.BIRTH)
        }
        PregnancyStatus.INTERRUPTED -> return@mapNotNull null
    }
    CalendarEvent("birth:${pregnancy.id}", kind, title, date, note = pregnancy.note)
}

internal fun recyclingEvents(batches: List<DomainRecyclingWithProducts>): List<CalendarEvent> =
    batches.flatMap { batch ->
        batch.products.mapNotNull { product ->
            val year = product.readyYear ?: return@mapNotNull null
            val month = product.readyMonth ?: return@mapNotNull null
            val day = product.readyDay ?: return@mapNotNull null
            val date = validDateTime { LocalDate.of(year, month, day) }
                ?.takeIf { it.year in 1..9999 } ?: return@mapNotNull null
            val time = validDateTime { LocalTime.of(product.readyHour ?: 0, product.readyMinute ?: 0) }
            CalendarEvent(
                id = "recycling:${product.id}",
                kind = CalendarEventKind.RECYCLING_READY,
                title = "Готовность · ${product.title}",
                date = date,
                time = time,
                note = batch.recycling.note,
                statusTitle = if (product.isCompleted) "Готово" else "В переработке",
            )
        }
    }

internal fun feedEvents(batches: List<DomainExpensesTable>): List<CalendarEvent> =
    batches.mapNotNull { batch ->
        val date = calendarDate(batch.lastDayFood) ?: return@mapNotNull null
        CalendarEvent(
            id = "feed:${batch.id}",
            kind = CalendarEventKind.FEED_END,
            title = "Окончание корма · ${batch.title}",
            date = date,
            note = batch.note
        )
    }

internal fun birthdayEvent(id: String, name: String, storedDate: String?): CalendarEvent? {
    val date = calendarDate(storedDate) ?: return null
    return CalendarEvent(id, CalendarEventKind.BIRTHDAY, "День рождения · $name", date, annual = true)
}

internal fun eventNote(vararg lines: String?): String =
    lines.mapNotNull { it?.takeIf(String::isNotBlank) }.joinToString("\n")
