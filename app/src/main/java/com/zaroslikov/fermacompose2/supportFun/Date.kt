package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.FilterDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun datePeriod(
    filterDate: FilterDate,
    dateStar: String,
    dateEnd: String
): Pair<String, String> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return when (filterDate) {

        FilterDate.TODAY -> {
            val d = today.format(formatter)
            d to d
        }

        FilterDate.WEEK -> {
            val start = today.with(DayOfWeek.MONDAY).format(formatter)
            val end = today.with(DayOfWeek.SUNDAY).format(formatter)
            start to end
        }

        FilterDate.MONTH -> {
            val start = today.withDayOfMonth(1).format(formatter)
            val end = today.withDayOfMonth(today.lengthOfMonth()).format(formatter)
            start to end
        }

        FilterDate.YEAR -> {
            val start = today.withDayOfYear(1).format(formatter)
            val end = today.withDayOfYear(today.lengthOfYear()).format(formatter)
            start to end
        }

        FilterDate.ALL_TIME -> "2020-01-01" to today.format(formatter)
        FilterDate.PERIOD -> dateStar to dateEnd
    }
}