package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.AnimalCountVersion

class AnimalCountVersionConverter {
    @TypeConverter
    fun fromAnimalCountVersion(category: AnimalCountVersion): Int = category.code

    @TypeConverter
    fun toAnimalCountVersion(value: Int): AnimalCountVersion = AnimalCountVersion.fromCode(value)
}

