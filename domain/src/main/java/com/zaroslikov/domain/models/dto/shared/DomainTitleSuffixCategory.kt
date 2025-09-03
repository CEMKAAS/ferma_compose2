package com.zaroslikov.domain.models.dto.shared

import com.zaroslikov.domain.models.enums.Category

data class DomainTitleSuffixCategory(
    val title : String,
    val suffix: String,
    val category: Category
)