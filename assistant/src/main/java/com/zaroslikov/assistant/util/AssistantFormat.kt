package com.zaroslikov.assistant.util

import com.zaroslikov.assistant.nlu.Units
import com.zaroslikov.domain.models.enums.Suffix
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

// DecimalFormat не потокобезопасен — по экземпляру на поток
private val NUMBER_FORMAT = ThreadLocal.withInitial {
    val symbols = DecimalFormatSymbols(Locale.ROOT).apply {
        groupingSeparator = ' ' // неразрывный пробел: число не переносится посередине
        decimalSeparator = ','
        minusSign = '-'
    }
    DecimalFormat("#,##0.##", symbols)
}

/** `12345.5` → `12 345,5`: разделитель тысяч, до 2 знаков, без хвостовых нулей. */
fun Double.assistantFormat(): String {
    val text = NUMBER_FORMAT.get()!!.format(this)
    return if (text == "-0") "0" else text
}

/** «10 л», «30 шт». */
fun quantityLabel(count: Double, suffix: Suffix?): String =
    if (suffix == null) count.assistantFormat() else "${count.assistantFormat()} ${Units.label(suffix)}".trim()

/** «1 500 ₽». */
fun moneyLabel(value: Double, currency: Suffix): String =
    "${value.assistantFormat()} ${Units.label(currency)}".trim()

/** Строка числа из поля ввода → Double; запятая допустима. */
fun String.toInputDouble(): Double? = trim().replace(' ', ' ').replace(" ", "").replace(',', '.').toDoubleOrNull()

/**
 * Склонение существительного по числу: `plural(3, "запись", "записи", "записей")` → «записи».
 */
fun plural(n: Long, one: String, few: String, many: String): String {
    val abs = kotlin.math.abs(n)
    val mod10 = abs % 10
    val mod100 = abs % 100
    return when {
        mod10 == 1L && mod100 != 11L -> one
        mod10 in 2..4 && mod100 !in 12..14 -> few
        else -> many
    }
}
