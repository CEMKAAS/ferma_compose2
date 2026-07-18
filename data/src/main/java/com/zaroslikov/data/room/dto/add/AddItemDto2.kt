package com.zaroslikov.data.room.dto.add

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class AddItemDto2(
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    val title: String = "",
    val count: Double = 0.0,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix = Suffix.KILOGRAM,
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val category: String?,
    @ColumnInfo(name = "animal_name")
    val nameAnimal: String? = null,
    val note: String = "",
    val animalCountId: Long? = null
)
