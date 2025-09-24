package com.zaroslikov.domain.models.dto.add

import com.zaroslikov.domain.models.enums.Suffix

data class TitleAndSuffixDomain(
    val title: String,
    val suffix: Suffix
)