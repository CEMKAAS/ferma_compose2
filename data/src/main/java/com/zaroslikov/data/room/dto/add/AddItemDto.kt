package com.zaroslikov.data.room.dto.add

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class AddItemDto(
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    val title: String = "",
    val count: Double = 0.0,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix = Suffix.KILOGRAM,
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val price: Double = 0.0,
    val category: String = "",
    @ColumnInfo(name = "animal_id")
    val animalId: Long? = null,
    @ColumnInfo(name = "animal_name")
    val nameAnimal: String? = null,
    val note: String = "",
    val idPT: Long = 0,
    @ColumnInfo(name = "animal_count_id")
    val animalCountId: Long? = null
)
