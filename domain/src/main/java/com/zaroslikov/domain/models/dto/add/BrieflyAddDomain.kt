package com.zaroslikov.domain.models.dto.add

import com.zaroslikov.domain.models.enums.Suffix

data class BrieflyAddDomain(
    val title: String = "",
    val count: Double = 0.0,
    val suffix: Suffix = Suffix.GRAM,
    val rowCount: Long = 0
)