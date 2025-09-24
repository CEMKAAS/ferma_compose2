package com.zaroslikov.data.room.dto.animal

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.Suffix

data class AnimalCountPriceDto(
    val id: Long,
    val count: String,
    val suffix: Suffix,
    val date: String,
    @ColumnInfo(name = "animal_id")
    val animalId: Long,
    val note: String,
    val version: AnimalCountVersion?,
    val price: Double?,
    val buyer: String?,
    @ColumnInfo(name = "table_id")
    val tableId: Long?,
    val idPT: Long?
)
