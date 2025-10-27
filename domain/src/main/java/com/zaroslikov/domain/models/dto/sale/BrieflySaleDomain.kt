package com.zaroslikov.domain.models.dto.sale

import com.zaroslikov.domain.models.enums.Suffix

data class BrieflySaleDomain(
    val title: String = "",
    val count: Double = 0.0,
    val suffix: String = "",
    val price: Double = 0.0
)
