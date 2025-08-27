package com.zaroslikov.data.room.dto.expenses

data class BrieflyExpensesDto(
    var title: String,
    var count: Double,
    var price: Double,
    var suffix: String
)