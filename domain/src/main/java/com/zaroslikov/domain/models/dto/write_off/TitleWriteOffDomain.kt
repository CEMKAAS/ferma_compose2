package com.zaroslikov.domain.models.dto.write_off

import com.zaroslikov.domain.models.enums.Category

data class TitleWriteOffDomain(
    val title : String,
    val suffix: String,
    val category: Category
)
