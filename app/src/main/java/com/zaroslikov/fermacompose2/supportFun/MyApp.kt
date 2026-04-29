package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.fermacompose2.R
import java.text.NumberFormat
import java.util.Locale

fun Double.formatNumber(isGroupingUsed: Boolean = true): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    numberFormat.isGroupingUsed = isGroupingUsed
    return numberFormat.format(this).toString()
}

fun Int.formatNumber(isGroupingUsed: Boolean = true): String {
    val numberFormat = NumberFormat.getInstance(Locale("ru", "RU"))
    numberFormat.minimumFractionDigits = 0
    numberFormat.maximumFractionDigits = 2
    numberFormat.isGroupingUsed = isGroupingUsed
    return numberFormat.format(this).toString()
}

fun dateBuilder(day: Int, month: Int, year: Int): String {
    return "%02d.%02d.%04d".format(day, month, year)
}

fun dateBuilder(day: Int, month: String, year: Int): String {
    return "%02d %s %04d".format(day, month, year)
}


fun monthToResString(month: Int): Int {
    return when (month) {
        1 -> R.string.month_yan
        2 -> R.string.month_feb
        3 -> R.string.month_mar
        4 -> R.string.month_apr
        5 -> R.string.month_may
        6 -> R.string.month_jun
        7 -> R.string.month_jul
        8 -> R.string.month_aug
        9 -> R.string.month_sep
        10 -> R.string.month_oct
        11 -> R.string.month_nov
        12 -> R.string.month_dec
        else -> R.string.month_yan
    }
}


fun monthToResString2(month: Int): Int {
    return when (month) {
        1 -> R.string.month_yan_
        2 -> R.string.month_feb_
        3 -> R.string.month_mar_
        4 -> R.string.month_apr_
        5 -> R.string.month_may_
        6 -> R.string.month_jun_
        7 -> R.string.month_jul_
        8 -> R.string.month_aug_
        9 -> R.string.month_sep_
        10 -> R.string.month_oct_
        11 -> R.string.month_nov_
        12 -> R.string.month_dec_
        else -> R.string.month_yan_
    }
}

fun monthToResString3(month: Int): Int {
    return when (month) {
        1 -> R.string.month_za_yan
        2 -> R.string.month_za_feb
        3 -> R.string.month_za_mar
        4 -> R.string.month_za_apr
        5 -> R.string.month_za_may
        6 -> R.string.month_za_jun
        7 -> R.string.month_za_jul
        8 -> R.string.month_za_aug
        9 -> R.string.month_za_sep
        10 -> R.string.month_za_oct
        11 -> R.string.month_za_nov
        12 -> R.string.month_za_dec
        else -> R.string.month_za_yan
    }
}

fun formatterTime(hour: Int, minute: Int): String {
    val formattedHour = hour.toString().padStart(2, '0')
    val formattedMinute = minute.toString().padStart(2, '0')

    return "$formattedHour:$formattedMinute"
}



