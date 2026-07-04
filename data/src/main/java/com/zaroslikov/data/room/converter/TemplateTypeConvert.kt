package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.TemplateType
import com.zaroslikov.domain.models.enums.TypeEgg

class TemplateTypeConvert {
    @TypeConverter
    fun fromTemplateType(value: TemplateType): Int = value.code

    @TypeConverter
    fun toTemplateType(value: Int): TemplateType = TemplateType.fromCode(value)
}