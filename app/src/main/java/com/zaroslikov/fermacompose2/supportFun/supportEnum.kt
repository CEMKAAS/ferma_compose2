package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.IndicationStatus
import com.zaroslikov.domain.models.enums.PregnancyInterruptReason
import com.zaroslikov.fermacompose2.R

fun AnimalCountVersion.toIndicationStatus(): IndicationStatus {
    return when (this) {
        AnimalCountVersion.SALE -> IndicationStatus.SALE
        AnimalCountVersion.EXPENSES -> IndicationStatus.EXPENSES
        AnimalCountVersion.KILL -> IndicationStatus.KILL
        AnimalCountVersion.WRITE_OFF -> IndicationStatus.WRITE_OFF
        AnimalCountVersion.INCUBATOR -> IndicationStatus.INCUBATOR
        AnimalCountVersion.ADD -> IndicationStatus.ADD
        AnimalCountVersion.TRANSFER_IN -> IndicationStatus.TRANSFER_IN
        AnimalCountVersion.TRANSFER_OUT -> IndicationStatus.TRANSFER_OUT
        AnimalCountVersion.HERD_JOIN -> IndicationStatus.HERD_JOIN
        AnimalCountVersion.HERD_LEAVE -> IndicationStatus.HERD_LEAVE
    }
}
fun PregnancyInterruptReason.toResId(): Int {
    return when (this) {
        PregnancyInterruptReason.NOT_CONFIRMED -> R.string.pregnancy_reason_not_confirmed
        PregnancyInterruptReason.ABORTION -> R.string.pregnancy_reason_abortion
        PregnancyInterruptReason.RESORPTION -> R.string.pregnancy_reason_resorption
        PregnancyInterruptReason.DISEASE -> R.string.pregnancy_reason_disease
        PregnancyInterruptReason.FEMALE_LOST -> R.string.pregnancy_reason_female_lost
        PregnancyInterruptReason.INPUT_ERROR -> R.string.pregnancy_reason_input_error
        PregnancyInterruptReason.OTHER -> R.string.pregnancy_reason_other
    }
}
