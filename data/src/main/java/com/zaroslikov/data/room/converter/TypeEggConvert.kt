package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.TypeEgg

class TypeEggConvert {
    @TypeConverter
    fun fromTypeEgg(value: TypeEgg): Int = value.code

    @TypeConverter
    fun toTypeEgg(value: Int): TypeEgg = TypeEgg.fromCode(value)
}
