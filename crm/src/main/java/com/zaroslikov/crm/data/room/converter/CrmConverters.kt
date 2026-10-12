package com.zaroslikov.crm.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.crm.domain.model.CrmClientSource
import com.zaroslikov.crm.domain.model.CrmContactType
import com.zaroslikov.crm.domain.model.CrmInteractionType

/** Enum'ы хранятся стабильным строковым кодом, а не ordinal/name. */
class CrmConverters {

    @TypeConverter
    fun fromClientSource(value: CrmClientSource): String = value.code

    @TypeConverter
    fun toClientSource(value: String): CrmClientSource = CrmClientSource.fromCode(value)

    @TypeConverter
    fun fromContactType(value: CrmContactType): String = value.code

    @TypeConverter
    fun toContactType(value: String): CrmContactType = CrmContactType.fromCode(value)

    @TypeConverter
    fun fromInteractionType(value: CrmInteractionType): String = value.code

    @TypeConverter
    fun toInteractionType(value: String): CrmInteractionType = CrmInteractionType.fromCode(value)
}
