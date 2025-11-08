package com.zaroslikov.domain.models.dto.write_off

import com.zaroslikov.domain.models.enums.Suffix

data class BrieflyWriteOffDomain(
    val title: String = "",
    val count: Double = 0.0,
    val suffix: Suffix = Suffix.KILOGRAM,
    val price: Double = 0.0,
    val priceSuffix: Suffix = Suffix.RUBLE,
    val rowCount: Long = 0
)
