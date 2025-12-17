package com.zaroslikov.domain.models.dto.shared

import com.zaroslikov.domain.models.enums.Suffix

data class DomainTitleCountSuffix(
    val title: String,
    val count: Double,
    val suffix: Suffix
)
