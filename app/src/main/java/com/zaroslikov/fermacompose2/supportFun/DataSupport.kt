package com.zaroslikov.fermacompose2.supportFun

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.zaroslikov.fermacompose2.R
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


data class TripleData(
    val first: Long, val second: String, val third: String
)

data class PairData(
    val first: String, val second: String
)

data class PairDataDoubleSting(
    val first: Double, val second: String? = null
)

data class DataStringListState(val list: List<String> = listOf())

data class DataPairListState(val animalList: List<PairData> = listOf())

data class DataTripleListState(val animalList: List<TripleData> = listOf())

data class DataFourWeight(
    val first: String,
    val second: String,
    val third: Double,
    val four: Boolean
)


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