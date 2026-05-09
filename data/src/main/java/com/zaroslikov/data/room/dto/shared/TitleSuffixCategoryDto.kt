package com.zaroslikov.data.room.dto.shared

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.ProductOrigin
import com.zaroslikov.domain.models.enums.Suffix

data class TitleSuffixCategoryDto(
    val title : String,
    val suffix: Suffix,
    @ColumnInfo(name = "product_origin")
    val productOrigin: ProductOrigin
)