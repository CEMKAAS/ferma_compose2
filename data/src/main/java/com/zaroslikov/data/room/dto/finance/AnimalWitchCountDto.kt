package com.zaroslikov.data.room.dto.finance

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.Suffix

data class AnimalWitchCountAndImageDto(
    val title: String,
    val type: String,
    val count: Double,
    val suffix: Suffix,
    @ColumnInfo(name = "current_icon")
    val currentIcon: Int?,
    @ColumnInfo(name = "image_path")
    val imagePath: String?
)
