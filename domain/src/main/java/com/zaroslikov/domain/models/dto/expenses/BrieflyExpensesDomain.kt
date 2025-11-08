package com.zaroslikov.data.room.dto.expenses

import com.zaroslikov.domain.models.enums.Suffix

data class BrieflyExpensesDomain(
    var title: String = "",
    var count: Double = 0.0,
    var price: Double = 0.0,
    var suffix: Suffix = Suffix.GRAM,
    var rowCount: Long = 0
)