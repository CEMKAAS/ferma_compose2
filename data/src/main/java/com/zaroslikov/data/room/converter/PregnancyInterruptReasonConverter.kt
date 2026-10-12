package com.zaroslikov.data.room.converter

import androidx.room.TypeConverter
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason

class PregnancyInterruptReasonConverter {
    @TypeConverter
    fun fromPregnancyInterruptReason(reason: PregnancyInterruptReason?): Int? = reason?.code

    @TypeConverter
    fun toPregnancyInterruptReason(value: Int?): PregnancyInterruptReason? =
        value?.let { PregnancyInterruptReason.fromCode(it) }
}
