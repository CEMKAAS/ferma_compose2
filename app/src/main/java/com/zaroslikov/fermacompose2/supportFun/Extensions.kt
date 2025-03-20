package com.zaroslikov.fermacompose2.supportFun

import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


fun String.toConvertDb(): String {
    return this.replace(Regex("[^\\d.]"), "").replace(",", ".").trim()
}

fun String.toFormatNumber(): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(this.toDouble()).toString()
}


//Date
//Выдает сегодняшнюю дату в формате dd.MM.yyyy"
fun dateToday(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    return LocalDate.now().format(formatter)
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