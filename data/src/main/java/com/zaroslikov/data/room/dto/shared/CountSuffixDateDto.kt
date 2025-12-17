package com.zaroslikov.data.room.dto.shared

import com.zaroslikov.domain.models.enums.Suffix

data class CountSuffixDateDto(
    val count: Double,
    val suffix: Suffix,
    val date: String
)