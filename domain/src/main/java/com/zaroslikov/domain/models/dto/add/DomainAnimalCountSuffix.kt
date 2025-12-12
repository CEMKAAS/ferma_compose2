package com.zaroslikov.domain.models.dto.add

import com.zaroslikov.domain.models.enums.Suffix

data class DomainAnimalCountSuffix(
    val title: String,
    val type: String,
    val count: Double,
    val suffix: Suffix
)
