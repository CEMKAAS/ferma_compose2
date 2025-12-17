package com.zaroslikov.domain.models.dto.shared

import com.zaroslikov.domain.models.enums.Suffix

data class DomainCountSuffixDate(
    val count: Double = 0.0,
    val suffix: Suffix = Suffix.PIECES,
    val date: String = ""
)