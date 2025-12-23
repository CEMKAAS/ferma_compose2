package com.zaroslikov.domain.models.table

import com.zaroslikov.domain.models.enums.Suffix

data class DomainSettings(
    val id: Long = 0,
    val currencySuffix: Suffix = Suffix.RUBLE,
    val weightSuffix: Suffix = Suffix.KILOGRAM,
    val volumeSuffix: Suffix = Suffix.LITERS,
    val linearSuffix: Suffix = Suffix.CENTIMETERS,
    val idPT: Long = 0,
)
