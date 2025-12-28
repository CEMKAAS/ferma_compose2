package com.zaroslikov.fermacompose2.supportFun

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.ui.formatNumber
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt


fun String.toConvertDb1(): String {
    return this.replace(Regex("[^\\d.]"), "").trim()
}

fun String.toConvertDb(): String {
    return this.replace(",", ".").replace(Regex("[^\\d.]"), "").trim()
}

fun String.toConvertDbDouble(): Double {
    return this.replace(",", ".").replace(Regex("[^\\d.]"), "").trim().toDouble()
}

fun String.toConvertDbOnlyInt(): Int {
    return this.replace(",", "").replace(".", "").replace(Regex("[^\\d.]"), "").trim().toInt()
}

fun String.toConvertOnlyInt2(): Int {
    return this.replace(",", "").replace(".", "").trim().toInt()
}

fun String.toConvertOnlyInt(): String {
    return this.replace(",", "").replace(".", "").replace(Regex("[^\\d.]"), "").trim()
}

fun String.toConvertZero(): Int {
    return if (this == "") 0 else this.toDouble().roundToInt()
}

fun String.toConvertZeroString(): String {
    return if (this == "") "0" else this
}

fun String.toConvertZeroDouble(): Double {
    return if (this == "") 0.0 else this.replace(",", ".").replace(Regex("[^\\d.]"), "").trim()
        .toDouble()
}

fun String.toConvertZeroDouble2(): Double {
    return if (this == "") 0.0 else this.replace(",", ".").trim()
        .toDouble()
}

fun String.toConvertZeroTooOneDouble(): Double {
    return if (this == "") 1.0 else this.toDouble()
}

fun String.toFormatNumber(): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(this.toConvertZeroDouble()).toString()
}


fun String.toFormatNumber2(): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(this.toConvertZeroDouble2()).toString()
}

fun getImageWriteOff(
    status: Boolean
): Int {
    return if (status)
        R.drawable.baseline_cottage_24
    else
        R.drawable.baseline_delete_24
}


fun calculatePriceAll(price: String, count: String): String {
    return (price.toConvertZeroString().toConvertDbDouble() * count.toConvertZeroString()
        .toConvertDbDouble()).formatNumber()
}

//Date
//Выдает сегодняшнюю датуVersionToImage в формате dd.MM.yyyy"
fun dateToday(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    return LocalDate.now().format(formatter)
}

fun dateTodayNextYear(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    return LocalDate.now().plusYears(1).format(formatter)
}

fun dateTodayArray(): List<Int> {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    val list = LocalDate.now().format(formatter)
    return list.split(".").map { it.toInt() }
}


fun formatDateToString(day: Int, month: Int, year: Int) =
    String.format(Locale.getDefault(), "%02d.%02d.%d", day, month, year)

fun formatDateToLong(data: String): Long {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    val utcZone = ZoneId.systemDefault()

    return if (data.isEmpty()) {
        // Если строка пустая, возвращаем текущее время в UTC
        ZonedDateTime.now(utcZone).toInstant().toEpochMilli()
    } else {
        // Парсим строку в LocalDate, затем преобразуем в ZonedDateTime в UTC
        val localDate = LocalDate.parse(data, formatter)
        localDate.atStartOfDay(utcZone).toInstant().toEpochMilli()
    }
}

fun firstDayOfMonth(): Pair<String, Long> {
    val today = LocalDate.now()
    val firstDayOfMonth = today.withDayOfMonth(1)
    val dateString =
        firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) // "2024-07-01"
    val timestamp = firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return Pair(dateString, timestamp)
}

fun todayOfMonth(): Pair<String, Long> {
    val today = LocalDate.now()
    val dateString = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val timestamp = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return Pair(dateString, timestamp)
}

fun dateLongToStringSQLPair(timestamp: Long): Pair<String, Long> {
    return Pair(
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), timestamp
    )
}

fun dateLongToStringSQL(timestamp: Long): String {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

}

fun dateLongToString(timestamp: Long): String {
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

}

fun getAgeFromDate(context: Context, dateString: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val birthDate = LocalDate.parse(dateString, formatter)
    val currentDate = LocalDate.now()

    if (birthDate.isAfter(currentDate)) {
        return "Дата в будущем"
    }

    val period = Period.between(birthDate, currentDate)
    val years = period.years
    val months = period.months

    val yeatsString = getString(context, R.string.date_years)
    val monthsString = getString(context, R.string.date_months)

    return when {
        years >= 1 -> "$years $yeatsString $months $monthsString"
        else -> {
            val totalMonths = ChronoUnit.MONTHS.between(birthDate, currentDate)
            "$totalMonths $monthsString"
        }
    }
}

fun getIndicatorsToVersion(indicators: String): Int {
    return when (indicators) {
        "Вес" -> 0
        "Размер" -> 1
        "Количество" -> 2
        "Прививки" -> 3
        else -> 5
    }
}

fun getVersionToImage(version: Int): Int {
    return when (version) {
        0 -> R.drawable.weight_24dp_000000_fill0_wght400_grad0_opsz24
        1 -> R.drawable.height_24dp_000000_fill0_wght400_grad0_opsz24
        2 -> R.drawable.baseline_spoke_24
        3 -> R.drawable.vaccines_24dp_000000_fill0_wght400_grad0_opsz24
        else -> 5
    }
}

fun getVersionToStringTitle(version: Int): Int {
    return when (version) {
        0 -> R.string.weight_screen_title
        1 -> R.string.height_screen_title
        2 -> R.string.count_screen_title
        3 -> R.string.vaccination_screen_title
        else -> R.string.is_empty
    }
}

fun getVersionToStringSuffix(version: Int): Int {
    return when (version) {
        0 -> R.string.suffix_kilogram
        1 -> R.string.suffix_meters
        2 -> R.string.suffix_pieces
        else -> R.string.is_empty
    }
}

fun getVersionToStringSup(version: Int): Int {
    return when (version) {
        0 -> R.string.support_text_weight_animal
        1 -> R.string.support_text_height_animal
        2 -> R.string.support_text_count_animals
        3 -> R.string.support_text_vaccination_animals
        else -> R.string.is_empty
    }
}

fun getVersionToStringError(version: Int): Int {
    return when (version) {
        0 -> R.string.error_no_weight_animal
        1 -> R.string.error_no_height_animal
        2 -> R.string.error_no_count_product
        3 -> R.string.error_no_vaccination
        else -> R.string.is_empty
    }
}

fun getVersionToTitleNoMessage(version: Int): Int {
    return when (version) {
        0 -> R.string.message_no_date_title_weight
        1 -> R.string.message_no_date_title_height
        2 -> R.string.message_no_date_title_count
        3 -> R.string.message_no_date_title_vaccination
        else -> R.string.is_empty
    }
}

fun getVersionToMessageNoMessage(version: Int): Int {
    return when (version) {
        0 -> R.string.message_no_date_message_weight
        1 -> R.string.message_no_date_message_height
        2 -> R.string.message_no_date_message_count
        3 -> R.string.message_no_date_message_vaccination
        else -> R.string.is_empty
    }
}

fun getVersionToSupportNoMessage(version: Int): Int {
    return when (version) {
        0 -> R.string.message_no_date_support_weight
        1 -> R.string.message_no_date_support_height
        2 -> R.string.message_no_date_support_count
        3 -> R.string.message_no_date_support_vaccination
        else -> R.string.is_empty
    }
}

fun getVersionToButtonMessage(version: Int): Int {
    return when (version) {
        0 -> R.string.button_sale_message_no_weight
        1 -> R.string.button_sale_message_no_height
        2 -> R.string.button_sale_message_no_count
        3 -> R.string.button_sale_message_no_vaccination
        else -> R.string.button_add
    }
}

