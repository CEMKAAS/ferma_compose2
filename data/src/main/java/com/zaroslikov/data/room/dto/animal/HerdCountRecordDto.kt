package com.zaroslikov.data.room.dto.animal

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix

/** Запись счётчика члена с меткой сборной группы — для истории поголовья группы. */
data class HerdCountRecordDto(
    val id: Long,
    val count: String,
    val suffix: Suffix,
    val date: String,
    val note: String,
    val version: AnimalCountVersion?,
    val animalId: Long,
    val animalName: String,
    @ColumnInfo(name = "is_group")
    val isGroup: Boolean,
    val price: Double?,
    @ColumnInfo(name = "price_all")
    val priceAll: Double?,
    val buyer: String?,
    @ColumnInfo(name = "females_count")
    val femalesCount: Int,
    @ColumnInfo(name = "males_count")
    val malesCount: Int
)
