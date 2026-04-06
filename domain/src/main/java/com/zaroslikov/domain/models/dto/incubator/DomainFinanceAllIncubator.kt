package com.zaroslikov.domain.models.dto.incubator

data class DomainFinanceAllIncubator(
    val income: Double,
    val expenses: Double,
    val postedEgg: Int,
    val lossesEgg: Int
)
