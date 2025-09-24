package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.Suffix

class SuffixConverter {
    @TypeConverter
    fun fromSuffix(value: Suffix): Int = value.code

    @TypeConverter
    fun toSuffix(value: Int): Suffix = Suffix.fromCode(value)
}
