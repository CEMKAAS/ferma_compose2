package com.zaroslikov.domain.models.dto.sale

import com.zaroslikov.domain.models.enums.Suffix

data class DomainBuyerPrice(
    val buyer: String?,
    val price: Double,
    val count: Double,
    val suffix: Suffix,
)