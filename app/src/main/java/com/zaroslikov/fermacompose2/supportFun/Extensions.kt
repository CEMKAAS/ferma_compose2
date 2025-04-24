package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.fermacompose2.R
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


fun String.toConvertDb(): String {
    return this.replace(Regex("[^\\d.]"), "").replace(",", ".").trim()
}

fun String.toConvertDbDouble(): Double {
    return this.replace(Regex("[^\\d.]"), "").replace(",", ".").trim().toDouble()
}

fun String.toConvertOnlyInt(): String {
    return this.replace(Regex("[^\\d.]"), "").replace(",", "").replace(".", "").trim()
}

fun String.toConvertZero(): Int {
    return if (this == "") 0 else this.toInt()
}

fun String.toConvertZeroDouble(): Double {
    return if (this == "") 0.0 else this.toDouble()
}

fun String.toFormatNumber(): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(this.toDouble()).toString()
}

fun getImageWriteOff(
    status: Int
): Int {
    return if (status == 0)
        R.drawable.baseline_cottage_24
    else
        R.drawable.baseline_delete_24
}

//Date
//Выдает сегодняшнюю датуVersionToImage в формате dd.MM.yyyy"
fun dateToday(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    return LocalDate.now().format(formatter)
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

