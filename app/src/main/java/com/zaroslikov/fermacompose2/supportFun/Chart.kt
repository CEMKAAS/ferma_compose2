package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.data.room.dto.sale.CountSuffixPriceDateDto
import com.zaroslikov.domain.models.dto.sale.DomainCountSuffixPriceDate
import com.zaroslikov.domain.models.dto.shared.DomainCountSuffixDate
import com.zaroslikov.domain.models.enums.FilterDate
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.table.DomainSettings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

data class ChartScale(
    val minY: Float,
    val maxY: Float,
    val stepY: Float
)

data class DomainChartPoint(
    val date: String,
    val value: Float
)

enum class ChartGranularity {
    DAY,
    WEEK,
    MONTH,
    YEAR
}

fun chartFilter(
    list: List<DomainCountSuffixPriceDate>,
    filterDate: FilterDate,
    dateStart: String,
    dateEnd: String,
    baseSuffix: Suffix,
    domainSettings: DomainSettings
): List<DomainChartPoint> {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val (start, end) = datePeriod(filterDate, dateStart, dateEnd)

    val startDate = LocalDate.parse(start, formatter)
    val endDate = LocalDate.parse(end, formatter)

    val firstDate = list
        .minOfOrNull { parseDate(it.date) }
        ?: startDate

    val granularity = resolveGranularity(
        filterDate,
        startDate,
        endDate,
        firstDate
    )

    // 1️⃣ группируем входные данные
    val grouped: Map<LocalDate, Float> =
        list.groupBy {
            parseDate(it.date).toGroupKey(granularity)
        }.mapValues { (_, items) ->
            items.sumOf {
                it.count.conversation22(
                    suffix = it.suffix,
                    baseSuffix = baseSuffix,
                    settings = domainSettings
                ).toDouble()
            }.toFloat()
        }

    // 2️⃣ генерируем ключи периода
    val keys = mutableListOf<LocalDate>()
    val axisStart = when (filterDate) {
        FilterDate.ALL_TIME -> firstDate
        else -> startDate
    }

    var current = axisStart.toGroupKey(granularity)

    while (!current.isAfter(endDate)) {
        keys += current
        current = when (granularity) {
            ChartGranularity.DAY -> current.plusDays(1)
            ChartGranularity.WEEK -> current.plusWeeks(1)
            ChartGranularity.MONTH -> current.plusMonths(1)
            ChartGranularity.YEAR -> current.plusYears(1)
        }
    }

    // 3️⃣ финальный результат
    return keys.map { date ->
        DomainChartPoint(
            date = formatLabel(date, granularity, filterDate),
            value = grouped[date] ?: 0f
        )
    }
}

private fun parseDate(date: String): LocalDate =
    LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))


private fun resolveGranularity(
    filter: FilterDate,
    start: LocalDate,
    end: LocalDate,
    firstDate: LocalDate
): ChartGranularity {
    return when (filter) {
        FilterDate.TODAY, FilterDate.WEEK, FilterDate.MONTH -> ChartGranularity.DAY
        FilterDate.YEAR -> ChartGranularity.MONTH
        FilterDate.ALL_TIME ->
            resolvePeriodGranularity(firstDate, end)

        FilterDate.PERIOD ->
            resolvePeriodGranularity(start, end)
    }
}

private fun resolvePeriodGranularity(
    firstDate: LocalDate,
    endDate: LocalDate
): ChartGranularity {
    val days = ChronoUnit.DAYS.between(firstDate, endDate)
    return when {
        days <= 7 -> ChartGranularity.DAY          // текущая неделя
        days <= 31 -> ChartGranularity.DAY         // текущий месяц (по дням)
        days <= 365 -> ChartGranularity.MONTH      // по месяцам
        else -> ChartGranularity.YEAR              // по годам
    }
}


private fun LocalDate.toGroupKey(granularity: ChartGranularity): LocalDate =
    when (granularity) {
        ChartGranularity.DAY -> this
        ChartGranularity.WEEK -> this.with(DayOfWeek.MONDAY)
        ChartGranularity.MONTH -> this.withDayOfMonth(1)
        ChartGranularity.YEAR -> this.withDayOfYear(1)
    }

private fun formatLabel(
    date: LocalDate,
    granularity: ChartGranularity,
    filterDate: FilterDate
): String {

    val ru = Locale("ru")

    return when {
        // 👇 неделя → пон. вт. ср.
        filterDate == FilterDate.WEEK && granularity == ChartGranularity.DAY ->
            date.dayOfWeek.getDisplayName(TextStyle.SHORT, ru)

        granularity == ChartGranularity.DAY ->
            date.dayOfMonth.toString()

        granularity == ChartGranularity.MONTH ->
            date.month.getDisplayName(TextStyle.SHORT, ru)

        granularity == ChartGranularity.YEAR ->
            date.year.toString()

        else -> date.toString()
    }
}


private fun DomainCountSuffixDate.toValue(baseSuffix: Suffix): Float =
    count.conversation2(
        suffix = suffix,
        baseSuffix = baseSuffix,
        settingsSuffix = baseSuffix
    ).toFloat()

fun calculateChartScale(
    list: List<CountSuffixPriceDateDto>,
    filterDate: FilterDate
): ChartScale {

    if (list.isEmpty()) {
        return ChartScale(0f, 10f, 2f)
    }

    // 1. Преобразуем count с учётом suffix
    val values = list.map { it.count.toFloat() }

    val minValue = values.minOrNull() ?: 0f
    val maxValue = values.maxOrNull() ?: 0f

    // Если все значения одинаковые
    if (minValue == maxValue) {
        return ChartScale(
            minValue - 1f,
            maxValue + 1f,
            1f
        )
    }

    // 2. Определяем сколько делений нужно в зависимости от периода
    val divisions = when (filterDate) {
        FilterDate.TODAY -> 3
        FilterDate.WEEK -> 4
        FilterDate.MONTH -> 6
        FilterDate.YEAR -> 8
        FilterDate.ALL_TIME -> 10
        FilterDate.PERIOD -> 6
    }

    val range = maxValue - minValue
    val step = niceStep(range / divisions)

    // Округляем границы вверх/вниз до красивых значений
    val minY = floor(minValue / step) * step
    val maxY = ceil(maxValue / step) * step

    return ChartScale(
        minY = minY,
        maxY = maxY,
        stepY = step
    )
}

fun niceStep(value: Float): Float {
    val exp = floor(log10(value))
    val base = value / 10f.pow(exp)

    val nice = when {
        base < 1.5f -> 1f
        base < 3f -> 2f
        base < 7f -> 5f
        else -> 10f
    }

    return nice * 10f.pow(exp)
}