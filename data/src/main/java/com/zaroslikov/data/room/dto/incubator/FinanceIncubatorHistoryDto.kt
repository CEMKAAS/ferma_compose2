package com.zaroslikov.data.room.dto.incubator

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix
import com.zaroslikov.domain.models.enums.TypeEgg

data class FinanceIncubatorHistoryDto(
    val title: String,
    val type: TypeEgg,
    val breed: String?,
    @ColumnInfo(name = "count")
    val countEgg: Int,
    val profit: Double,
    val income: Double,
    val chicks: Int,
    val expenses: Double,
    @ColumnInfo(name = "price_one_egg")
    val priceOneEgg: Double,
    @ColumnInfo(name = "posted_price")
    val postedPrice: Double,
    @ColumnInfo(name = "posted_egg")
    val postedEgg: Int,
    @ColumnInfo(name = "losses_price")
    val lossesPrice: Double,
    @ColumnInfo(name = "losses_egg")
    val lossesEgg: Int,
)
