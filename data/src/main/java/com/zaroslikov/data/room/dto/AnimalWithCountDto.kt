package com.zaroslikov.data.room.dto

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix


data class AnimalWithCountDto(
    val id: Long,
    val name: String,
    val type: String,
    val date: String,
    val dateFactory: String?,
    @ColumnInfo(name = "is_group")
    val isGroup: Boolean,
    val sex: Boolean,
    val count: String?,
    val suffix: String?,
    @ColumnInfo(name = "food_day")
    val foodDay: Double,
    @ColumnInfo(name = "food_day_suffix")
    val foodDaySuffix: Suffix
)
