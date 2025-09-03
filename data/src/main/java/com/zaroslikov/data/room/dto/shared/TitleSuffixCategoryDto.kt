package com.zaroslikov.data.room.dto.shared

import com.zaroslikov.domain.models.enums.Category

data class TitleSuffixCategoryDto(
    val title : String,
    val suffix: String,
    val category: Category
)