package com.zaroslikov.domain.models.dto.incubator

import com.zaroslikov.domain.models.enums.TypeEgg

data class DomainFinanceIncubatorHistory(
    val title: String,
    val type: TypeEgg,
    val breed: String?,
    val countEgg: Int,
    val profit: Double,
    val income: Double,
    val chicks: Int,
    val expenses: Double,
    val priceOneEgg: Double,
    val postedPrice: Double,
    val postedEgg: Int,
    val lossesPrice: Double,
    val lossesEgg: Int,
)
