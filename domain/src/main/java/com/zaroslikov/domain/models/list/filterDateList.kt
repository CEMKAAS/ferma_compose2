package com.zaroslikov.domain.models.list

import com.zaroslikov.domain.models.enums.FilterDate
import kotlin.collections.listOf

val filterDateList = listOf(
    FilterDate.TODAY,
    FilterDate.WEEK,
    FilterDate.MONTH,
    FilterDate.YEAR,
    FilterDate.ALL_TIME,
    FilterDate.PERIOD
)