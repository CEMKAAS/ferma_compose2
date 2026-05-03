package com.zaroslikov.data.room.dto.sale

import com.zaroslikov.domain.models.enums.Suffix

data class BuyerPriceDto(
    val buyer: String?,
    val price: Double,
    val count: Double,
    val suffix: Suffix,
)
