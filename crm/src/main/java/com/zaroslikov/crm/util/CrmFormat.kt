package com.zaroslikov.crm.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

// Неразрывный пробел: сумма не переносится посередине числа
private const val GROUP_SEPARATOR = ' '

private val DISPLAY_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

private val RU = Locale("ru")

// DecimalFormat не потокобезопасен — по экземпляру на поток
private val NUMBER_FORMAT = ThreadLocal.withInitial {
    val symbols = DecimalFormatSymbols(Locale.ROOT).apply {
        groupingSeparator = GROUP_SEPARATOR
        decimalSeparator = ','
        minusSign = '-'
    }
    DecimalFormat("#,##0.##", symbols)
}

private val COMPACT_FORMAT = ThreadLocal.withInitial {
    DecimalFormat("0.#", DecimalFormatSymbols(Locale.ROOT).apply { decimalSeparator = ',' })
}

/** `12345.5` → `12 345,5`: разделитель тысяч, до 2 знаков, без хвостовых нулей. */
fun Double.crmFormat(): String {
    val text = NUMBER_FORMAT.get()!!.format(this)
    // -0,001 округляется до «-0»
    return if (text == "-0") "0" else text
}

fun Int.crmFormat(): String = toDouble().crmFormat()

/** Сумма с валютой: `1 500 ₽`; без валюты — только число. */
fun Double.crmMoney(currency: String): String = "${crmFormat()} $currency".trim()

/** Короткая запись для осей графиков: `12 500` → `12,5 тыс`, `1 200 000` → `1,2 млн`. */
fun Double.crmCompact(): String {
    val format = COMPACT_FORMAT.get()!!
    return when {
        abs(this) >= 1_000_000 -> format.format(this / 1_000_000) + " млн"
        abs(this) >= 1_000 -> format.format(this / 1_000) + " тыс"
        else -> crmFormat()
    }
}

/** Сегодняшняя дата в формате `yyyy-MM-dd`. */
fun today(): String = LocalDate.now().toString()

fun parseIsoDate(iso: String?): LocalDate? =
    try {
        iso?.let { LocalDate.parse(it) }
    } catch (e: Exception) {
        null
    }

/** `yyyy-MM-dd` → `dd.MM.yyyy`; нераспознанная строка возвращается как есть. */
fun isoToDisplay(iso: String): String = parseIsoDate(iso)?.format(DISPLAY_DATE) ?: iso

/** Количество дней от [fromIso] до [toIso]; `null`, если дата не распознана. */
fun daysBetween(fromIso: String, toIso: String): Long? {
    val from = parseIsoDate(fromIso) ?: return null
    val to = parseIsoDate(toIso) ?: return null
    return ChronoUnit.DAYS.between(from, to)
}

/** Миллисекунды UTC для `DatePicker`. */
fun isoToMillis(iso: String): Long? =
    parseIsoDate(iso)?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

fun millisToIso(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()

/** Номер месяца 1..12 → `янв`, `февр`, …, `дек` (без точки); вне диапазона — пустая строка. */
fun monthShortRu(month: Int): String =
    if (month !in 1..12) ""
    else Month.of(month).getDisplayName(TextStyle.SHORT_STANDALONE, RU).removeSuffix(".").lowercase(RU)
