package com.zaroslikov.data.room.dto

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.zaroslikov.domain.models.enums.Category
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

data class PairDataStringInt(
    val first: String, val second: Int
)

data class SaleTitleData(
    val first: String, val second: String, val third: Category
)

data class PairDataDoubleSting(
    val first: Double, val second: String? = null
)

data class DataStringListState(val list: List<String> = listOf())

data class DataPairListState(val list: List<PairData> = listOf())

data class DataTripleListState(val list: List<TripleData> = listOf())

data class DataFourWeight(
    val first: String,
    val second: String,
    val third: Double,
    val four: Boolean
)


