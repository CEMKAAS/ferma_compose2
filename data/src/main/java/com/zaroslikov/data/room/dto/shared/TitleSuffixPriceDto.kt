package com.zaroslikov.data.room.dto.shared

import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix

data class TitleSuffixPriceDto(
    val title: String,
    val suffix: Suffix,
    val price: Double,
    val category: FinanceCategory
)
