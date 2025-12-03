package com.zaroslikov.domain.models.dto.finance

import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix

data class DomainIncomeExpenses(
    val title: String = "",
    val count: Double = 0.0,
    val suffix: Suffix = Suffix.PIECES,
    val price: Double = 0.0,
    val date: String = "",
    val category: FinanceCategory = FinanceCategory.SALE
)
