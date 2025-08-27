package com.zaroslikov.data.room.dto.write_off

import com.zaroslikov.domain.models.enums.Category

data class TitleWriteOffDto(
    val title : String,
    val suffix: String,
    val category: Category
)
