package com.zaroslikov.data.room.dto.incubator

import androidx.room.ColumnInfo
import com.zaroslikov.domain.models.enums.TypeEgg

data class TypeEggCountDto(
    @ColumnInfo("type_egg")
    val typeEgg: TypeEgg,
    val count: Int
)