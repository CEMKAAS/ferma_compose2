package com.zaroslikov.domain.models.dto.finance

data class DomainIncomeExpenses(
    val title: String,
    val count: Double,
    val suffix: String,
    val price: Double,
    val date: String
)
