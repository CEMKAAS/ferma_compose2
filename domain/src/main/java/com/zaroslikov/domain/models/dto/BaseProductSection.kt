package com.zaroslikov.domain.models.dto

import com.zaroslikov.domain.models.enums.Suffix

interface BaseProductSection {
    val title: String
    val count: Double
    val countSuffix: Suffix
    val price: Double?
    val priceAll: Double?
}