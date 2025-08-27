package com.zaroslikov.data.room.dto.sale

import com.zaroslikov.domain.models.enums.Category

data class TitleSaleDto(
    val title: String,
    val suffix: String,
    val category: Category
)

