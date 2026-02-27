package com.zaroslikov.data.room.dto.incubator

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class FinanceIncubatorMainDto(
    val profit: Double,
    val income: Double,
    val chicks: Int,
    val expenses: Double,
    val incubator: Double,
    @ColumnInfo("eggs_price")
    val eggsPrice: Double,
    @ColumnInfo("posted_price")
    val postedPrice: Double,
    @ColumnInfo("losses_price")
    val lossesPrice: Double,
    @ColumnInfo("posted_egg")
    val postedEgg: Int,
    @ColumnInfo("losses_egg")
    val lossesEgg: Int,
    @ColumnInfo("average_egg_price")
    val averageEggPrice: Double,
    @ColumnInfo("average_chicks_price")
    val averageChicksPrice: Double,
    @ColumnInfo("cost_chicks_price")
    val costChicksPrice: Double
)
