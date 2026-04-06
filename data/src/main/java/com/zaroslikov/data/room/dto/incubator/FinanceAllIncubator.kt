package com.zaroslikov.data.room.dto.incubator

import androidx.room.ColumnInfo

data class FinanceAllIncubator(
    val income: Double,
    val expenses: Double,
    @ColumnInfo("posted_egg")
    val postedEgg: Int,
    @ColumnInfo("losses_egg")
    val lossesEgg: Int
)
