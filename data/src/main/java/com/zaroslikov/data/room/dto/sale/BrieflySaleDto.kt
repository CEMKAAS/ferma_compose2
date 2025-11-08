package com.zaroslikov.data.room.dto.sale

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class BrieflySaleDto(
    val title: String,
    val count: Double,
    val suffix: Suffix,
    val price: Double,
    @ColumnInfo(name = "row_count")
    val rowCount: Long
)
