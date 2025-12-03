package com.zaroslikov.data.room.dto.finance

import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix

data class IncomeExpensesDto(
    val title: String,
    val count: Double,
    val suffix: Suffix,
    val price: Double,
    val date: String,
    val category: FinanceCategory
)
