package com.zaroslikov.fermacompose2.supportFun

import com.zaroslikov.domain.models.enums.AnimalCountVersion
import com.zaroslikov.domain.models.enums.IndicationStatus

fun AnimalCountVersion.toIndicationStatus(): IndicationStatus {
    return when (this) {
        AnimalCountVersion.SALE -> IndicationStatus.SALE
        AnimalCountVersion.EXPENSES -> IndicationStatus.EXPENSES
        AnimalCountVersion.KILL -> IndicationStatus.KILL
        AnimalCountVersion.WRITE_OFF -> IndicationStatus.WRITE_OFF
        else -> IndicationStatus.ADD
    }
}