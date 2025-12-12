package com.zaroslikov.data.room.dto.add

import com.zaroslikov.domain.models.enums.Suffix

data class AnimalCountSuffixDto(
    val title: String,
    val type: String,
    val count: Double,
    val suffix: Suffix
)
