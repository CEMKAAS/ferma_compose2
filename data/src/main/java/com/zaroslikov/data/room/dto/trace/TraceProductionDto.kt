package com.zaroslikov.data.room.dto.trace

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.domain.models.enums.Suffix

/** Строка `add_table` с данными животного-производителя (LEFT JOIN `animal_table`). */
data class TraceProductionDto(
    @ColumnInfo(name = "_id")
    val id: Long,
    val count: Double,
    @ColumnInfo(name = "count_suffix")
    val countSuffix: Suffix,
    val day: Int,
    val month: Int,
    val year: Int,
    val price: Double,
    @ColumnInfo(name = "price_suffix")
    val priceSuffix: Suffix,
    @ColumnInfo(name = "animal_id")
    val animalId: Long?,
    @ColumnInfo(name = "animal_name")
    val animalName: String?,
    @ColumnInfo(name = "animal_type")
    val animalType: String?,
    @ColumnInfo(name = "animal_breed")
    val animalBreed: String?,
    @ColumnInfo(name = "animal_is_group")
    val animalIsGroup: Boolean?,
    @ColumnInfo(name = "animal_sex")
    val animalSex: Boolean?,
    @ColumnInfo(name = "animal_herd_id")
    val animalHerdId: Long?,
    @ColumnInfo(name = "animal_icon")
    val animalIcon: AppIcon?,
    @ColumnInfo(name = "animal_image_path")
    val animalImagePath: String?,
    val note: String
)
