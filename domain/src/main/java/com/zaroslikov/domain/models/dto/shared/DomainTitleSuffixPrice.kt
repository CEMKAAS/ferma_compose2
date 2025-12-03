package com.zaroslikov.domain.models.dto.shared

import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix

data class DomainTitleSuffixPrice(
    val title: String = "",
    val suffix: Suffix = Suffix.PIECES,
    val price: Double = 0.0,
    val category: FinanceCategory = FinanceCategory.SALE
)
