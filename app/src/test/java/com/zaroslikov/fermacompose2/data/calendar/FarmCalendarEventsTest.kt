package com.zaroslikov.fermacompose2.data.calendar

import com.zaroslikov.calendar.domain.CalendarCategory
import com.zaroslikov.calendar.domain.CalendarEventKind
import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.AnimalSpecies
import com.zaroslikov.domain.models.enums.PregnancyStatus
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainAnimalPregnancy
import com.zaroslikov.domain.models.table.DomainAnimalVaccination
import com.zaroslikov.domain.models.table.DomainRecycling
import com.zaroslikov.domain.models.table.DomainRecyclingProduct
import com.zaroslikov.domain.models.table.DomainRecyclingWithProducts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class FarmCalendarEventsTest {
    @Test
    fun parsesLegacyAndIsoDatesAndRejectsImpossibleDates() {
        assertEquals(LocalDate.of(2024, 2, 9), calendarDate("9.2.2024"))
        assertEquals(LocalDate.of(2024, 2, 29), calendarDate("2024-02-29"))
        assertNull(calendarDate("29.02.2025"))
        assertNull(calendarDate("2025-02-29"))
        assertNull(calendarDate("0"))
        assertEquals(LocalTime.of(9, 5), calendarTime("09:05"))
        assertNull(calendarTime("24:00"))
    }

    @Test
    fun keepsArchivedHistoryAndUsesOnlyLatestValidDosePerVaccine() {
        val vaccinations = listOf(
            DomainAnimalVaccination(1, "Вакцина A", date = "01.01.2026", nextVaccination = "01.02.2026", idAnimal = 10),
            DomainAnimalVaccination(2, "Вакцина A", date = "2026-01-15", idAnimal = 10),
            DomainAnimalVaccination(3, "Вакцина B", date = "09.01.2026", nextVaccination = "09.02.2026", idAnimal = 10),
            DomainAnimalVaccination(4, "Вакцина A", date = "10.01.2026", nextVaccination = "10.02.2026", idAnimal = 20),
            DomainAnimalVaccination(5, "Вакцина B", date = "31.02.2026", nextVaccination = "01.03.2026", idAnimal = 10)
        )
        val animals = mapOf(10L to CalendarAnimal("Белка", false), 20L to CalendarAnimal("Стрелка", true))
        val events = vaccinationEvents(vaccinations, animals)

        assertEquals(
            mapOf(
                "vaccination:1" to CalendarEventKind.VACCINATION,
                "vaccination:2" to CalendarEventKind.VACCINATION,
                "vaccination:3" to CalendarEventKind.VACCINATION,
                "vaccination:4" to CalendarEventKind.VACCINATION,
                "vaccination-next:3" to CalendarEventKind.NEXT_VACCINATION,
            ),
            events.associate { it.id to it.kind }
        )
        assertEquals(setOf(CalendarCategory.VACCINATIONS), events.map { it.category }.toSet())
    }

    @Test
    fun replacesExpectedBirthWithActualDate() {
        val pregnancy = DomainAnimalPregnancy(
            id = 1, animalId = 10, species = AnimalSpecies.COW, matingDate = "01.01.2026"
        )
        val animals = mapOf(10L to CalendarAnimal("Белка", false))
        val expected = birthEvents(listOf(pregnancy), animals).single()
        val actual = birthEvents(
            listOf(pregnancy.copy(status = PregnancyStatus.BIRTH, endDate = "01.10.2026")),
            animals
        ).single()
        assertEquals(expected.id, actual.id)
        assertEquals(CalendarEventKind.EXPECTED_BIRTH, expected.kind)
        assertEquals(CalendarEventKind.BIRTH, actual.kind)
        assertEquals(LocalDate.of(2026, 10, 1), actual.date)
    }

    @Test
    fun includesSavedEndDateOfHiddenFeed() {
        val feed = DomainExpensesTable(
            id = 8, title = "Овёс", category = null, isFood = true,
            isShowFood = false, lastDayFood = "18.11.2026"
        )
        val event = feedEvents(listOf(feed)).single()
        assertEquals(CalendarEventKind.FEED_END, event.kind)
        assertEquals(CalendarCategory.PROCESSING_AND_FEED, event.category)
        assertEquals(LocalDate.of(2026, 11, 18), event.date)
    }

    @Test
    fun includesCompletedRecyclingProductWithSavedReadyTime() {
        val product = DomainRecyclingProduct(
            id = 9, title = "Сыр", count = 1.0, countSuffix = Suffix.PIECES, idPT = 1,
            readyYear = 2026, readyMonth = 11, readyDay = 19, readyHour = 16, readyMinute = 30,
            isCompleted = true
        )
        val batch = DomainRecyclingWithProducts(
            recycling = DomainRecycling(day = 18, month = 11, year = 2026, idPT = 1),
            products = listOf(product, product.copy(id = 10, readyDay = 31)),
            ingredients = emptyList()
        )
        val event = recyclingEvents(listOf(batch)).single()
        assertEquals("recycling:9", event.id)
        assertEquals(CalendarEventKind.RECYCLING_READY, event.kind)
        assertEquals(CalendarCategory.PROCESSING_AND_FEED, event.category)
        assertEquals("Готово", event.statusTitle)
        assertEquals(LocalDate.of(2026, 11, 19), event.date)
        assertEquals(LocalTime.of(16, 30), event.time)
    }

    @Test
    fun birthdayKeepsItsOwnKindAndRepeatsAnnually() {
        val event = requireNotNull(birthdayEvent("client:3", "Анна", "1990-11-20"))

        assertEquals(CalendarEventKind.BIRTHDAY, event.kind)
        assertTrue(event.annual)
    }
}
