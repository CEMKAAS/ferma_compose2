package com.zaroslikov.data.room.dto.crm

import com.zaroslikov.domain.models.enums.Suffix

data class CrmBuyerProductDto(
    val title: String,
    val count: Double,
    val suffix: Suffix,
    val total: Double,
)
