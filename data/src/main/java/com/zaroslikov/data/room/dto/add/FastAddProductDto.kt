package com.zaroslikov.data.room.dto.add

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class FastAddProductDto(
    val title: String,
    val count: Double,
    val suffix: Suffix,
    val category: String?,
    val idAnimal: Long?,
    val animalName: String?,
    @ColumnInfo(name = "count_row")
    val countRow: Int
)

