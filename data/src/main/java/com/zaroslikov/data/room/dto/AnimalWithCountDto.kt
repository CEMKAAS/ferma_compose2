package com.zaroslikov.data.room.dto

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix

/** Строка [com.zaroslikov.data.room.dao.ANIMAL_WITH_HERD_COLUMNS]. */
data class AnimalWithCountDto(
    val id: Long,
    val name: String,
    val type: String,
    val breed: String?,
    val date: String,
    val dateFactory: String?,
    @ColumnInfo(name = "is_group")
    val isGroup: Boolean,
    val sex: Boolean,
    @ColumnInfo(name = "food_day")
    val foodDay: Double,
    @ColumnInfo(name = "food_day_suffix")
    val foodDaySuffix: Suffix,
    @ColumnInfo(name = "archive")
    val isArchive: Boolean,
    @ColumnInfo(name = "image_path")
    val imagePath: String?,
    @ColumnInfo(name = "icon")
    val currentIcon: AppIcon?,
    @ColumnInfo(name = "females_count")
    val femalesCount: Int,
    @ColumnInfo(name = "males_count")
    val malesCount: Int,
    val herdId: Long?,
    val herdName: String?,
    val ownCount: Double,
    val countSuffix: Suffix
)
