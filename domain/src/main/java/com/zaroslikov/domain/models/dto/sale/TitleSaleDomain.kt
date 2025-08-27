package com.zaroslikov.domain.models.dto.sale

import com.zaroslikov.domain.models.enums.Category

data class TitleSaleDomain(
    val title: String,
    val suffix: String,
    val category: Category
)
