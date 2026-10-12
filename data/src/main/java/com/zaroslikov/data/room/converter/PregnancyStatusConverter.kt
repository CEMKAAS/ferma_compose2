package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.PregnancyStatus

class PregnancyStatusConverter {
    @TypeConverter
    fun fromPregnancyStatus(status: PregnancyStatus): Int = status.code

    @TypeConverter
    fun toPregnancyStatus(value: Int): PregnancyStatus = PregnancyStatus.fromCode(value)
}
