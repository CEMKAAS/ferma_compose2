package com.zaroslikov.data.room.dto.expenses

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class BrieflyExpensesDto(
    val title: String,
    val count: Double,
    val price: Double,
    val suffix: Suffix,
    @ColumnInfo(name = "row_count")
    val rowCount : Long
)