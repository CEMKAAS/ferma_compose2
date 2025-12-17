package com.zaroslikov.data.room.dto.sale

import com.zaroslikov.domain.models.enums.Suffix

data class CountSuffixPriceDateDto(
    val count: Double,
    val suffix: Suffix,
    val price: Double,
    val date: String
)