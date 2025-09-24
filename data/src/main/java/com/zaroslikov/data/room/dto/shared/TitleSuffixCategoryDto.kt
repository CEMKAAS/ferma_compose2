package com.zaroslikov.data.room.dto.shared

import com.zaroslikov.domain.models.enums.Category
import com.zaroslikov.domain.models.enums.Suffix

data class TitleSuffixCategoryDto(
    val title : String,
    val suffix: Suffix,
    val category: Category
)