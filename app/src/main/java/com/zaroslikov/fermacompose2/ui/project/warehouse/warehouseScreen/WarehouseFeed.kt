package com.zaroslikov.fermacompose2.ui.project.warehouse.warehouseScreen

import com.zaroslikov.domain.models.DomainExpensesTable
import com.zaroslikov.domain.models.enums.Suffix
import java.math.BigDecimal
import java.math.MathContext
import java.time.DateTimeException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class WarehouseFeedSummary(
    val stockKilograms: Double = 0.0,
    val stockWithoutMass: Map<Suffix, Double> = emptyMap(),
    val dailyKilograms: Double = 0.0,
    val forecastDays: Int? = null,
    val partialForecast: Boolean = false,
)

internal data class WarehouseFeedSnapshot(
    val rows: List<FoodListUi>,
    val summary: WarehouseFeedSummary,
    val exhausted: List<DomainExpensesTable>,
)

internal fun calculateWarehouseFeed(
    purchases: List<DomainExpensesTable>,
    today: LocalDate,
): WarehouseFeedSnapshot {
    val rows = mutableListOf<FoodListUi>()
    val exhausted = mutableListOf<DomainExpensesTable>()
    val stockWithoutMass = linkedMapOf<Suffix, Double>()
    var stockKilograms = BigDecimal.ZERO
    var forecastStockKilograms = BigDecimal.ZERO
    var dailyKilograms = BigDecimal.ZERO
    var hasStockWithoutConsumption = false

    for (purchase in purchases) {
        val (quantity, suffix) = purchase.stockQuantity()
        val scaleToKilograms = suffix.kilogramShift()
        val stock = scaleToKilograms?.let(quantity::scaleByPowerOfTen) ?: quantity
        val daily = if (scaleToKilograms != null) purchase.dailyKilograms(suffix) else BigDecimal.ZERO
        val hasConsumption = daily.signum() > 0
        val remaining = (stock - daily * purchase.daysSince(today).toBigDecimal()).max(BigDecimal.ZERO)
        if (hasConsumption && remaining.signum() == 0) {
            exhausted.add(purchase)
            continue
        }
        val percent = if (hasConsumption) remaining.divide(stock, MathContext.DECIMAL64).toFloat() else 1f

        if (scaleToKilograms != null) {
            stockKilograms += remaining
        } else if (remaining.signum() > 0) {
            stockWithoutMass[suffix] = stockWithoutMass.getOrDefault(suffix, 0.0) + remaining.toDouble()
        }
        if (hasConsumption) {
            forecastStockKilograms += remaining
            dailyKilograms += daily
        } else if (remaining.signum() > 0) {
            hasStockWithoutConsumption = true
        }

        rows.add(
            FoodListUi(
                id = purchase.id,
                title = purchase.title,
                daysEnd = if (hasConsumption) remaining.wholeDays(daily) else null,
                weightAll = remaining.scaleByPowerOfTen(-(scaleToKilograms ?: 0)).toDouble(),
                weightSuffix = suffix,
                price = (purchase.priceAll ?: purchase.price) * percent,
                percentFloat = percent,
            )
        )
    }

    return WarehouseFeedSnapshot(
        rows = rows,
        summary = WarehouseFeedSummary(
            stockKilograms = stockKilograms.toDouble(),
            stockWithoutMass = stockWithoutMass,
            dailyKilograms = dailyKilograms.toDouble(),
            forecastDays = if (dailyKilograms.signum() > 0) forecastStockKilograms.wholeDays(dailyKilograms) else null,
            partialForecast = dailyKilograms.signum() > 0 && hasStockWithoutConsumption,
        ),
        exhausted = exhausted,
    )
}

private fun Suffix.kilogramShift(): Int? = when (this) {
    Suffix.GRAM, Suffix.GRAM_DAY -> -3
    Suffix.KILOGRAM, Suffix.KILOGRAM_DAY -> 0
    Suffix.TONS, Suffix.TONS_DAY -> 3
    else -> null
}

private fun DomainExpensesTable.stockQuantity(): Pair<BigDecimal, Suffix> {
    val count = count.takeIf { it.isFinite() }?.toBigDecimal() ?: BigDecimal.ZERO
    if (countSuffix.kilogramShift() != null) return count to countSuffix
    val massPerPurchaseUnit = weight?.takeIf { it.isFinite() && it > 0.0 } ?: return count to countSuffix
    val massSuffix = weightSuffix?.takeIf { it.kilogramShift() != null } ?: Suffix.KILOGRAM
    return count * massPerPurchaseUnit.toBigDecimal() to massSuffix
}

private fun DomainExpensesTable.dailyKilograms(stockSuffix: Suffix): BigDecimal {
    val daily = feedFood?.takeIf { it.isFinite() && it > 0.0 } ?: return BigDecimal.ZERO
    val shift = (feedFoodSuffix ?: stockSuffix).kilogramShift() ?: return BigDecimal.ZERO
    return daily.toBigDecimal().scaleByPowerOfTen(shift)
}

private fun DomainExpensesTable.daysSince(today: LocalDate): Long {
    val purchasedOn = try {
        LocalDate.of(year, month, day)
    } catch (_: DateTimeException) {
        return 0L
    }
    return ChronoUnit.DAYS.between(purchasedOn, today).coerceAtLeast(0L)
}

// Десятичный расчёт сохраняет целые сутки при дробных нормах и остатках.
private fun BigDecimal.wholeDays(daily: BigDecimal): Int =
    divideToIntegralValue(daily).min(Int.MAX_VALUE.toBigDecimal()).toInt()
