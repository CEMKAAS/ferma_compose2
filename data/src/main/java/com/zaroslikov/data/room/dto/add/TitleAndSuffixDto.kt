package com.zaroslikov.data.room.dto.add

import com.zaroslikov.domain.models.enums.Suffix

data class TitleAndSuffixDto(
    val title: String,
    val suffix: Suffix
)