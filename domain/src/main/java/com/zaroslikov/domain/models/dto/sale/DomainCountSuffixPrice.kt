package com.zaroslikov.domain.models.dto.sale

import com.zaroslikov.domain.models.enums.Suffix

data class DomainCountSuffixPrice(
    val count: Double,
    val suffix: Suffix,
    val price: Double
)