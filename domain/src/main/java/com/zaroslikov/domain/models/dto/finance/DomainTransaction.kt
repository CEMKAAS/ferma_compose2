package com.zaroslikov.domain.models.dto.finance

import com.zaroslikov.domain.models.enums.FinanceCategory
import com.zaroslikov.domain.models.enums.Suffix

data class DomainTransaction(
    val value: Double,
    val suffix: Suffix,
    val price: Double?,
    val priceAll: Double?,
    val category: String?,
    val animal: String?,
    val buyer: String? = null,
    val data: String,
    val categoryFinance: FinanceCategory
)