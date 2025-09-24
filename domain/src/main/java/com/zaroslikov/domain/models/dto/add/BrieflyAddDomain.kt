package com.zaroslikov.domain.models.dto.add

import com.zaroslikov.domain.models.enums.Suffix

data class BrieflyAddDomain(
    val title: String,
    val count: Double,
    val suffix: Suffix
)