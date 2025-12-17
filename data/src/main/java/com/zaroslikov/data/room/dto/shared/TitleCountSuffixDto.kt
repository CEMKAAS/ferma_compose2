package com.zaroslikov.data.room.dto.shared

import com.zaroslikov.domain.models.enums.Suffix

data class TitleCountSuffixDto(
    val title: String,
    val count: Double,
    val suffix: Suffix
)
