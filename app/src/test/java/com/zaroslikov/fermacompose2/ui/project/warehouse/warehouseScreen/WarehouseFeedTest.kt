package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WarehouseFeedTest {
    private val today = LocalDate.of(2026, 11, 30)

    @Test
    fun convertsMassPackagesVolumeAndDailyRatesBeforeSumming() {
        val snapshot = calculateWarehouseFeed(
            listOf(
                feed(1, 750.0, Suffix.GRAM, 250.0, Suffix.GRAM_DAY),
                feed(2, 1.0, Suffix.KILOGRAM, 0.25, Suffix.KILOGRAM_DAY),
                feed(3, 0.003, Suffix.TONS, 0.001, Suffix.TONS_DAY),
                feed(4, 2.0, Suffix.PIECES, 0.5, Suffix.KILOGRAM_DAY, 1_000.0, Suffix.GRAM),
                feed(5, 2.0, Suffix.LITERS, 0.5, Suffix.KILOGRAM_DAY, 0.25, Suffix.KILOGRAM_TO_LITERS),
                feed(6, 3.0, Suffix.CUBIC_METERS, 0.5, Suffix.KILOGRAM_DAY, 0.5, Suffix.KILOGRAM_TO_CUBIC_METERS),
            ),
            today,
        )

        assertEquals(8.75, snapshot.summary.stockKilograms, 1e-9)
        assertEquals(3.0, snapshot.summary.dailyKilograms, 1e-9)
        assertEquals(2, snapshot.summary.forecastDays)
        assertFalse(snapshot.summary.partialForecast)
        assertEquals(listOf(750.0, 1.0, 0.003, 2_000.0, 0.5, 1.5), snapshot.rows.map { it.weightAll })
        assertEquals(listOf(Suffix.GRAM, Suffix.KILOGRAM, Suffix.TONS, Suffix.GRAM, Suffix.KILOGRAM, Suffix.KILOGRAM), snapshot.rows.map { it.weightSuffix })
    }

    @Test
    fun forecastUsesOnlyStockWithPositiveDailyConsumption() {
        val snapshot = calculateWarehouseFeed(
            listOf(
                feed(1, 750.0, Suffix.GRAM, 1.0, Suffix.KILOGRAM_DAY),
                feed(2, 10.0, Suffix.KILOGRAM, 0.0),
                feed(3, 2.0, Suffix.KILOGRAM, -0.5),
                feed(4, 3.0, Suffix.KILOGRAM),
            ),
            today,
        )

        assertEquals(15.75, snapshot.summary.stockKilograms, 1e-9)
        assertEquals(1.0, snapshot.summary.dailyKilograms, 1e-9)
        assertEquals(0, snapshot.summary.forecastDays)
        assertTrue(snapshot.summary.partialForecast)
        assertEquals(4, snapshot.rows.size)
        assertNull(snapshot.rows.last().daysEnd)
    }

    @Test
    fun recalculatesRemainingStockAfterDateChangeAndRemovesExhaustedRows() {
        val purchase = feed(1, 2.0, Suffix.KILOGRAM, 500.0, Suffix.GRAM_DAY)
        val initial = calculateWarehouseFeed(listOf(purchase), today)
        val tomorrow = calculateWarehouseFeed(listOf(purchase), today.plusDays(1))
        val exhausted = calculateWarehouseFeed(listOf(purchase), today.plusDays(4))

        assertEquals(4, initial.summary.forecastDays)
        assertEquals(3, tomorrow.summary.forecastDays)
        assertEquals(1.5, tomorrow.rows.single().weightAll, 1e-9)
        assertEquals(1.5, tomorrow.summary.stockKilograms, 1e-9)
        assertEquals(0.0, exhausted.summary.stockKilograms, 0.0)
        assertEquals(listOf(1L), exhausted.exhausted.map { it.id })
        assertTrue(exhausted.rows.isEmpty())
        assertNull(exhausted.summary.forecastDays)
    }

    @Test
    fun keepsUnconvertibleLegacyStocksInTheirOriginalUnits() {
        val snapshot = calculateWarehouseFeed(
            listOf(feed(1, 3.0, Suffix.LITERS), feed(2, 1.0, Suffix.PIECES)),
            today,
        )

        assertEquals(mapOf(Suffix.LITERS to 3.0, Suffix.PIECES to 1.0), snapshot.summary.stockWithoutMass)
        assertEquals(listOf(Suffix.LITERS, Suffix.PIECES), snapshot.rows.map { it.weightSuffix })
        assertNull(snapshot.summary.forecastDays)
        assertFalse(snapshot.summary.partialForecast)
    }

    @Test
    fun treatsWholeDecimalDaysAsWholeAndDoesNotGrowFuturePurchases() {
        val purchase = feed(1, 0.3, Suffix.KILOGRAM, 0.1, Suffix.KILOGRAM_DAY)
        val snapshot = calculateWarehouseFeed(listOf(purchase), today.minusDays(1))

        assertEquals(0.3, snapshot.summary.stockKilograms, 0.0)
        assertEquals(3, snapshot.summary.forecastDays)
        assertEquals(3, snapshot.rows.single().daysEnd)
    }

    @Test
    fun preservesWholeDaysAfterSubtractingAndSummingDecimalStock() {
        val purchases = listOf(
            feed(1, 0.03, Suffix.KILOGRAM, 0.01, Suffix.KILOGRAM_DAY),
            feed(2, 0.42, Suffix.KILOGRAM, 0.14, Suffix.KILOGRAM_DAY),
        )

        assertEquals(3, calculateWarehouseFeed(purchases, today).summary.forecastDays)
        val snapshot = calculateWarehouseFeed(purchases, today.plusDays(1))
        assertEquals(2, snapshot.summary.forecastDays)
        assertEquals(listOf(2, 2), snapshot.rows.map { it.daysEnd })
    }

    @Test
    fun returnsEmptySummaryWithoutActivePurchases() {
        assertEquals(WarehouseFeedSummary(), calculateWarehouseFeed(emptyList(), today).summary)
    }

    private fun feed(
        id: Long,
        count: Double,
        suffix: Suffix,
        daily: Double? = null,
        dailySuffix: Suffix? = null,
        weight: Double? = null,
        weightSuffix: Suffix? = null,
    ) = DomainExpensesTable(
        id = id,
        title = "Корм $id",
        count = count,
        countSuffix = suffix,
        day = today.dayOfMonth,
        month = today.monthValue,
        year = today.year,
        category = null,
        price = 100.0,
        isFood = true,
        isShowFood = true,
        feedFood = daily,
        feedFoodSuffix = dailySuffix,
        weight = weight,
        weightSuffix = weightSuffix,
    )
}
